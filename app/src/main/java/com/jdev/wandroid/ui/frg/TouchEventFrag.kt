package com.jdev.wandroid.ui.frg

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import com.jdev.wandroid.utils.FloatUtils
import kotlinx.android.synthetic.main.frag_test_touchevent.*

class TouchEventFrag : BaseViewStubFragment() {

    override fun getViewStubId(): Int {
        return R.layout.frag_test_touchevent
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        vg_a.setText(show)
        vg_b.setText(show)
        v_a.setText(show)
    }


}