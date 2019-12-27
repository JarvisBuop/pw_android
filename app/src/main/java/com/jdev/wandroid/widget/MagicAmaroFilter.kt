//package com.jdev.wandroid.widget
//
//import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
//import jp.co.cyberagent.android.gpuimage.util.OpenGlUtils
//import android.opengl.GLES20
//import android.R
//import android.content.Context
//
//
///**
// * info: create by jd in 2019/12/11
// * @see:
// * @description:
// *
// */
//
//class MagicAmaroFilter(var context: Context) : GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER, OpenGlUtils.readShaderFromRawResource(R.raw.amaro)) {
//    private val inputTextureHandles = intArrayOf(-1, -1, -1)
//    private val inputTextureUniformLocations = intArrayOf(-1, -1, -1)
//    private var mGLStrengthLocation: Int = 0
//
//    override fun onDestroy() {
//        super.onDestroy()
//        GLES20.glDeleteTextures(inputTextureHandles.size, inputTextureHandles, 0)
//        for (i in inputTextureHandles.indices)
//            inputTextureHandles[i] = -1
//    }
//
//    protected fun onDrawArraysAfter() {
//        var i = 0
//        while (i < inputTextureHandles.size && inputTextureHandles[i] != OpenGlUtils.NO_TEXTURE) {
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i + 3))
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
//            i++
//        }
//    }
//
//    override fun onDrawArraysPre() {
//        var i = 0
//        while (i < inputTextureHandles.size && inputTextureHandles[i] != OpenGlUtils.NO_TEXTURE) {
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i + 3))
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureHandles[i])
//            GLES20.glUniform1i(inputTextureUniformLocations[i], i + 3)
//            i++
//        }
//    }
//
//    override fun onInit() {
//        super.onInit()
//        for (i in inputTextureUniformLocations.indices)
//            inputTextureUniformLocations[i] = GLES20.glGetUniformLocation(program, "inputImageTexture" + (2 + i))
//        mGLStrengthLocation = GLES20.glGetUniformLocation(glProgId,
//                "strength")
//    }
//
//    override fun onInitialized() {
//        super.onInitialized()
//        setFloat(mGLStrengthLocation, 1.0f)
//        runOnDraw {
//            inputTextureHandles[0] = OpenGlUtils.loadTexture(context, "filter/brannan_blowout.png")
//            inputTextureHandles[1] = OpenGlUtils.loadTexture(context, "filter/overlaymap.png")
//            inputTextureHandles[2] = OpenGlUtils.loadTexture(context, "filter/amaromap.png")
//        }
//    }
//}