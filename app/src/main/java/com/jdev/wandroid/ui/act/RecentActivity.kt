package com.jdev.wandroid.ui.act

import android.os.Bundle
import com.jdev.kit.baseui.BaseActivity
import com.jdev.wandroid.R
import kotlinx.android.synthetic.main.app_activity_container.*

/**
 * info: create by jd in 2020/8/5
 * @see:
 * @description:
 *
 */
class RecentActivity : BaseActivity() {
    override fun getViewStubId(): Int {
        return R.layout.app_activity_container
    }

    override fun initIntentData(): Boolean {
        return true
    }

    override fun customOperate(savedInstanceState: Bundle?) {
        btn_retry?.text = "recent in document"
    }

}