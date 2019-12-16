package com.jdev.wandroid.ui.frg

import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.os.Bundle
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import com.jdev.wandroid.ui.opengl.MyRender
import kotlinx.android.synthetic.main.app_frag_simple_opengl_demo.*

/**
 * info: create by jd in 2019/12/16
 * @see:
 * @description:
 *
 */
class SimpleOpenglDemoFrag : BaseViewStubFragment() {
    override fun getViewStubId(): Int {
        return R.layout.app_frag_simple_opengl_demo
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
//        glSurfaceview.setEGLContextClientVersion(2)
//        glSurfaceview.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        glSurfaceview.setRenderer(MyRender())
        glSurfaceview.getHolder().setFormat(PixelFormat.RGBA_8888)
        glSurfaceview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY)
    }

}