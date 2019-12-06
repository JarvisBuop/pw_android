package com.jdev.kit.support.popwindow

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.widget.PopupWindow
import com.jdev.kit.R


abstract class BasePopWindow : PopupWindow {

    protected lateinit var mRootView: View
    private var animateView: View? = null
    protected var mContext: Context? = null
    protected var parent: View? = null

    protected var isSupportShowAnimation = true

    companion object {

        public var SHOWDURATION = 300L
        public var DURATION = 300L

        protected var showAlphaAnimation = AnimationCreator.getAlphaAnimation(SHOWDURATION, 0f, 1f)
        protected var dismissAlphaAnimation = AnimationCreator.getAlphaAnimation(DURATION, 1f, 0f)
        protected var showScaleAnimation = AnimationCreator.getScaleAnimation(SHOWDURATION, 0.3f, 1f)
        protected var dismissScaleAnimation = AnimationCreator.getScaleAnimation(DURATION, 1f, 0.3f)

        protected var trasnLateInAnimation = AnimationCreator.getTranslateAnimationY(SHOWDURATION, 1f, 0f, true)
        protected var trasnLateOutAnimation = AnimationCreator.getTranslateAnimationY(BasePopWindow.DURATION, 0f, 1f, true)

        protected var trasnLateInAnimationX = AnimationCreator.getTranslateAnimationX(SHOWDURATION, 1f, 0f, true)
        protected var trasnLateOutAnimationX = AnimationCreator.getTranslateAnimationX(BasePopWindow.DURATION, 0f, 1f, true)
    }

    constructor(context: Context) {
        initPop(context, getLayoutId())
    }

    constructor(context: Context, layoutId: Int) {
        initPop(context, layoutId)
    }

    protected fun initPop(context: Context, layoutId: Int) {
        mContext = context
        animationStyle = R.style.adv_popwin_anim_style
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mRootView = inflater.inflate(layoutId, null)
        initView()
        contentView = mRootView
        setWH()
        if (isSupportBGDismiss()) {
            val dw = ColorDrawable(getBackDrawableColor())
            this.setBackgroundDrawable(dw)
            isOutsideTouchable = true
        }
        try {
            animateView = getAnimateView()
        } catch (e: Exception) {
            isSupportShowAnimation = false
        }

    }

    protected open fun initView() {
        parent = mRootView!!.findViewById(R.id.parent)
    }

    protected fun setWH() {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun showAtLocation(parent: View, gravity: Int, x: Int, y: Int) {
        try {
            if (mContext == null) {
                return
            }
            if (mContext is Activity && ((mContext as Activity).isFinishing || (mContext as Activity).isDestroyed)) {
                return
            }
            super.showAtLocation(parent, gravity, x, y)
            if (isSupportShowAnimation) {
                show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getView(viewId: Int): View {
        return mRootView!!.findViewById(viewId)
    }

    protected fun show() {
        val animationSet = createShowAnimation()
        if (animateView != null) {
            animateView!!.clearAnimation()
            animateView!!.startAnimation(animationSet)
        }
        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                doShowEnd()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }

    protected fun doShowEnd() {}

    protected fun createShowAnimation(): AnimationSet {
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(showAlphaAnimation)
        animationSet.addAnimation(showScaleAnimation)
        return animationSet
    }

    protected fun createHideAnimation(): AnimationSet {
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(dismissAlphaAnimation)
        animationSet.addAnimation(dismissScaleAnimation)
        return animationSet
    }

    fun dismissAfterAnimation() {
        val animSet = createHideAnimation()
        animSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                mRootView!!.post { dismiss() }
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        if (animateView != null) {
            animateView!!.clearAnimation()
            animateView!!.startAnimation(animSet)
        } else
            dismiss()
    }


    protected fun mFindViewById(id: Int): View? {
        return if (mRootView != null) {
            mRootView!!.findViewById(id)
        } else
            null
    }

    override fun showAsDropDown(anchor: View) {
        resetHeight(anchor)
        super.showAsDropDown(anchor)
    }

    open fun resetHeight(anchor: View) {
        if (Build.VERSION.SDK_INT >= 24) {
            val rect = Rect()
            anchor.getGlobalVisibleRect(rect)
            val h = anchor.resources.displayMetrics.heightPixels - rect.bottom
            height = h
        }
    }


    abstract fun getLayoutId(): Int
    abstract fun getAnimateView(): View?

    open fun isSupportBGDismiss(): Boolean {
        return true
    }

    open fun getBackDrawableColor(): Int {
        return 0xb0000000.toInt()
    }

}
