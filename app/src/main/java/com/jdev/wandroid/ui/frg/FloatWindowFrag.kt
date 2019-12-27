package com.jdev.wandroid.ui.frg

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.jdev.kit.baseui.BaseViewStubFragment

/**
 * info: create by jd in 2019/12/26
 * @see:
 * @description:
 *
 */
class FloatWindowFrag : BaseViewStubFragment() {

    override fun getViewStubDefault(): View? {
        var textView = Button(mContext)
        textView.text = "btn_floatview"

        textView.setOnClickListener {

        }
        return textView
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {

    }


}