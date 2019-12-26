package com.jdev.wandroid.ui.frg

import android.annotation.SuppressLint
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
    @SuppressLint("SetTextI18n")
    override fun getViewStubDefault(): View? {
        var btn = Button(mContext)
        btn.text = "show window"
        btn.setOnClickListener {

        }
        return btn
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
    }

}