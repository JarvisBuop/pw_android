package com.jdev.wandroid.ui.frg

import android.os.Bundle
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R

/**
 * info: create by jd in 2019/12/10
 * @see:
 * @description: gpuimage test
 *
 * https://github.com/cats-oss/android-gpuimage
 *
 * Android filters based on OpenGL (idea from GPUImage for iOS)
 */
class GpuImageTestFrag : BaseViewStubFragment() {
    override fun getViewStubId(): Int {
        return R.layout.app_frag_gpuimage
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {


    }

}