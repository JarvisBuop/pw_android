package com.example.libimagefilter.widget.glsurface

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.libimagefilter.widget.gesture.GestureGLHelper

/**
 * info: create by jd in 2020/1/16
 * @see:
 * @description:添加手势的 glSurface;
 */
class GPUImageGestureGLSurfaceView : GPUImageGLSurfaceView {

    lateinit var mGestureHelper: GestureGLHelper

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    fun enableGesture() {
        mGestureHelper = GestureGLHelper(context)

        mGestureHelper.setControllListener(object : GestureGLHelper.ControllListener {
            override fun setScaleXY(mScaleX: Float, mScaleY: Float) {
                setScaleX(mScaleX)
                setScaleY(mScaleY)
            }

            override fun setScrollXY(mScrollX: Int, mScrollY: Int) {
//                setScrollX(mScrollX)
//                setScrollY(mScrollY)
                translationX = -mScrollX.toFloat()
                translationY = -mScrollY.toFloat()
            }

            override fun postAction(runnable: Runnable?) {
                post(runnable)
            }

            override fun removeAction(runnable: Runnable?) {
                removeCallbacks(runnable)
            }

            override fun invalidateView() {
                invalidate()
                requestRender()
            }

        })
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (mGestureHelper.dispatchTouchEvent(event, parent)) {
            return true
        } else {
            return super.dispatchTouchEvent(event)
        }
    }
}