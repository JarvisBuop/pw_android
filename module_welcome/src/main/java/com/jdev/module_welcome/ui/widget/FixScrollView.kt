package com.jdev.module_welcome.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.VelocityTracker
import android.widget.ScrollView
import android.widget.Scroller


/**
 * Created by JarvisDong on 2019/07/03.
 * @Description:
 * @see:
 */
class VerticalScrollView : ScrollView {

//    private var mChildIndex:Int = 0

    private var mLastX = 0
    private var mLastY = 0

    private var mLastXIntecept = 0
    private var mLastYIntecept = 0

    private var mScroller: Scroller? = null
    private var mVelocityTracker: VelocityTracker? = null
    private var mGestureDetector: GestureDetector? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    init {
        if (mScroller == null) {
            mScroller = Scroller(context)
            mVelocityTracker = VelocityTracker.obtain()
        }
        mGestureDetector = GestureDetector(context, YScrollDetector())
    }

    inner class YScrollDetector : GestureDetector.SimpleOnGestureListener() {

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            if (Math.abs(distanceY) > Math.abs(distanceX)) {
                return true
            }
            return false
        }
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return super.onInterceptTouchEvent(ev)
                && mGestureDetector!!.onTouchEvent(ev)

//        var interceptor: Boolean = false
//        val x = ev.x.toInt()
//        val y = ev.y.toInt()
//
//        val action = ev.action
//        when (action) {
//            MotionEvent.ACTION_DOWN -> {
//                interceptor = false
//                if (!mScroller!!.isFinished) {
//                    mScroller!!.abortAnimation()
//                    interceptor = true
//                }
//            }
//            MotionEvent.ACTION_MOVE -> {
//                val deltaX = Math.abs(x - mLastXIntecept)
//                val deltaY = Math.abs(y - mLastYIntecept)
//                // 这里是否拦截的判断依据是左右滑动，读者可根据自己的逻辑进行是否拦截
//
//                if (deltaX > deltaY) {// 左右滑动不拦截
//                    interceptor = false
//                } else {
//                    interceptor = true
//                }
//            }
//            MotionEvent.ACTION_UP -> {
//                interceptor = false
//            }
//        }
//
//        mLastX = x
//        mLastY = y
//
//        mLastXIntecept = x
//        mLastYIntecept = y
//        Log.e("VerticalScrollView", "intercept:  " + interceptor)
//
//        return interceptor
    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        mVelocityTracker!!.addMovement(event)//速度追踪;
//        val x = event!!.getX().toInt()
//        val y = event!!.getY().toInt()
//        when (event.getAction()) {
//            MotionEvent.ACTION_DOWN -> if (!mScroller!!.isFinished()) {
//                mScroller!!.abortAnimation()
//            }
//            MotionEvent.ACTION_MOVE -> {
//                //父容器仅支持水平滑动;
//                val deltaX = (x - mLastX).toInt()
//                val deltaY = y - mLastY
//                Log.e("VerticalScrollView", "onTouchEvent-Move:$deltaX/$deltaY")
//                scrollBy(-deltaX, 0)
//            }
//            MotionEvent.ACTION_UP -> {
//                //getleft 和 view内容的左边缘的距离;
//                val scrollX = scrollX
//                //计算速度;
//                mVelocityTracker!!.computeCurrentVelocity(1000)
//                val xVelocity = mVelocityTracker!!.getXVelocity()
//
////                if (Math.abs(xVelocity) >= 50) {
////                    //滑动速度大于50,判断滑动方向是否翻页;
////                    mChildIndex = if (xVelocity > 0) mChildIndex - 1 else mChildIndex + 1
////                } else {
////                    //滑动速度小于50,判断滑动距离是否翻页;
////                    mChildIndex = (scrollX + mChildIWidth / 2) / mChildIWidth
////                }
////                Log.e("VerticalScrollView", "onTouchEvent-Up:$mChildIndex size:$mChildSize")
////                mChildIndex = Math.max(0, Math.min(mChildIndex, mChildSize - 1))
////                val dx = mChildIndex * mChildIWidth - scrollX//viewgroup滑动的距离;
////                smoothScrollBy(dx, 0)
//                mVelocityTracker!!.clear()
//            }
//        }
//        mLastX = x
//        mLastY = y
//        return true
//    }
//
////    fun smoothScrollBy(dx: Int, dy: Int) {
////        mScroller.startScroll(scrollX, dy, dx, dy, 500)
////        invalidate()
////    }
//
//    override fun computeScroll() {
//        if (mScroller != null && mScroller!!.computeScrollOffset()) {
//            scrollTo(mScroller!!.getCurrX(), mScroller!!.getCurrY())
//            postInvalidate()
//        }
//    }
//
//    override fun onDetachedFromWindow() {
//        mVelocityTracker!!.recycle()
//        super.onDetachedFromWindow()
//    }
}