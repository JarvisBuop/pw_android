//package com.jdev.module_welcome
//
//import android.os.Bundle
//import com.airbnb.lottie.LottieAnimationView
//import com.jarvisdong.kotlindemo.ui.BaseActivity
//
///**
// * Created by JarvisDong on 2019/3/29.
// * OverView:
// */
//
//class WelcomeActivity : BaseActivity() {
//    private lateinit var mAnimationView: LottieAnimationView
//
//
//    override fun initIntentData(): Boolean {
//        return true
//    }
//
//    override fun customOperate(savedInstanceState: Bundle?) {
//        mAnimationView = findViewById<LottieAnimationView>(R.id.animation_view)
//
//        mAnimationView.setAnimation(R.raw.logosmall)
//    }
//
//    override fun getViewStubId(): Int {
//        return R.layout.act_welcome_main
//    }
//
//}
