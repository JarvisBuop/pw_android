package jp.co.cyberagent.android.gpuimage.gpureal

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.opengl.EGL14
import android.opengl.GLES20
import com.seu.magicfilter.camera.CameraEngine
import com.seu.magicfilter.encoder.video.TextureMovieEncoder
import com.seu.magicfilter.filter.advanced.MagicBeautyFilter
import com.seu.magicfilter.filter.base.MagicCameraInputFilter
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter
import com.seu.magicfilter.filter.helper.MagicFilterFactory
import com.seu.magicfilter.utils.MagicParams
import com.seu.magicfilter.utils.OpenGlUtils
import jp.co.cyberagent.android.gpuimage.GPUImageNativeLibrary
import java.io.File
import java.io.IOException
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * info: create by jd in 2019/12/20
 * @see:
 * @description:
 *
 */
class JdGpuCameraRenderer() : JdGPUImageRenderer(null), Camera.PreviewCallback {

    private var cameraInputFilter: MagicCameraInputFilter? = null
    private var beautyFilter: MagicBeautyFilter? = null

    //frame -> texture;
    private var surfaceTexture: SurfaceTexture? = null
    private var glRgbBuffer: IntBuffer? = null
    private val videoEncoder: TextureMovieEncoder = TextureMovieEncoder()
    private val outputFile: File

    private var recordingEnabled: Boolean = false
    private var recordingStatus: Int = -1

    private val RECORDING_OFF = 0
    private val RECORDING_ON = 1
    private val RECORDING_RESUMED = 2

    //todo interact
    var onFrameAvailableListener: SurfaceTexture.OnFrameAvailableListener? = null


    init {
        outputFile = File(MagicParams.videoPath, MagicParams.videoName)
    }


    override fun onSurfaceCreatedExcute(unused: GL10?, config: EGLConfig?) {
        recordingEnabled = videoEncoder.isRecording
        if (recordingEnabled) {
            recordingStatus = RECORDING_RESUMED
        } else {
            recordingStatus = RECORDING_OFF
        }

        if (cameraInputFilter == null) {
            cameraInputFilter = MagicCameraInputFilter()
        }
        cameraInputFilter?.init()

        if (glTextureId == OpenGlUtils.NO_TEXTURE) {
            glTextureId = OpenGlUtils.getExternalOESTextureID()
            if (glTextureId != OpenGlUtils.NO_TEXTURE) {
                surfaceTexture = SurfaceTexture(glTextureId)
                surfaceTexture?.setOnFrameAvailableListener(onFrameAvailableListener)
            }
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        openCamera()
    }

    override fun onDrawFrameExcute(gl: GL10?) {
        if (surfaceTexture == null)
            return
        surfaceTexture?.updateTexImage()

        if (recordingEnabled) {
            when (recordingStatus) {
                RECORDING_OFF -> {
                    val info = CameraEngine.getCameraInfo()
                    videoEncoder.setPreviewSize(info.previewWidth, info.pictureHeight)
                    videoEncoder.setTextureBuffer(glTextureBuffer)
                    videoEncoder.setCubeBuffer(glCubeBuffer)
                    videoEncoder.startRecording(TextureMovieEncoder.EncoderConfig(
                            outputFile, info.previewWidth, info.pictureHeight,
                            1000000, EGL14.eglGetCurrentContext(),
                            info))
                    recordingStatus = RECORDING_ON
                }
                RECORDING_RESUMED -> {
                    videoEncoder.updateSharedContext(EGL14.eglGetCurrentContext())
                    recordingStatus = RECORDING_ON
                }
                RECORDING_ON -> {
                }
                else -> throw RuntimeException("unknown status $recordingStatus")
            }
        } else {
            when (recordingStatus) {
                RECORDING_ON, RECORDING_RESUMED -> {
                    videoEncoder.stopRecording()
                    recordingStatus = RECORDING_OFF
                }
                RECORDING_OFF -> {
                }
                else -> throw RuntimeException("unknown status $recordingStatus")
            }
        }
        val mtx = FloatArray(16)
        surfaceTexture?.getTransformMatrix(mtx)
        cameraInputFilter?.setTextureTransformMatrix(mtx)
        var id = glTextureId
        if (filter == null) {
            cameraInputFilter?.onDrawFrame(glTextureId, glCubeBuffer, glTextureBuffer)
        } else {
            id = cameraInputFilter?.onDrawToTexture(glTextureId) ?: -1
            filter.onDrawFrame(id, glCubeBuffer, glTextureBuffer)
        }
        videoEncoder.setTextureId(id)
        videoEncoder.frameAvailable(surfaceTexture)
    }

    override fun setFilter(filter: GPUImageFilter?) {
        super.setFilter(filter)
        videoEncoder.setFilter(MagicFilterFactory.currentFilterType)
    }

    override fun onFilterChanged() {
        super.onFilterChanged()
        cameraInputFilter?.onDisplaySizeChanged(outputWidth, outputHeight)
        if (filter != null)
            cameraInputFilter?.initCameraFrameBuffer(imageWidth, imageHeight)
        else
            cameraInputFilter?.destroyFramebuffers()
    }

    @Deprecated("magic replace yuv ")
    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        val previewSize = camera.parameters.previewSize
        onPreviewFrame(data, previewSize.width, previewSize.height)
    }

    @Deprecated("magic replace yuv ")
    fun onPreviewFrame(data: ByteArray, width: Int, height: Int) {
        if (glRgbBuffer == null) {
            glRgbBuffer = IntBuffer.allocate(width * height)
        }
        if (runOnDraw.isEmpty()) {
            runOnDraw {
                GPUImageNativeLibrary.YUVtoRBGA(data, width, height, glRgbBuffer!!.array())
                glTextureId = OpenGlUtils.loadTexture(glRgbBuffer, width, height, glTextureId)

                if (imageWidth != width) {
                    imageWidth = width
                    imageHeight = height
                    adjustSize()
                }
            }
        }
    }

    @Deprecated("gpuimage use")
    fun setUpSurfaceTexture(camera: Camera) {
        runOnDraw {
            val textures = IntArray(1)
            GLES20.glGenTextures(1, textures, 0)
            surfaceTexture = SurfaceTexture(textures[0])
            try {
                camera.setPreviewTexture(surfaceTexture)
                camera.setPreviewCallback(this)
                camera.startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun openCamera() {
        if (CameraEngine.getCamera() == null)
            CameraEngine.openCamera()
        val info = CameraEngine.getCameraInfo()
        if (info.orientation == 90 || info.orientation == 270) {
            imageWidth = info.previewHeight
            imageHeight = info.previewWidth
        } else {
            imageWidth = info.previewWidth
            imageHeight = info.previewHeight
        }
        cameraInputFilter?.onInputSizeChanged(imageWidth, imageHeight)
        adjustSize(info.orientation, info.isFront, true)
        if (surfaceTexture != null) {
            CameraEngine.startPreview(surfaceTexture)
        }
    }

    fun changeRecordingState(isRecording: Boolean) {
        recordingEnabled = isRecording
    }

    fun onBeautyLevelChanged() {
        cameraInputFilter?.onBeautyLevelChanged()
    }



}