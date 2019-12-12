package com.jdev.wandroid.ui.frg

import android.os.Bundle
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R

/**
 * info: create by jd in 2019/12/12
 * @see:
 * @description: magic camera demo
 *
 * @see https\://github.com/jameswanliu/MagicCamera_master
 *
 */
class GpuMagicCameraFrag : BaseViewStubFragment() {
    override fun getViewStubId(): Int {
        return R.layout.app_frag_magiccamera
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {


    }

}