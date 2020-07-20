package com.jdev.wandroid.ui.frg

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.utils.media.MediaUtils
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

class MediaMuxerTestFrag : BaseViewStubFragment() {
    private var mediaCodec: MediaCodec? = null

    companion object {
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
            text = "合成视频 = Frame + Audio"
            setOnClickListener {
                mergeVideoByParts()
            }
        })

        linearLayout.addView(Button(mContext).apply {
            text = "bitmap图片转为视频"
            setOnClickListener {
                mergeVideoByBitmaps()
            }
        })
        return linearLayout
    }

    //bitmaps -> video
    private fun mergeVideoByBitmaps() {
        initMediaCodec()


//        var mediaMuxer = MediaMuxer(mergeBitmapsOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
//        mediaMuxer.addTrack()

    }

    private fun initMediaCodec() {
        var width = 640
        var height = 480
        var bitRate = 1300 * 1000
        var frameRate = 24
        var interval = 2
        var mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaUtils.getColorFormat())
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, interval)

//        mediaCodec = MediaCodec.createByCodecName(MediaFormat.MIMETYPE_VIDEO_AVC)
//        var mOutputFormat: MediaFormat?=null
//        mediaCodec?.setCallback(object:MediaCodec.Callback(){
//            override fun onOutputBufferAvailable(codec: MediaCodec, outputBufferId: Int, info: MediaCodec.BufferInfo) {
//                var outputBuffer = codec.getOutputBuffer(outputBufferId)
//                var bufferFormat = codec.getOutputFormat(outputBufferId)
//
//                codec.releaseOutputBuffer(outputBufferId, )
//            }
//
//            override fun onInputBufferAvailable(codec: MediaCodec, inputBufferId: Int) {
//                var inputBuffer = codec.getInputBuffer(inputBufferId)
//
//                codec.queueInputBuffer(inputBufferId, )
//            }
//
//            override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
//                mOutputFormat = format
//            }
//
//            override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
//            }
//
//        })
//        mediaCodec?.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
//        mOutputFormat = mediaCodec?.outputFormat
//        mediaCodec?.start()

        mediaCodec = MediaCodec.createByCodecName(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec?.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        var outputFormat = mediaCodec?.outputFormat
        mediaCodec?.start()
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
        var fileDir = File("$SDCARD_PATH")
        var file = File("$inputPath")
        if (file.exists()) {
            file.delete()
        }
        if (!fileDir.exists()) {
            fileDir.mkdirs()
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