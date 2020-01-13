package com.jdev.wandroid.ui.frg

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.jarvisdong.kit.baseui.BaseApp
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import com.jdev.wandroid.utils.FloatUtils
import com.yhao.floatwindow.FloatWindow


/**
 * info: create by jd in 2019/12/26
 * @see:
 * @description:
 *
 */
class FloatWindowFrag : BaseViewStubFragment() {

    override fun getViewStubDefault(): View? {
        (findView(R.id.root_coor_layout) as ViewGroup).setBackgroundColor(mContext!!.resources.getColor(R.color.green_4DC0A4))
        var linearLayout = LinearLayout(mContext)
        var textView = Button(mContext)
        textView.text = "btn_floatview"
        textView.setOnClickListener {
            FloatUtils.showFloatViewByTag()
            FloatUtils.startSendMsg()
        }
        var textView2 = Button(mContext)
        textView2.text = "btn_destroy"
        textView2.setOnClickListener {
            FloatWindow.destroy("multi")
        }

        linearLayout.addView(textView)
        linearLayout.addView(textView2)
        return linearLayout
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        FloatUtils.showFloatViewByTag()
        FloatUtils.startSendMsg()
    }


}