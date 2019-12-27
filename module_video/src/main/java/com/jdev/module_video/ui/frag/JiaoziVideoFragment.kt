package com.jdev.module_video.ui.frag

import android.os.Bundle
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.module_video.R

/**
 * info: create by jd in 2019/8/5
 * @see:
 * @description:
 *
 */
class JiaoziVideoFragment : BaseViewStubFragment() {

    override fun getViewStubId(): Int {
        return R.layout.mv_frag_layout_jiaozi
    }

    override fun initIntentData(): Boolean {
        return true
    }

    override fun customOperate(savedInstanceState: Bundle?) {
    }

}