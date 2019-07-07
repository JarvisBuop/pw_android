package com.jdev.module_welcome.ui.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View

/**
 * Created by JarvisDong on 2019/07/06.
 * @Description:
 * @see:
 */
class FixViewPager : ViewPager {
    val TAG: String = "FixViewPager"

    private var mFixChildHeightMeasureSpec: Int = 0
    private var mFixChildWidthMeasureSpec: Int = 0
    private var makeMeasureSpec: Int = 0

    private var fixHeight: Int = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    init {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        /**
         * 父类测量完,高度为0,需要再次测量父布局;
         *
         * 因为父类已经将所有子view测量,应该可以直接获取每个子view的高度;
         */

//        var currentChildViewHeight = getHeightForCurrentPosition(currentItem)
        var currentChildViewHeight = if (getHeightForOffset() != 0) getHeightForOffset() else getHeightForCurrentPosition(currentItem)

        setMeasuredDimension(View.getDefaultSize(0, widthMeasureSpec),
                View.getDefaultSize(currentChildViewHeight, heightMeasureSpec))
    }

    fun getHeightForOffset(): Int {
        return fixHeight
    }

    fun setFixHeight(fixHeight: Int) {
        this.fixHeight = fixHeight
    }

    /**
     * 获取当前viewpager的 item 高度;
     */
    fun getHeightForCurrentPosition(position: Int): Int {
        if (position >= 0 && position < childCount) {
            var childAt = getChildAt(position)
            if (childAt != null && childAt.visibility != View.GONE) {
                var childWidthSize = measuredWidth - paddingLeft - paddingRight
                var childHeightSize = measuredHeight - paddingTop - paddingBottom
                mFixChildWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(childWidthSize, View.MeasureSpec.EXACTLY)
                mFixChildHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                childAt.measure(mFixChildWidthMeasureSpec, mFixChildHeightMeasureSpec)
                return childAt.measuredHeight
            }
        }
        return 0
    }

//    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
//        var interceptor: Boolean = false
//        var x = ev.x
//        var y = ev.y
//
//        val action = ev.action
//        when (action) {
//            MotionEvent.ACTION_DOWN -> {
//                interceptor = false
////                if (!mScroller.isFinished) {
////                    mScroller.abortAnimation()
////                    interceptor = true
////                }
////                downX = x
////                downY = y
//            }
//            MotionEvent.ACTION_MOVE -> {
////                var dx = x - mLastX
////                var dy = y - mLastY
////
////                var totaldx = x - downX
////                var totaldy = y - downY
////                if (Math.abs(totaldy) < scaledTouchSlop) {
////                    interceptor = false
////                } else {
////                    if (isAttachPosition(floatView!!, dx, dy)) {
////                        interceptor = false
////                    } else {
////                        interceptor = true
////                    }
////                }
//                Log.e(TAG, " interceptor: " + interceptor)
//            }
//            MotionEvent.ACTION_UP -> {
//                interceptor = false
//            }
//        }
//
////        mLastX = x
////        mLastY = y
////        super.onInterceptTouchEvent(ev)
//        return interceptor
//    }

//    override fun onTouchEvent(ev: MotionEvent?): Boolean {
//        var evBool = super.onTouchEvent(ev)
//        Log.e(TAG, " onTouchEvent " + evBool)
//        return evBool
//    }
}