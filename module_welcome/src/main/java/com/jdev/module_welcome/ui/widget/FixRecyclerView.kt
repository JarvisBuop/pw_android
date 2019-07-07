package com.jdev.module_welcome.ui.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewParent


/**
 * Created by JarvisDong on 2019/07/07.
 * @Description:
 * @see:
 */
class FixRecyclerView : RecyclerView {
    val TAG: String = "FixRecyclerView"

    lateinit var parentP: ViewParent

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.e(TAG, "dispatchTouchEvent")

        getParentView()

        return super.dispatchTouchEvent(ev)
    }

    private fun getParentView() {
        parentP = this

        // 循环查找ViewPager, 请求ViewPager不拦截触摸事件
        while (parentP !is ViewPager) {
            this.parentP = this.parentP.parent
            // nop
        }
        parentP.requestDisallowInterceptTouchEvent(true)
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        Log.e(TAG, "onInterceptTouchEvent")

        return super.onInterceptTouchEvent(e)
    }

//    var mLastX: Float = 0f
//    var mLastY: Float = 0f
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        Log.e(TAG, "onTouchEvent")
//        var x = event.x
//        var y = event.y
//        var action = event.getAction()
//        when (action) {
//            MotionEvent.ACTION_DOWN -> {
//                getParentView()
//            }
//            MotionEvent.ACTION_MOVE -> {
//                getParentView()
////                var dx = event.x - mLastX
////                var dy = event.y - mLastY
////                smoothScrollBy(0, -dy.toInt())
//            }
//        }
//
//        mLastX = x
//        mLastY = y
//        return super.onTouchEvent(event)
//    }
}