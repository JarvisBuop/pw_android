package com.jdev.wandroid.popwindow.impl

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.jarvisdong.kit.utils.ResourceIdUtils
import com.jdev.wandroid.popwindow.BasicFunctionPopWindow
import com.jdev.wandroid.utils.ViewUtils
import com.jdev.wandroid.R

/**
 * info: create by jd in 2019/6/14
 * @see:
 * @description: 主页 新版功能修改的引导;
 *
 */
class KtVersionMainPop(context: Context, var callback: ViewUtils.OnCallback<*>) : BasicFunctionPopWindow(context) {
    lateinit var image1: ImageView
    lateinit var image2: ImageView
    lateinit var image3: ImageView
    lateinit var image4: ImageView
    lateinit var image5: ImageView
    lateinit var tipsView: View
    lateinit var tipsTxt: TextView
    lateinit var layoutMenu: View


    var duration: Long = 2000L


    var maxTranY: Float = ConvertUtils.dp2px(200f).toFloat()

    var markHeight: Int = 0
    var markWidth: Int = 0

    var offsetMargin: Int = ConvertUtils.dp2px(10f)

    override fun getLayoutId(): Int = R.layout.pop_version_main

    override fun isSupportBGDismiss(): Boolean {
        return true
    }

    override fun initialViews() {
        super.initialViews()
        image1 = mRootView.findViewById(R.id.version_icon_1)
        image2 = mRootView.findViewById(R.id.version_icon_2)
        image3 = mRootView.findViewById(R.id.version_icon_3)
        image4 = mRootView.findViewById(R.id.version_icon_4)
        image5 = mRootView.findViewById(R.id.version_icon_5)
        tipsView = mRootView.findViewById(R.id.version_icon4)
        tipsTxt = mRootView.findViewById(R.id.version_name)
        layoutMenu = mRootView.findViewById(R.id.layout_root_menu)

        var makeMeasureSpecW = View.MeasureSpec.makeMeasureSpec(ConvertUtils.dp2px(215f), View.MeasureSpec.EXACTLY)
        var makeMeasureSpecH = View.MeasureSpec.makeMeasureSpec(ConvertUtils.dp2px(90f), View.MeasureSpec.EXACTLY)
        tipsView.measure(makeMeasureSpecW, makeMeasureSpecH)
        markHeight = tipsView.measuredHeight
        markWidth = tipsView.measuredWidth
        createAnimatorForVersion()


        setOnDismissListener {
            if (callback != null) {
                callback.callback(null)
            }
        }
    }

    /**
     * 动画:
     *
     * (1)会员Tab4 向上移动,在向右移出;   隐藏会员tab4
     *
     * (2)商城tab2 向右移动;   隐藏商城tab2,显示tab4,设置tab4为商城图片;
     *
     * (3)资源库tab2上方向右移进,向下移动至商城tab2;   显示tab2,设置tab2为资源库图片;
     *
     *
     */
    private fun createAnimatorForVersion() {
        tipsView.postDelayed({
            tipsView.visibility = View.GONE
            ViewUtils.setImageRes(image2, R.drawable.menu_icon_search)
            ViewUtils.setImageRes(image4, R.drawable.menu_icon_mem)

            createAnimatorForFirst(image4, AnimatorSet())
        }, 1000)
    }

    private fun createAnimatorForFirst(targetView: View, animatorSet: AnimatorSet) {
        var locationPoint = getLocationPoint(targetView)
        var firstAnim1 = ObjectAnimator.ofFloat(targetView, "translationY", 0f, -maxTranY)
        firstAnim1.interpolator = DecelerateInterpolator()
        firstAnim1.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)

                tipsView.visibility = View.VISIBLE
                ViewUtils.setMsgIntoView(ResourceIdUtils.getStringById(R.string.app_version_main_1), tipsTxt)
                var layoutParams = tipsView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.leftMargin = targetView.x.toInt() - markWidth / 2
                layoutParams.topMargin = getHeightForAnim(targetView)
                tipsView.layoutParams = layoutParams
            }
        })

        var animX: Float = (ScreenUtils.getScreenWidth() - locationPoint[0]).toFloat()
        var firstAnim2 = ObjectAnimator.ofFloat(targetView, "translationX", 0f, animX)
        firstAnim2.interpolator = AnticipateInterpolator()
        firstAnim2.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
//                tipsView.visibility = View.GONE
//                tipsView.left = 0
//                tipsView.top = 0
            }
        })


        animatorSet.playSequentially(firstAnim1, firstAnim2)
        animatorSet.setDuration(duration)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                image4.visibility = View.VISIBLE
                ViewUtils.setImageRes(image4, R.drawable.menu_icon_mem)
                revertImage(image4)
                image4.bringToFront()
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                image4.visibility = View.INVISIBLE
                revertImage(image4)

                createAnimatorForSecond(image2, AnimatorSet())
            }
        })
        animatorSet.startDelay = 500
        animatorSet.start()
    }

    private fun getHeightForAnim(targetView: View) =
            (ScreenUtils.getScreenHeight() - BarUtils.getStatusBarHeight() - Math.abs(targetView.y) - layoutMenu.measuredHeight - markHeight - offsetMargin).toInt()

    fun revertImage(view: View) {
        view.translationX = 0F
        view.translationY = 0F
    }

    private fun createAnimatorForSecond(targetView: ImageView, animatorSet: AnimatorSet) {
        var locationPoint = getLocationPoint(targetView)
        var animX = (locationPoint[0] + image2.measuredHeight * 2).toFloat()
        var secondAnim1 = ObjectAnimator.ofFloat(targetView, "translationX", 0f, animX)
        secondAnim1.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)

                tipsView.visibility = View.VISIBLE
                ViewUtils.setMsgIntoView(ResourceIdUtils.getStringById(R.string.app_version_main_2), tipsTxt)
                var layoutParams = tipsView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.leftMargin = ScreenUtils.getScreenWidth() - markWidth
                layoutParams.topMargin = (ScreenUtils.getScreenHeight() - BarUtils.getStatusBarHeight() - (layoutMenu.measuredHeight + markHeight) - offsetMargin).toInt()
                tipsView.layoutParams = layoutParams
            }
        })

        animatorSet.playSequentially(secondAnim1)
        animatorSet.setDuration(duration)
        animatorSet.interpolator = LinearInterpolator()
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                image2.visibility = View.VISIBLE
                ViewUtils.setImageRes(image2, R.drawable.menu_icon_search)
                revertImage(image2)
                image2.bringToFront()
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                image2.visibility = View.INVISIBLE


                image4.visibility = View.VISIBLE
                ViewUtils.setImageRes(image4, R.drawable.menu_icon_search)
                revertImage(image4)

                createAnimatorForThird(image2, AnimatorSet())
            }
        })
        animatorSet.startDelay = 500
        animatorSet.start()
    }

    fun revertImage(view: View, x: Float, y: Float) {
        view.translationX = x
        view.translationY = y
    }

    private fun createAnimatorForThird(targetView: ImageView, animatorSet: AnimatorSet) {
        var locationPoint = getLocationPoint(targetView)

        var animX = (locationPoint[0] - image2.measuredHeight * 2).toFloat()
        var thirdAnim1 = ObjectAnimator.ofFloat(targetView, "translationX", -animX, 0f)
        thirdAnim1.interpolator = DecelerateInterpolator()
        thirdAnim1.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)

                tipsView.visibility = View.VISIBLE
                ViewUtils.setMsgIntoView(ResourceIdUtils.getStringById(R.string.app_version_main_3), tipsTxt)
                var layoutParams = tipsView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.leftMargin = 0
                layoutParams.topMargin = getHeightForAnim(targetView)
                tipsView.layoutParams = layoutParams
            }
        })

        var thirdAnim2 = ObjectAnimator.ofFloat(targetView, "translationY", -maxTranY, 0f)
        thirdAnim2.interpolator = AccelerateInterpolator()
        animatorSet.playSequentially(thirdAnim1, thirdAnim2)
        animatorSet.setDuration(duration)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                image2.visibility = View.VISIBLE
                ViewUtils.setImageRes(image2, R.drawable.menu_icon_resource)
                revertImage(image2, -animX, -maxTranY)
                image2.bringToFront()
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)

                createAnimatorForVersion()
            }
        })
        animatorSet.startDelay = 500
        animatorSet.start()
    }


    private fun getLocationPoint(view: View): IntArray {
        var intArray = IntArray(2)
        view.getLocationInWindow(intArray)
        return intArray
    }
}