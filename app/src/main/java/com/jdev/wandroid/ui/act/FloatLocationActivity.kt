//package com.jdev.wandroid.ui.act
//
//import android.animation.ObjectAnimator
//import android.os.Bundle
//import android.view.View
//import android.view.ViewGroup
//import com.blankj.utilcode.util.ConvertUtils
//import com.jarvisdong.kotlindemo.ui.BaseActivity
//import com.jdev.wandroid.R
//import com.jdev.wandroid.utils.FloatUtils
//import com.yhao.floatwindow.Util
//import kotlinx.android.synthetic.main.app_float_item_include.*
//import kotlinx.android.synthetic.main.app_float_location.*
//import java.io.Serializable
//
///**
// * info: create by jd in 2019/12/30
// * @see:
// * @description: 点击定位的activity
// *
// * [仿微信方式一]
// *
// */
//class FloatLocationActivity : BaseActivity() {
//    private var left: Boolean = false
//    var locationParams: MyLocation? = null
//    var offsetVertical: Int = ConvertUtils.dp2px(70f)
//    val margin = ConvertUtils.dp2px(15f)
//
//    companion object {
//        val KEY_LOCATION_PARAMS = "LOCATION_PARAMS"
//    }
//
//    override fun getViewStubId(): Int {
//        return R.layout.app_float_location
//    }
//
//    override fun initIntentData(): Boolean {
//        locationParams = intent.getSerializableExtra(KEY_LOCATION_PARAMS) as MyLocation?
//        return locationParams != null
//    }
//
//    override fun customOperate(savedInstanceState: Bundle?) {
//        appbarLayout?.visibility = View.GONE
//        float_container.setOnClickListener {
//            finish()
//        }
//
//        float_img_close.setOnClickListener {
//            FloatUtils.destroy()
//            finish()
//        }
//
//        //location-margin
//        left = locationParams?.isLeft ?: false
//        var layoutParams = float_playview.layoutParams as ViewGroup.MarginLayoutParams
//        if (left) {
//            layoutParams.rightMargin = margin
//            float_playview.setBackgroundResource(R.drawable.bg_float_rightcorner)
//        } else {
//            layoutParams.leftMargin = margin
//            float_playview.setBackgroundResource(R.drawable.bg_float_leftcorner)
//        }
//        float_playview.layoutParams = layoutParams
//
//        //location-Y
//        var targetY = 0f
//        if (locationParams!!.y.toFloat() + locationParams!!.h + offsetVertical > Util.getScreenHeight(mContext)) {
//            targetY = locationParams!!.y.toFloat() - offsetVertical
//        } else {
//            targetY = locationParams!!.y.toFloat() + offsetVertical
//        }
//        float_playview.y = targetY
//
//        startAnimator()
//    }
//
//    private fun startAnimator() {
//        var startX = 0f
//        var endX = 0f
//
//        if (left) {
//            startX = (-(Util.getScreenWidth(mContext) - margin)).toFloat()
//            endX = 0f
//        } else {
//            startX = (Util.getScreenWidth(mContext) - margin).toFloat()
//            endX = 0f
//        }
//
//        var ofFloat = ObjectAnimator.ofFloat(float_playview, "translationX", startX, endX)
//        ofFloat.start()
//    }
//
//    data class MyLocation(
//            var x: Int = 0,
//            var y: Int = 0,
//            var w: Int = 0,
//            var h: Int = 0,
//            var isLeft: Boolean = false
//    ) : Serializable
//}