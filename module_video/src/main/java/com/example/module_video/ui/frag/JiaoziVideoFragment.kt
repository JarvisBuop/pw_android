package com.example.module_video.ui.frag

import android.os.Bundle
import com.jdev.kit.baseui.BaseFragment
import com.jdev.module_video.R

/**
 * info: create by jd in 2019/8/5
 * @see:
 * @description:
 *
 */
class JiaoziVideoFragment : BaseFragment() {

    override fun getViewStubId(): Int {
        return R.layout.frag_layout_jiaozi
    }

    override fun initIntentData(): Boolean {
        return true
    }

    override fun customOperate(savedInstanceState: Bundle?) {
    }

}