package com.jdev.kit.widget.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewConfiguration
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ScrollView
import com.jdev.kit.helper.HeaderScrollHelper
import java.util.*

//import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;

/**
 * 仿淘宝详情;
 */
class ExhanceScrollViewContainer : FrameLayout {

    private val TAG = "ScrollViewContainer"

    companion object {
        //state
        val AUTO_UP = 0
        val AUTO_DOWN = 1
        val DONE = 2

        // animator
        val SPEED = 6.5f
    }

    //system class;
    private val mVelocityTracker: VelocityTracker
    private val mTouchSlop: Int
    private val mMaximumVelocity: Int
    private var mViewHeight: Int = 0
    private var mViewWidth: Int = 0
    private val mScrollable: ScrollHelper

    //params record
    private var state = DONE
    private var canPullDown: Boolean = false
    private var canPullUp: Boolean = false
    private var mCurrentViewIndex = 0
    //触摸移动距离;
    private var mMoveDistanceY: Float = 0f

    //view params
    private var topView: View? = null
    private var bottomView: View? = null
//    private var mEvents: Int = 0

    //async
    private var mTimer: MyTimer? = null

    //public params
    private var disableDoubleScroll: Boolean = false


    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            if (mMoveDistanceY != 0f) {
                when (state) {
                    AUTO_UP -> {
                        mMoveDistanceY -= SPEED
                        if (mMoveDistanceY <= -mViewHeight) {
                            mMoveDistanceY = (-mViewHeight).toFloat()
                            state = DONE
                            mCurrentViewIndex = 1
                        }
                    }
                    AUTO_DOWN -> {
                        mMoveDistanceY += SPEED
                        if (mMoveDistanceY >= 0) {
                            mMoveDistanceY = 0f
                            state = DONE
                            mCurrentViewIndex = 0
                        }
                    }
                    else -> {
                        mTimer?.cancel()
                    }
                }
            }
            requestLayout()
        }
    }


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    init {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mMaximumVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity
        mVelocityTracker = VelocityTracker.obtain()
        mScrollable = ScrollHelper()

        mTimer = MyTimer(handler)
    }

    /**
     * 上方View的触摸事件
     */
    private val topViewTouchListener = OnTouchListener { v, event ->
        mScrollable.targetView = v
        if (mScrollable.isBottom() /*&& mCurrentViewIndex == 0*/ && !disableDoubleScroll) {
            canPullUp = true
        } else {
            canPullUp = false
        }
//        LogUtils.e(TAG, "top " + canPullUp)
        false
    }

    /**
     * 下方View的触摸事件
     */
    private val bottomViewTouchListener = OnTouchListener { v, event ->
        mScrollable.targetView = v
        if (mScrollable.isTop() /*&& mCurrentViewIndex == 1*/) {
            canPullDown = true
        } else {
            canPullDown = false
        }
//        LogUtils.e(TAG, "bottom " + canPullDown)
        false
    }

    private var mLastY: Float = 0f
    private var mLastX: Float = 0f

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
//        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mLastX = ev.x
                mLastY = ev.y
                mVelocityTracker.addMovement(ev)
//                mEvents = 0
            }
//            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP ->
                // 多一只手指按下或抬起时舍弃将要到来的第一个事件move，防止多点拖拽的bug
//                mEvents = -1
            MotionEvent.ACTION_MOVE -> {
//                LogUtils.e(TAG, " move :" + canPullUp + "/" + canPullDown + " current: " + mCurrentViewIndex)
                var dy = ev.y - mLastY
                if (canPullUp && mCurrentViewIndex == 0 /*&& mEvents == 0*/) {
                    mMoveDistanceY += dy
//                    Log.e(TAG, "dy:  " + dy + " / " + mMoveDistanceY + " " + canPullUp)
                    illegalDistance()
                    if (mMoveDistanceY < -mTouchSlop) {
                        // 防止事件冲突
                        ev.action = MotionEvent.ACTION_CANCEL
                    }
                } else if (canPullDown && mCurrentViewIndex == 1 /*&& mEvents == 0*/) {
                    mMoveDistanceY += dy
//                    Log.e(TAG, "dy:  " + dy + " / " + mMoveDistanceY + " " + canPullDown)
                    illegalDistance()
                    if (mMoveDistanceY > mTouchSlop - mViewHeight) {
                        // 防止事件冲突
                        ev.action = MotionEvent.ACTION_CANCEL
                    }
                }
//                else
//                    mEvents++
                mLastX = ev.x
                mLastY = ev.y
                requestLayout()
            }
            MotionEvent.ACTION_UP -> run {
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                val mYV = mVelocityTracker.yVelocity
                if (mMoveDistanceY == 0f || mMoveDistanceY == (-mViewHeight).toFloat()) {
                    return@run
                }
                if (Math.abs(mYV) < 500) {
                    // 速度小于一定值的时候当作静止释放，这时候两个View往哪移动取决于滑动的距离
                    if (mMoveDistanceY <= -mViewHeight / 2) {
                        state = AUTO_UP
                    } else if (mMoveDistanceY > -mViewHeight / 2) {
                        state = AUTO_DOWN
                    }
                } else {
                    // 抬起手指时速度方向决定两个View往哪移动
                    if (mYV < 0)
                        state = AUTO_UP
                    else
                        state = AUTO_DOWN
                }
                mTimer?.schedule(2)
            }
        }
        super.dispatchTouchEvent(ev)
        return true
    }

    fun illegalDistance() {
        if (mMoveDistanceY > 0) {
            mMoveDistanceY = 0f
            mCurrentViewIndex = 0
        } else if (mMoveDistanceY < -mViewHeight) {
            mMoveDistanceY = (-mViewHeight).toFloat()
            mCurrentViewIndex = 1
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initChildView()
    }

    fun initChildView() {
        topView = getChildAt(0)
        bottomView = getChildAt(1)

        topView?.setOnTouchListener(topViewTouchListener)
        bottomView?.setOnTouchListener(bottomViewTouchListener)

        if (topView == null || bottomView == null) throw NullPointerException("child view must not be null!!")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mViewHeight = measuredHeight
        mViewWidth = measuredWidth
        while (topView == null || bottomView == null) {
            initChildView()
        }
        val touchMove = mMoveDistanceY.toInt()
        val topBottom = (topView?.measuredHeight ?: 0) + touchMove
        val bottomBottom = topBottom + (bottomView?.measuredHeight ?: 0)

//        Log.e(TAG, "SVC:  " + touchMove + "/" + topBottom + "/" + bottomBottom)
        topView?.layout(0, touchMove, mViewWidth, topBottom)
        bottomView?.layout(0, topBottom, mViewWidth, bottomBottom)
    }

    internal inner class MyTimer(private val handler: Handler) {
        private val timer: Timer
        private var mTask: MyTask? = null

        init {
            timer = Timer()
        }

        fun schedule(period: Long) {
            if (mTask != null) {
                mTask!!.cancel()
                mTask = null
            }
            mTask = MyTask(handler)
            timer.schedule(mTask, 0, period)
        }

        fun cancel() {
            if (mTask != null) {
                mTask!!.cancel()
                mTask = null
            }
        }

        internal inner class MyTask(private val handler: Handler) : TimerTask() {
            override fun run() {
                handler.obtainMessage().sendToTarget()
            }
        }
    }

    /**
     * 是否允许滑动;
     */
    fun setDisableDoubleScroll(disableDoubleScroll: Boolean) {
        this.disableDoubleScroll = disableDoubleScroll
    }


    /**
     * 判断childview滑动
     */
    class ScrollHelper {
        public var targetView: View? = null

        fun isTop(): Boolean {
            val scrollableView = targetView
            if (scrollableView == null) {
                Log.e("isTop", "You should call ScrollableHelper.setCurrentScrollableContainer() to set ScrollableContainer.")
                return false
            }
            if (scrollableView is AdapterView<*>) {
                return HeaderScrollHelper.isAdapterViewTop(scrollableView)
            }
            if (scrollableView is ScrollView) {
                return HeaderScrollHelper.isScrollViewTop(scrollableView)
            }
            if (scrollableView is androidx.recyclerview.widget.RecyclerView) {
                return HeaderScrollHelper.isRecyclerViewTopCompleteVisiable(scrollableView)
            }
            if (scrollableView is WebView) {
                return HeaderScrollHelper.isWebViewTop(scrollableView)
            }
            Log.e("isTop", "scrollableView must be a instance of AdapterView|ScrollView|RecyclerView")
            return false
        }

        fun isBottom(): Boolean {
            val scrollableView = targetView
            if (scrollableView == null) {
                Log.e("isTop", "You should call ScrollableHelper.setCurrentScrollableContainer() to set ScrollableContainer.")
                return false
            }
            if (scrollableView is AdapterView<*>) {
                return HeaderScrollHelper.isAdapterViewBottom(scrollableView)
            }
            if (scrollableView is ScrollView) {
                return HeaderScrollHelper.isScrollViewBottom(scrollableView)
            }
            if (scrollableView is androidx.recyclerview.widget.RecyclerView) {
                return HeaderScrollHelper.isRecyclerViewBottomCompleteVisiable(scrollableView)
            }
            if (scrollableView is WebView) {
                return HeaderScrollHelper.isWebViewBottom(scrollableView)
            }
            Log.e("isTop", "scrollableView must be a instance of AdapterView|ScrollView|RecyclerView")
            return false
        }

    }
}
