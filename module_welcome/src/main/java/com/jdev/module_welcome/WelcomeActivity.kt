package com.jdev.module_welcome

import android.os.Bundle
import com.jarvisdong.kit.baseui.BaseApp
import com.jarvisdong.kotlindemo.ui.BaseActivity

/**
 * Created by JarvisDong on 2019/3/29.
 * OverView:
 */

class WelcomeActivity: BaseActivity(){

    override fun initIntentData(): Boolean {
        return true
    }

    override fun customOperate(savedInstanceState: Bundle?) {

    }

    override fun getViewStubId(): Int {
        return R.layout.act_welcome_main
    }

}
