package com.jdev.module_welcome.ui.widget

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import com.blankj.utilcode.util.BarUtils
import com.jdev.module_welcome.ui.helper.HeaderScrollHelper


/**
 * Created by JarvisDong on 2019/07/03.
 * @Description:
 * @see:
 *
 * 监听 tab滑动到某位置,不拦截事件;
 */
class FixScrollView : NestedScrollView {
    val TAG: String = "FixScrollView"

    //目标view的浮顶;
    var floatView: View? = null
    //距离顶部偏移
    var offsetForTop: Int = 0

    var maxScrollY: Int = 0
    //是否浮顶
    var criticalH: Int = 0

    var mTouchSlop: Int = 0
    lateinit var mScroller: Scroller
    lateinit var mVelocityTracker: VelocityTracker


    var flagLeaveTop: Boolean = false
    val mScrollable: HeaderScrollHelper

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    interface OnScrollListener {
//        fun onScroll(currentY: Int, maxY: Int)

        /**
         * 向下滑动 && 离开上临界点;
         * 显示act中按钮向上的图片等;
         */
        fun scrolldownToLeaveThreshold(criticalH: Int, dy: Float)
    }

    private var mListener: OnScrollListener? = null
    fun setOnScrollListener(onScrollListener: OnScrollListener) {
        this.mListener = onScrollListener
    }

    fun setCurrentScrollableContainer(scrollableContainer: HeaderScrollHelper.ScrollableContainer?) {
        mScrollable.setCurrentScrollableContainer(scrollableContainer)
    }


    init {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mScroller = Scroller(context)
        mVelocityTracker = VelocityTracker.obtain()
        mScrollable = HeaderScrollHelper()

    }

    /**
     * 最大滑动距离;
     */
    fun setTargetView(floatView: View, offsetForTop: Int = 0) {
        this.floatView = floatView
        this.offsetForTop = offsetForTop

        if (maxScrollY <= 0) {
            //todo  获取的高度为-185, 可能不正确,负数重新计算;
            var currentScrollDistance = getCurrentScrollDistance(floatView)
            maxScrollY = if (currentScrollDistance > maxScrollY) currentScrollDistance else maxScrollY
        }
    }

    /**
     * 此处也可以传入minY和maxY 来规定滑动的位置;
     */
    fun isAttachPosition(floatView: View, dx: Float, dy: Float): Boolean {
        criticalH = getCurrentScrollDistance(floatView)
        Log.e(TAG, "h:: " + criticalH + " dy: " + dy + "  " + scrollY + " orient: " + mScrollable.isTop())
//        /**
//         * logic:
//         * return true -> 不拦截;
//         * return false -> 拦截;
//         *
//         * 到上临界点:
//         *     - 上滑(dy < 0): scrollview 固定,子view获取事件; 不拦截(true)
//         *     - 下滑: scrollview 获取事件,滑动; 拦截(false)
//         *
//         * 到下临界点:
//         *     - 上滑: scrollview 获取事件,滑动; 拦截(false)
//         *     - 下滑: scrollview 固定,子view获取事件; 不拦截(true)
//         *
//         * 处于上临界点和下临界点之间 :
//         *     - 上滑: scrollview 获取事件,滑动; 拦截(false);
//         *     - 下滑: scrollview 获取事件,滑动; 拦截(false);
//         *
//         *
//         */
//        if (criticalH <= 0) {
//            return dy <= 0
//        } else if (scrollY <= 0) {
//            return dy > 0
//        } else {
//            return false
//        }

        /**
         * 修改逻辑
         *
         * (1) 到上临界点:
         *      up(dy < 0): -> 子view获取事件; scrollview不拦截(true)
         *      down: 判断子view 是否滑到顶部
         *             是 top ->  scrollview 获取事件,拦截(false)
         *             不是top -> 子view获取事件; scrollview不拦截(true)
         *
         * (2) 到下临界点:
         *      up: -> scrollview 获取事件,拦截(false)
         *      down: -> 子view获取事件,scrollview不拦截(true)
         *          (离开头部,所有fragment中的scrollview滑动到top;)
         *
         * (3) 处于之间:
         *      up: -> scrollview 获取事件,拦截(false)
         *
         *      down: -> scrollview 获取事件,拦截(false)
         *                  (离开头部,所有fragment中的scrollview滑动到top;)
         */

        if (criticalH <= 0) {
            flagLeaveTop = true
            if (dy <= 0) {
                return true
            } else {
                if (mScrollable.isTop()) {
                    return false
                } else {
                    return true
                }
            }
        } else {
            if (mListener != null) {
                if (dy > 0 && flagLeaveTop) {
                    mListener?.scrolldownToLeaveThreshold(criticalH, dy)
                    flagLeaveTop = false
                }
            }

            if (scrollY <= 0) {
                if (dy <= 0) {
                    return false
                } else {
                    return true
                }
            } else {
                return false
            }
        }
    }

    fun getCurrentScrollDistance(floatView: View): Int {
        val location = IntArray(2)
        floatView.getLocationInWindow(location)
        return (location[1] - BarUtils.getStatusBarHeight() - offsetForTop)
    }

    private fun scrollToTop(dy: Float) {
        //设置可滑动的最大距离:
        var scrollDy = dy.toInt()

        if (criticalH > 0 && scrollDy + criticalH <= 0 && dy <= 0) {
            scrollDy = -criticalH
        }

//        Log.e(TAG, " scrollToTop: " + criticalH + " scrollDy: [" + scrollDy + "," + dy + "] scrollY: " + scrollY)
        smoothScrollBy(0, -scrollDy)
    }

    /**
     * 重写防止跳动;
     */
    override fun computeScroll() {
//        super.computeScroll()
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            postInvalidate()
        }
    }

//    override fun scrollBy(x: Int, y: Int) {
//        val scrollY = scrollY
//        var toY = scrollY + y
//        if (toY >= maxScrollY) {
//            toY = maxScrollY
//        }
//        var finalY = toY - scrollY
//
////        Log.e(TAG, " scrollBy: " + criticalH + " y : " + y + " finalY " + finalY + " max " + maxScrollY)
//        super.scrollBy(x, finalY)
//    }
//
//    override fun scrollTo(x: Int, y: Int) {
//        var finalY = y
//        if (y >= maxScrollY) {
//            finalY = maxScrollY
//        }
//        super.scrollTo(x, finalY)
//    }


    var mLastX: Float = 0f
    var mLastY: Float = 0f
    var downX: Float = 0f
    var downY: Float = 0f
    var orientationY = true

    /**
     * 可保证滑动的流畅性;
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        return super.dispatchTouchEvent(ev)
        if (floatView == null) return super.dispatchTouchEvent(ev)

        val action = ev.action
        var x = ev.x
        var y = ev.y
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (!mScroller.isFinished) {
                    mScroller.abortAnimation()
                }
                mLastX = x
                mLastY = y
                downX = x
                downY = y
                orientationY = true
            }
            MotionEvent.ACTION_MOVE -> {
                var dx = ev.x - mLastX
                var dy = ev.y - mLastY

                var totaldx = x - downX
                var totaldy = y - downY

                orientationY = Math.abs(dy) > Math.abs(dx)

                var logFlag = 0
                if (orientationY) {
                    if (isAttachPosition(floatView!!, dx, dy)) {
                        logFlag = 1
                    } else {
                        scrollToTop(dy)
                        logFlag = 2
                    }
                } else {

                }
                Log.e(TAG, " dispatchTouchEvent - MOVE " + logFlag)
            }
            MotionEvent.ACTION_UP -> {
                if (orientationY) {
                    mVelocityTracker.computeCurrentVelocity(500, maxScrollY.toFloat())
                    var yVelocity = mVelocityTracker.yVelocity
                    isAttachPosition(floatView!!, 0f, 0f)
                    scrollToTop(yVelocity)
                    mVelocityTracker.clear()
                }
            }
        }

        mLastX = x
        mLastY = y

        //手动将事件传递给子View，让子View自己去处理事件
        super.dispatchTouchEvent(ev)
        //消费事件，返回True表示当前View需要消费事件，就是事件的TargetView
        return true
    }

//    //todo 存在滑动不连续的问题,原因是返回true之后就直接交给当前的onTouchEvent处理了;
//    //todo 使用dispatchTouchEvent尝试;
//    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
//        if (floatView == null) return super.onInterceptTouchEvent(ev)
//        var interceptor: Boolean = super.onInterceptTouchEvent(ev)
//        var x = ev.x
//        var y = ev.y
//
//        val action = ev.action
//        when (action) {
//            MotionEvent.ACTION_DOWN -> {
//                interceptor = false
//                if (!mScroller.isFinished) {
//                    mScroller.abortAnimation()
//                    interceptor = true
//                }
//                downX = x
//                downY = y
//            }
//            MotionEvent.ACTION_MOVE -> {
//                var dx = x - mLastX
//                var dy = y - mLastY
//
//                var totaldx = x - downX
//                var totaldy = y - downY
//                if (Math.abs(totaldy) < scaledTouchSlop) {
//                    interceptor = false
//                } else {
//                    if (isAttachPosition(floatView!!, dx, dy)) {
//                        interceptor = false
//                    } else {
//                        interceptor = true
//                    }
//                }
//                Log.e(TAG, " interceptor: " + interceptor)
//            }
//            MotionEvent.ACTION_UP -> {
//                interceptor = false
//            }
//        }
//
//        mLastX = x
//        mLastY = y
//        return interceptor
//    }
//
//    override fun onTouchEvent(ev: MotionEvent): Boolean {
//        if (floatView == null) return super.onTouchEvent(ev)
//        mVelocityTracker.addMovement(ev)
//        var x = ev.x
//        var y = ev.y
//
//        val action = ev.action
//        when (action) {
//            MotionEvent.ACTION_DOWN -> {
//                if (!mScroller.isFinished) {
//                    mScroller.abortAnimation()
//                }
//                downX = x
//                downY = y
//            }
//            MotionEvent.ACTION_MOVE
//            -> {
//                var dx = ev.x - mLastX
//                var dy = ev.y - mLastY
//
//                var totaldx = x - downX
//                var totaldy = y - downY
//
//                var flag = 0
//                if (Math.abs(totaldy) < scaledTouchSlop) {
//                    flag = 0
//                } else {
//                    if (isAttachPosition(floatView!!, dx, dy)) {
//                        flag = 1
//                    } else {
////                        super.onTouchEvent(ev)
//                        scrollToTop(dy)
//                        flag = 2
//                    }
//                }
//                Log.e(TAG, " onTouchEvent - MOVE " + flag)
//            }
//            MotionEvent.ACTION_UP -> {
//                //最大可滑200
//                mVelocityTracker.computeCurrentVelocity(500, maxScrollY.toFloat())
//                var yVelocity = mVelocityTracker.yVelocity
//                isAttachPosition(floatView!!, 0f, 0f)
//                scrollToTop(yVelocity)
//                mVelocityTracker.clear()
//            }
//            else -> {
////                super.onTouchEvent(ev)
//            }
//        }
//        mLastX = x
//        mLastY = y
//        return true
//    }
}
