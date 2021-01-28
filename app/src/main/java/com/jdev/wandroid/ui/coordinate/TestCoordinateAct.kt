package com.zt.base.debug.coordinate

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jdev.kit.baseui.BaseActivity
import com.jdev.wandroid.R
import kotlinx.android.synthetic.main.app_test_coordinate.*

class TestCoordinateAct : BaseActivity() {

    companion object {
        fun launch(context: Context) {
            context.apply {
                startActivity(Intent(this, TestCoordinateAct::class.java))
            }
        }
    }


    override fun getViewStubId(): Int {
        return R.layout.app_test_coordinate
    }

    override fun initIntentData(): Boolean {
        return true
    }

    override fun customOperate(savedInstanceState: Bundle?) {
        viewTop.behavior = TopBehavior()
        viewBottom.behavior = BottomBehavior()
//        for (i in 0..100) {
//            dv_container.addView(TextView(mContext).apply {
//                text = "test$i"
//            })
//        }
    }
}