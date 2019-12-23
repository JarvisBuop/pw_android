package com.example.lib_imagefilter.widget

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.opengl.EGL14
import android.opengl.GLES20
import com.example.lib_imagefilter.camera.CameraEngine
import com.example.lib_imagefilter.encoder.video.TextureMovieEncoder
import com.example.lib_imagefilter.filter.base.MagicCameraInputFilter
import com.example.lib_imagefilter.filter.base.gpuimage.GPUImageFilter
import com.example.lib_imagefilter.utils.MagicParams
import com.example.lib_imagefilter.utils.OpenGlUtils
import com.seu.magicfilter.filter.helper.MagicFilterFactory
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
class JdGpuCameraRenderer() : JdGPUImageRenderer(null)/*, Camera.PreviewCallback*/ {

    private var cameraInputFilter: MagicCameraInputFilter? = null
    //todo
//    private var beautyFilter: MagicBeautyFilter? = null

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

    @Deprecated("gpuimage use")
    fun setUpSurfaceTexture(camera: Camera) {
        runOnDraw {
            val textures = IntArray(1)
            GLES20.glGenTextures(1, textures, 0)
            surfaceTexture = SurfaceTexture(textures[0])
            try {
                camera.setPreviewTexture(surfaceTexture)
                camera.setPreviewCallback(null)
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