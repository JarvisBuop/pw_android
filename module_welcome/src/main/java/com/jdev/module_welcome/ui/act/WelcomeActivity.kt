package com.jdev.module_welcome.ui.act


import android.content.Intent
import android.os.Bundle
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.jdev.kit.baseui.BaseActivity
import com.jdev.module_welcome.R

/**
 * Created by JarvisDong on 2019/3/29.
 * OverView:
 */

class WelcomeActivity : BaseActivity() {
    private lateinit var mAnimationView: LottieAnimationView


    override fun initIntentData(): Boolean {
        return true
    }

    override fun customOperate(savedInstanceState: Bundle?) {
        mAnimationView = findViewById<LottieAnimationView>(R.id.animation_view)
        mAnimationView.setAnimation(R.raw.logosmall)

        findViewById<View>(R.id.layout_go).setOnClickListener {
            startActivity(Intent(this,FullscreenActivity::class.java))
        }
    }

    override fun getViewStubId(): Int {
        return R.layout.mw_act_welcome_main
    }

}
