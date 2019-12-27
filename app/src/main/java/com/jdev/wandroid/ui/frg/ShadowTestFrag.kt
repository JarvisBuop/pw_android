package com.jdev.wandroid.ui.frg

import android.os.Bundle
import com.jdev.kit.baseui.BaseFragment
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import com.jdev.wandroid.test.TestMath
import kotlinx.android.synthetic.main.app_frag_show_customview.*

/**
 * info: create by jd in 2019/12/9
 * @see:
 * @description:
 *
 */
class ShadowTestFrag : BaseViewStubFragment() {
    override fun getViewStubId(): Int {
        return R.layout.app_frag_show_customview
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        quickContactBadge.assignContactFromPhone("13817228124", false)

        wrapper_shadow.setOnClickListener {
            TestMath.testInteger()
        }
    }

}