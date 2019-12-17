package com.jdev.wandroid.ui.frg

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import com.blankj.utilcode.util.LogUtils
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import com.jdev.wandroid.ui.opengl.MyG20Render
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
        //gl10
//        glSurfaceview.setRenderer(MyRender())
        var myG20Render = MyG20Render()
        //gl20
        glSurfaceview.setEGLContextClientVersion(2)
        glSurfaceview.setRenderer(myG20Render)
        var supportsEs2 =
                (mContext!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                        .deviceConfigurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        LogUtils.e(" issupport es2: $supportsEs2")
    }

}