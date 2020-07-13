package com.jdev.wandroid.ui.frg

import android.os.Bundle
import android.view.View
import android.widget.QuickContactBadge
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
    lateinit var quickContactBadge: QuickContactBadge
    lateinit var wrapper_shadow: View
    override fun getViewStubId(): Int {
        return R.layout.app_frag_show_customview
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        quickContactBadge = findView(R.id.quickContactBadge)
        wrapper_shadow = findView(R.id.wrapper_shadow)
        quickContactBadge.assignContactFromPhone("13817228124", false)

        wrapper_shadow.setOnClickListener {
            TestMath.testInteger()
        }
    }

}