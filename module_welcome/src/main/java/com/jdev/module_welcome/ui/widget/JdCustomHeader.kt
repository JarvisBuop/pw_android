package com.jdev.module_welcome.ui.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.jdev.module_welcome.R
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle

/**
 * info: create by jd in 2019/7/9
 * @see:
 * @description:
 *
 */
class JdCustomHeader : ConstraintLayout, RefreshHeader {
    val TAG = "JdCustomHeader"
    var bgView: ImageView
    var bgPicView: View
    var txtTips: TextView

    lateinit var foreverAnim: ObjectAnimator

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_refresh_header, this, true)
        bgView = findViewById<ImageView>(R.id.img_bg_view)
        bgPicView = findViewById<View>(R.id.img_person_view)
        txtTips = findViewById<TextView>(R.id.text_tips)
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate
    }

    //延迟弹回;
    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        //刷新完;
        Log.e(TAG, "onFinish")
        if (success) {
            txtTips.setText("刷新完成")
        } else {
            txtTips.setText("刷新失败")
        }

        stopAnimator()
        return 2000
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
        Log.e(TAG, "onInitialized")
    }


    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        //已释放
        Log.e(TAG, "onReleased")
    }


    override fun getView(): View {
        return this
    }

    override fun setPrimaryColors(vararg colors: Int) {
        Log.e(TAG, "setPrimaryColors")
    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        //释放后播放动画;
        Log.e(TAG, "onStartAnimator")


        startAnimator(false)
        foreverAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                startAnimator(false)
            }
        })
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        Log.e(TAG, "onStateChanged " + newState)
        when (newState) {
            RefreshState.None, RefreshState.PullDownToRefresh -> {
                //下拉开始刷新;
                txtTips.setText("下拉开始刷新")
            }
            RefreshState.Refreshing -> {
                //正在刷新;
                txtTips.setText("正在刷新")
            }
            RefreshState.ReleaseToRefresh -> {
                //释放立即刷新;
                txtTips.setText("释放立即刷新")
            }

        }
    }

    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {
        Log.e(TAG, "onMoving" + isDragging + " p: " + percent + " offset: " + offset + " height: " + height + " max: " + maxDragHeight)

        //滑动旋转
        bgView.rotation = offset.toFloat()

        //滑动大小
        var fration = offset * 1.0f / height
        setTargetScale(fration, bgView)
        setTargetScale(fration, bgPicView)
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
        Log.e(TAG, "onHorizontalDrag")
    }


    //-----------------------------------------

    fun startAnimator(isUp: Boolean) {
        var start = 0f
        var end = 360f
        if (isUp) {
            start = 0f
            end = 360f
        } else {
            start = 0f
            end = -360f
        }
        foreverAnim = ObjectAnimator.ofFloat(bgView, "rotation", start, end)
        foreverAnim.interpolator = LinearInterpolator()
        foreverAnim.duration = 1500
        foreverAnim.start()
    }

    fun stopAnimator(){
        if(this::foreverAnim.isInitialized){
            foreverAnim.cancel()
        }
    }

    fun setTargetScale(fration: Float, targetView: View) {
//        var floatEvaluator = FloatEvaluator()
//        var bgViewWidth = ConvertUtils.dp2px(floatMaxParams)
//        var evaluate1 = floatEvaluator.evaluate(fration, bgViewWidth * 0.2f, bgViewWidth)

//        LogUtils.e(TAG,"evaluate: "+ fration)
//        var layoutParams1 = targetView.layoutParams
//        layoutParams1.width = evaluate1.toInt()
//        layoutParams1.height = evaluate1.toInt()
//        targetView.layoutParams = layoutParams1
        var scale = fration
        if (scale < 0.2f) {
            scale = 0.2f
        } else if (scale > 1f) {
            scale = 1f
        }
        targetView.scaleX = scale
        targetView.scaleY = scale
    }
}