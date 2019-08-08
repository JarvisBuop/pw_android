package com.jdev.wandroid.customui

import android.content.Context
import android.os.Handler
import android.os.Message
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.RelativeLayout
import android.widget.ScrollView

import java.util.Timer
import java.util.TimerTask

//import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;

/**
 * 仿淘宝详情;
 */
class ScrollViewContainer : RelativeLayout {

    //system class;
    private var vt: VelocityTracker? = null
    private var mViewHeight: Int = 0
    private var mViewWidth: Int = 0

    //params record
    private var state = DONE
    private var isNeedMeasured = false
    private var canPullDown: Boolean = false
    private var canPullUp: Boolean = false
    private var mCurrentViewIndex = 0
    private var mMoveDistance: Float = 0.toFloat()

    //view params
    private var topView: View? = null
    private var bottomView: View? = null
    private var mLastY: Float = 0.toFloat()
    private val mLastX: Float = 0.toFloat()
    private var mEvents: Int = 0

    //async
    private var mTimer: MyTimer? = null
    private val handler = object : Handler() {

        override fun handleMessage(msg: Message) {
            if (mMoveDistance != 0f) {
                if (state == AUTO_UP) {
                    mMoveDistance -= SPEED
                    if (mMoveDistance <= -mViewHeight) {
                        mMoveDistance = (-mViewHeight).toFloat()
                        state = DONE
                        mCurrentViewIndex = 1
                    }
                } else if (state == AUTO_DOWN) {
                    mMoveDistance += SPEED
                    if (mMoveDistance >= 0) {
                        mMoveDistance = 0f
                        state = DONE
                        mCurrentViewIndex = 0
                    }
                } else {
                    mTimer!!.cancel()
                }
            }
            requestLayout()
        }

    }

    /**
     * 上方View的触摸事件
     *
     * @see ObservableScrollView
     */
    private val topViewTouchListener = OnTouchListener { v, event ->
        if (v is ScrollView) {
            if ((v.scrollY == v.getChildAt(0).measuredHeight - v
                            .measuredHeight || v.getChildAt(0).measuredHeight < v
                            .measuredHeight) && mCurrentViewIndex == 0 && !disableDoubleScroll)
                canPullUp = true
            else
                canPullUp = false
        } else if (v is RecyclerView) {
            if (isScrollBottom(v)) {
                canPullUp = true
            } else {
                canPullUp = false
            }
        }
        false
    }


    // 最后一个完全可见项的位置
    private var lastCompletelyVisibleItemPosition: Int = 0


    /**
     * 下方View的触摸事件
     *
     * @see NestedScrollView
     */
    private val bottomViewTouchListener = OnTouchListener { v, event ->
        if (v is NestedScrollView) {
            if (v.scrollY == 0 && mCurrentViewIndex == 1)
                canPullDown = true
            else
                canPullDown = false
        } else if (v is RecyclerView) {
            if (isScrollTop(v)) {
                canPullDown = true
            } else {
                canPullDown = false
            }
        }
        false
    }

    private var firstCompletelyVisibleItemPosition: Int = 0

    private var disableDoubleScroll: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        mTimer = MyTimer(handler)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                try {
                    if (vt == null)
                        vt = VelocityTracker.obtain()
                    else
                        vt!!.clear()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mLastY = ev.y
                vt!!.addMovement(ev)
                mEvents = 0
            }
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP ->
                // 多一只手指按下或抬起时舍弃将要到来的第一个事件move，防止多点拖拽的bug
                mEvents = -1
            MotionEvent.ACTION_MOVE -> {
                vt!!.addMovement(ev)
                if (canPullUp && mCurrentViewIndex == 0 && mEvents == 0) {
                    mMoveDistance += ev.y - mLastY
                    // 防止上下越界
                    if (mMoveDistance > 0) {
                        mMoveDistance = 0f
                        mCurrentViewIndex = 0
                    } else if (mMoveDistance < -mViewHeight) {
                        mMoveDistance = (-mViewHeight).toFloat()
                        mCurrentViewIndex = 1

                    }
                    if (mMoveDistance < -8) {
                        // 防止事件冲突
                        ev.action = MotionEvent.ACTION_CANCEL
                    }
                } else if (canPullDown && mCurrentViewIndex == 1 && mEvents == 0) {
                    mMoveDistance += ev.y - mLastY
                    // 防止上下越界
                    if (mMoveDistance < -mViewHeight) {
                        mMoveDistance = (-mViewHeight).toFloat()
                        mCurrentViewIndex = 1
                    } else if (mMoveDistance > 0) {
                        mMoveDistance = 0f
                        mCurrentViewIndex = 0
                    }
                    if (mMoveDistance > 8 - mViewHeight) {
                        // 防止事件冲突
                        ev.action = MotionEvent.ACTION_CANCEL
                    }
                } else
                    mEvents++
                mLastY = ev.y
                requestLayout()
            }
            MotionEvent.ACTION_UP -> run {
                mLastY = ev.y
                vt!!.addMovement(ev)
                vt!!.computeCurrentVelocity(700)
                // 获取Y方向的速度
                val mYV = vt!!.yVelocity
                if (mMoveDistance == 0f || mMoveDistance == (-mViewHeight).toFloat()) {
                    return@run
                }
                if (Math.abs(mYV) < 500) {
                    // 速度小于一定值的时候当作静止释放，这时候两个View往哪移动取决于滑动的距离
                    if (mMoveDistance <= -mViewHeight / 2) {
                        state = AUTO_UP
                    } else if (mMoveDistance > -mViewHeight / 2) {
                        state = AUTO_DOWN
                    }
                } else {
                    // 抬起手指时速度方向决定两个View往哪移动
                    if (mYV < 0)
                        state = AUTO_UP
                    else
                        state = AUTO_DOWN
                }
                mTimer!!.schedule(2)
            }
        }
        super.dispatchTouchEvent(ev)
        return true
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (!isNeedMeasured) {
            isNeedMeasured = true

            mViewHeight = measuredHeight
            mViewWidth = measuredWidth

            topView = getChildAt(0)
            bottomView = getChildAt(1)

            bottomView!!.setOnTouchListener(bottomViewTouchListener)
            topView!!.setOnTouchListener(topViewTouchListener)
        }
        topView!!.layout(0, mMoveDistance.toInt(), mViewWidth,
                topView!!.measuredHeight + mMoveDistance.toInt())
        bottomView!!.layout(0, topView!!.measuredHeight + mMoveDistance.toInt(),
                mViewWidth, topView!!.measuredHeight + mMoveDistance.toInt()
                + bottomView!!.measuredHeight)
    }

    private fun isScrollBottom(recyclerView: RecyclerView): Boolean {
        val layoutManager = recyclerView.layoutManager
        // 找到最后一个完全可见项的位置
        if (layoutManager is StaggeredGridLayoutManager) {
            //            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) layoutManager;
            //            if (lastCompletelyVisiblePositions == null) {
            //                lastCompletelyVisiblePositions = new int[manager.getSpanCount()];
            //            }
            //            manager.findLastCompletelyVisibleItemPositions(lastCompletelyVisiblePositions);
            //            lastCompletelyVisibleItemPosition = getMaxPosition(lastCompletelyVisiblePositions);
        } else if (layoutManager is GridLayoutManager) {
            //            lastCompletelyVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
        } else if (layoutManager is LinearLayoutManager) {
            lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
        } else {
            throw RuntimeException("Unsupported LayoutManager.")
        }

        // 通过比对 最后完全可见项位置 和 总条目数，来判断是否滑动到底部
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        return if (visibleItemCount > 0 && lastCompletelyVisibleItemPosition >= totalItemCount - 1) {
            true
        } else {
            false
        }
    }

    private fun isScrollTop(recyclerView: RecyclerView): Boolean {
        val layoutManager = recyclerView.layoutManager
        // 找到最后一个完全可见项的位置
        if (layoutManager is StaggeredGridLayoutManager) {
            //            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) layoutManager;
            //            if (firstCompletelyVisibleItemPosition == null) {
            //                firstCompletelyVisibleItemPosition = new int[manager.getSpanCount()];
            //            }
            //            manager.findLastCompletelyVisibleItemPositions(lastCompletelyVisiblePositions);
            //            firstCompletelyVisibleItemPosition = getMaxPosition(firstCompletelyVisibleItemPosition);
        } else if (layoutManager is GridLayoutManager) {
            //            firstCompletelyVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
        } else if (layoutManager is LinearLayoutManager) {
            firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        } else {
            throw RuntimeException("Unsupported LayoutManager.")
        }

        // 通过比对 最后完全可见项位置 和 总条目数，来判断是否滑动到底部
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        return if (firstCompletelyVisibleItemPosition == 0) {
            true
        } else {
            false
        }
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

    fun setNeedMeasured(isMeasured: Boolean) {
        this.isNeedMeasured = isMeasured
    }

    fun setDisableDoubleScroll(disableDoubleScroll: Boolean) {
        this.disableDoubleScroll = disableDoubleScroll
    }

    companion object {
        //state
        val AUTO_UP = 0
        val AUTO_DOWN = 1
        val DONE = 2

        //params
        val SPEED = 6.5f
    }

}
