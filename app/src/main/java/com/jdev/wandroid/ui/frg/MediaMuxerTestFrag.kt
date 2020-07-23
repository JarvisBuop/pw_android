package com.jdev.wandroid.ui.frg

import android.graphics.BitmapFactory
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import com.jdev.wandroid.utils.media.AvcEncoder
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

class MediaMuxerTestFrag : BaseViewStubFragment() {
    private var isRunning: Boolean = false
    private var mediaCodec: MediaCodec? = null
    private var mediaMuxer: MediaMuxer? = null

    private var arr = arrayListOf<Int>(
            R.drawable.filter_thumb_antique,
            R.drawable.filter_thumb_amoro,
            R.drawable.filter_thumb_beauty,
            R.drawable.filter_thumb_brannan
    )

    companion object {
        private var mMuxerStarted = false
        private var mTrackIndex = 0

        private val mInputVideoPath: String = "/pwandroid/input.mp4"
        private val mOutputVideoPath: String = "/pwandroid/output.mp4"
        private val SDCARD_PATH = android.os.Environment.getExternalStorageDirectory().path

        val inputPath = "$SDCARD_PATH$mInputVideoPath"

        //audio + Frame ->video
        val mergeOutputPath = "$SDCARD_PATH$mOutputVideoPath"

        //split frame
        val outputFramePath = "$SDCARD_PATH/pwandroid/outputFrame"

        //split audio
        val outputAudioPath = "$SDCARD_PATH/pwandroid/outputAudio"

        //bitmaps -> video
        val mergeBitmapsOutputPath = "$SDCARD_PATH/pwandroid/outputBitmaps"

        fun newInstance(): MediaMuxerTestFrag {
            return MediaMuxerTestFrag()
        }
    }

    override fun getViewStubDefault(): View? {
        var linearLayout = LinearLayout(mContext).apply {
            orientation = LinearLayout.VERTICAL
        }
        linearLayout.addView(Button(mContext).apply {
            text = "加载input视频到本地"
            setOnClickListener {
                loadVideo2SdCard()
            }
        })

        linearLayout.addView(Button(mContext).apply {
            text = "分离视频 = Frame "
            setOnClickListener {
                splitPartsByOriginVideo(0)
            }
        })

        linearLayout.addView(Button(mContext).apply {
            text = "分离视频 = Audio"
            setOnClickListener {
                splitPartsByOriginVideo(1)
            }
        })

        linearLayout.addView(Button(mContext).apply {
            text = "合成视频 = Frame + Audio"
            setOnClickListener {
                mergeVideoByParts()
            }
        })

        linearLayout.addView(Button(mContext).apply {
            text = "todo - bitmap图片转为视频"
            setOnClickListener {
                mergeVideoByBitmaps()
            }
        })
        return linearLayout
    }

    //todo  bitmaps -> video
    private fun mergeVideoByBitmaps() {
        try {
            initMediaCodec()
            initMediaMuxer()
        } finally {
            releaseMediaCodec()
        }
    }

    private fun computePresentationTime(frameIndex: Int): Long {
        return (132 + frameIndex * 1000000 / 24).toLong()
    }

    private fun getSize(size: Int): Int {
        return size / 4 * 4
    }

    private fun initMediaMuxer() {
        mediaMuxer = MediaMuxer(mergeBitmapsOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        val TIMEOUT_USEC: Long = 10000
        val info = MediaCodec.BufferInfo()
        var buffers: Array<ByteBuffer?>? = null
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            buffers = mediaCodec!!.inputBuffers
        }

        while (isRunning) {
            for (i in 0..3) {
                var inputBufferId = mediaCodec!!.dequeueInputBuffer(TIMEOUT_USEC)
                if (inputBufferId > 0) {
                    var bitmap = BitmapFactory.decodeResource(mContext!!.resources, arr[i])
                    val ptsUsec: Long = computePresentationTime(i)
                    if (i >= 3) {
                        mediaCodec!!.queueInputBuffer(inputBufferId, 0, 0, ptsUsec, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        isRunning = false
                        drainEncoder(true, info)
                    } else {
                        val input: ByteArray = AvcEncoder.getNV12(getSize(bitmap.width), getSize(bitmap.height), bitmap)
                        //有效的空的缓存区
                        var inputBuffer: ByteBuffer? = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                            buffers!![inputBufferId]
                        } else {
                            mediaCodec!!.getInputBuffer(inputBufferId) //inputBuffers[inputBufferIndex];
                        }
                        inputBuffer!!.clear()
                        inputBuffer.put(input)
                        //将数据放到编码队列
                        mediaCodec!!.queueInputBuffer(inputBufferId, 0, input.size, ptsUsec, 0)
                        drainEncoder(false, info)
                    }
                }
            }
        }
    }

    private fun drainEncoder(endOfStream: Boolean, bufferInfo: MediaCodec.BufferInfo) {
        val TIMEOUT_USEC = 10000
        var buffers: Array<ByteBuffer?>? = null
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            buffers = mediaCodec!!.outputBuffers
        }
        if (endOfStream) {
            try {
                mediaCodec!!.signalEndOfInputStream()
            } catch (e: java.lang.Exception) {
            }
        }
        while (true) {
            val encoderStatus = mediaCodec!!.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC.toLong())
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream) {
                    break // out of while
                } else {
                    Log.i(TAG, "no output available, spinning to await EOS")
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (mMuxerStarted) {
                    throw RuntimeException("format changed twice")
                }
                val mediaFormat = mediaCodec!!.outputFormat
                mTrackIndex = mediaMuxer!!.addTrack(mediaFormat)
                mediaMuxer!!.start()
                mMuxerStarted = true
            } else if (encoderStatus < 0) {
                Log.i(TAG, "unexpected result from encoder.dequeueOutputBuffer: $encoderStatus")
            } else {
                var outputBuffer: ByteBuffer? = null
                outputBuffer = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    buffers!![encoderStatus]
                } else {
                    mediaCodec!!.getOutputBuffer(encoderStatus)
                }
                if (outputBuffer == null) {
                    throw RuntimeException("encoderOutputBuffer "
                            + encoderStatus + " was null")
                }
                if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                    Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG")
                    bufferInfo.size = 0
                }
                if (bufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw RuntimeException("muxer hasn't started")
                    }

                    // adjust the ByteBuffer values to match BufferInfo
                    outputBuffer.position(bufferInfo.offset)
                    outputBuffer.limit(bufferInfo.offset + bufferInfo.size)
                    Log.d(TAG, "BufferInfo: " + bufferInfo.offset + ","
                            + bufferInfo.size + ","
                            + bufferInfo.presentationTimeUs)
                    try {
                        mediaMuxer!!.writeSampleData(mTrackIndex, outputBuffer, bufferInfo)
                    } catch (e: java.lang.Exception) {
                        Log.i(TAG, "Too many frames")
                    }
                }
                mediaCodec!!.releaseOutputBuffer(encoderStatus, false)
                if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    if (!endOfStream) {
                        Log.i(TAG, "reached end of stream unexpectedly")
                    } else {
                        Log.i(TAG, "end of stream reached")
                    }
                    break // out of while
                }
            }
        }
    }

    private fun initMediaCodec() {
        var bitmap = BitmapFactory.decodeResource(mContext!!.resources, arr[0])
        var width = 640
        var height = 480
        var bitRate = 4000000
        var frameRate = 4
        var interval = 5

        var mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, AvcEncoder.getColorFormat())
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, interval)

//        对于planar的YUV格式，先连续存储所有像素点的Y，紧接着存储所有像素点的U，随后是所有像素点的V。
//        对于packed的YUV格式，每个像素点的Y,U,V是连续交*存储的。
        //createByCodecName
        mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec?.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        var outputFormat = mediaCodec?.outputFormat
        mediaCodec?.start()

        isRunning = true;
//        while (true) {
//            var inputBufferId = mediaCodec?.dequeueInputBuffer(timeoutUs);
//            if (inputBufferId >= 0) {
//                ByteBuffer inputBuffer = mediaCodec?.getInputBuffer(…);
//                // fill inputBuffer with valid data
//                …
//                mediaCodec.queueInputBuffer(inputBufferId, …);
//            }
//            int outputBufferId = mediaCodec.dequeueOutputBuffer(…);
//            if (outputBufferId >= 0) {
//                ByteBuffer outputBuffer = mediaCodec.getOutputBuffer(outputBufferId);
//                MediaFormat bufferFormat = mediaCodec.getOutputFormat(outputBufferId); // option A
//                // bufferFormat is identical to outputFormat
//                // outputBuffer is ready to be processed or rendered.
//                …
//                mediaCodec.releaseOutputBuffer(outputBufferId, …);
//            } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                // Subsequent data will conform to new format.
//                // Can ignore if using getOutputFormat(outputBufferId)
//                outputFormat = mediaCodec.getOutputFormat(); // option B
//            }
//        }

//        releaseMediaCodec()
    }

    private fun releaseMediaCodec() {
        if (mediaCodec != null) {
            mediaCodec?.stop()
            mediaCodec?.release()
            mediaCodec = null
        }
    }

    private fun mergeVideoByParts() {
        if (!isValidPartsPath()) return

        var mediaExtractor = MediaExtractor()
        var audioExtractor = MediaExtractor()

        mediaExtractor.setDataSource(outputFramePath)
        audioExtractor.setDataSource(outputAudioPath)

        var trackFrameCount = mediaExtractor.trackCount
        var trackAudioCount = audioExtractor.trackCount

        var frameTrackIndex = -1
        var audioTrackIndex = -1
        for (i in 0 until trackFrameCount) {
            var trackFormat = mediaExtractor.getTrackFormat(i)
            if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("video/")) {
                frameTrackIndex = i
            }
        }
        for (i in 0 until trackAudioCount) {
            var trackFormat = audioExtractor.getTrackFormat(i)
            if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                audioTrackIndex = i
            }
        }
        if (frameTrackIndex <= -1 || audioTrackIndex <= -1) {
            throw IllegalArgumentException("not found target index, frameTrackIndex: $frameTrackIndex, audioTrackIndex: $audioTrackIndex")
            return
        }
        mediaExtractor.selectTrack(frameTrackIndex)
        audioExtractor.selectTrack(audioTrackIndex)

        var mediaMuxer = MediaMuxer(mergeOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        var trackFrameWriteIndex = mediaMuxer.addTrack(mediaExtractor.getTrackFormat(frameTrackIndex))
        var trackAudioWriteIndex = mediaMuxer.addTrack(audioExtractor.getTrackFormat(audioTrackIndex))
        mediaMuxer.start()

        //buffer
        val bufferInfo = MediaCodec.BufferInfo()
        var inputBuffer = ByteBuffer.allocate(500 * 1024)

        var frameFinished: Boolean = false
        while (!frameFinished) {
            var readSampleData = mediaExtractor.readSampleData(inputBuffer, 0)
            frameFinished = readSampleData < 0

            if (!frameFinished) {
                bufferInfo.size = readSampleData
                bufferInfo.flags = mediaExtractor.sampleFlags
                bufferInfo.offset = 0
                bufferInfo.presentationTimeUs = mediaExtractor.sampleTime

                mediaExtractor.advance()
                mediaMuxer.writeSampleData(trackFrameWriteIndex, inputBuffer, bufferInfo)
            }
        }

        var audioFinished: Boolean = false
        while (!audioFinished) {
            var readSampleData = audioExtractor.readSampleData(inputBuffer, 0)
            audioFinished = readSampleData < 0

            if (!audioFinished) {
                bufferInfo.size = readSampleData
                bufferInfo.flags = audioExtractor.sampleFlags
                bufferInfo.offset = 0
                bufferInfo.presentationTimeUs = audioExtractor.sampleTime

                audioExtractor.advance()
                mediaMuxer.writeSampleData(trackAudioWriteIndex, inputBuffer, bufferInfo)
            }
        }
        ToastUtils.showShort("complete")
        mediaMuxer.stop()
        mediaMuxer.release()
        mediaExtractor.release()
        audioExtractor.release()
    }

    //0 frame
    //1 audio
    private fun splitPartsByOriginVideo(selectIndex: Int = 0) {
        if (!isValidInputPath()) return
        var isAudioSample = selectIndex == 1

        //抽取媒体类
        var mediaExtractor = MediaExtractor()
        //设置数据源
        mediaExtractor.setDataSource(inputPath)
        //数据源信道
        var trackCount = mediaExtractor.trackCount
        //记录信道索引
        var frameIndex = -1
        var audioIndex = -1
        for (i in 0 until trackCount) {
            var trackFormat = mediaExtractor.getTrackFormat(i)
            if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("video/")) {
                frameIndex = i
            }
            if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                audioIndex = i
            }
        }
        //当前分离信道
        var currentTrack = if (isAudioSample) audioIndex else frameIndex
        if (currentTrack <= -1) {
            throw IllegalArgumentException("not found target index $currentTrack,should be gt 0")
            return
        }
        //混合流输入文件路径;
        var outputPath = if (isAudioSample) outputAudioPath else outputFramePath

        //设置选择某个信道进行抽取;
        mediaExtractor.selectTrack(currentTrack)
        //媒体混合器
        var mediaMuxer = MediaMuxer("$outputPath", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        //添加支持的信道;
        var writeTrackIndex = mediaMuxer.addTrack(mediaExtractor.getTrackFormat(currentTrack))
        //开始取样;
        mediaMuxer.start()

        //buffer
        var inputBuffer = ByteBuffer.allocate(500 * 1024)
        var bufferInfo = MediaCodec.BufferInfo()

        var finished: Boolean = false
        while (!finished) {
            //从指定的信道读取数据;
            var readSampleData = mediaExtractor.readSampleData(inputBuffer, 0)
            finished = readSampleData < 0

            if (!finished) {
                var sampleTime = mediaExtractor.sampleTime
                LogUtils.e("process...  $sampleTime  $readSampleData")
                bufferInfo.size = readSampleData
                bufferInfo.flags = mediaExtractor.sampleFlags
                bufferInfo.offset = 0
                bufferInfo.presentationTimeUs = sampleTime

                //下一个采样提前处理
                mediaExtractor.advance()
                //写入到指定的信道混合流中
                mediaMuxer.writeSampleData(writeTrackIndex, inputBuffer, bufferInfo)
            }
        }
        ToastUtils.showShort("complete")
        mediaMuxer.stop()
        mediaMuxer.release()
        mediaExtractor.release()
    }

    //将asset 中的视频文件复制到 sd卡中;
    private fun loadVideo2SdCard() {
        var open = mContext!!.resources.assets.open("input.mp4")
        var file = File("$inputPath")
        if (file.exists()) {
            file.delete()
        }
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        if (!file.exists()) {
            file.createNewFile()
        }
        var fileOutputStream = FileOutputStream(file)
        open.use {
            var bytes = ByteArray(1024)
            while (true) {
                if (it.read(bytes) != -1) fileOutputStream.write(bytes) else {
                    ToastUtils.showShort("success")
                    break
                }
            }
        }
    }

    private fun isValidInputPath(): Boolean {
        if (!File(inputPath).exists()) {
            ToastUtils.showShort("源文件 input.mp4 不存在")
            return false
        }
        return true
    }

    private fun isValidPartsPath(): Boolean {
        if (!File(outputAudioPath).exists() || !File(outputFramePath).exists()) {
            ToastUtils.showShort("碎片视频 或 碎片音频 文件不存在")
            return false
        }
        return true
    }

    override fun customOperate(savedInstanceState: Bundle?) {

    }
}