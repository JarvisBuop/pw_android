package com.zt.base.debug.coordinate

import android.support.design.widget.CoordinatorLayout
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

class MoveBehavior : CoordinatorLayout.Behavior<View>() {
    companion object {
        val TAG = "Behavior"
    }

    private var touchSlop = -1

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        Log.d(TAG, "onNestedPreScroll child-target ${child.javaClass.simpleName} ${target.javaClass.simpleName} $dx $dy")
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        Log.d(TAG, "onNestedScroll child-target ${child.javaClass.simpleName} ${target.javaClass.simpleName} $dxConsumed $dyConsumed $dxUnconsumed $dyUnconsumed")
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
    }

    override fun onNestedScrollAccepted(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int) {
        Log.d(TAG, "onNestedScrollAccepted child-direct-target ${child.javaClass.simpleName} ${directTargetChild.javaClass.simpleName} ${target.javaClass.simpleName} $axes")
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    override fun onNestedFling(coordinatorLayout: CoordinatorLayout, child: View, target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        Log.d(TAG, "onNestedFling child-target ${child.javaClass.simpleName} ${target.javaClass.simpleName} $velocityX $velocityY $consumed")
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed)
    }

    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: View, target: View, velocityX: Float, velocityY: Float): Boolean {
        Log.d(TAG, "onNestedFling child-target ${child.javaClass.simpleName} ${target.javaClass.simpleName} $velocityX $velocityY")
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        Log.d(TAG, "onStartNestedScroll child-target ${child.javaClass.simpleName} ${target.javaClass.simpleName}")
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
        Log.d(TAG, "onStopNestedScroll child-target ${child.javaClass.simpleName} ${target.javaClass.simpleName}")
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
    }

    override fun onAttachedToLayoutParams(params: CoordinatorLayout.LayoutParams) {
        Log.d(TAG, "onAttachedToLayoutParams")
        super.onAttachedToLayoutParams(params)
    }

    override fun onDetachedFromLayoutParams() {
        Log.d(TAG, "onDetachedFromLayoutParams")
        super.onDetachedFromLayoutParams()
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: View, ev: MotionEvent): Boolean {
        Log.d(TAG, "onInterceptTouchEvent child ${child.javaClass.simpleName}")
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_UP -> {
            }
            MotionEvent.ACTION_MOVE -> {
            }
        }
        return super.onInterceptTouchEvent(parent, child, ev)
    }

    private var lastMotionY = 0
    private var lastMotionX = 0
    public var dx = 0
    public var dy = 0
    override fun onTouchEvent(parent: CoordinatorLayout, child: View, event: MotionEvent): Boolean {
        Log.d(TAG, "onTouchEvent child ${child.javaClass.simpleName} ${event.action}")
        if (this.touchSlop < 0) {
            this.touchSlop = ViewConfiguration.get(parent.context).scaledTouchSlop
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastMotionX = event.x.toInt()
                lastMotionY = event.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val x: Int = event.x.toInt()
                val y: Int = event.y.toInt()

                dx = x - lastMotionX
                dy = y - lastMotionY
                val left: Int = child.left + dx
                val right: Int = child.right + dx
                val top: Int = child.top + dy
                val bottom: Int = child.bottom + dy
                child.layout(left, top, right, bottom)
                lastMotionX = x
                lastMotionY = y
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        Log.d(TAG, "onLayoutChild child ${child.javaClass.simpleName} $layoutDirection")
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onMeasureChild(parent: CoordinatorLayout, child: View, parentWidthMeasureSpec: Int, widthUsed: Int, parentHeightMeasureSpec: Int, heightUsed: Int): Boolean {
        Log.d(TAG, "onMeasureChild child ${child.javaClass.simpleName}")
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        Log.d(TAG, "layoutDependsOn child-dependency ${child.javaClass.simpleName} ${dependency.javaClass.simpleName}")
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        Log.d(TAG, "onDependentViewChanged child-dependency ${child.javaClass.simpleName} ${dependency.javaClass.simpleName}")
        return super.onDependentViewChanged(parent, child, dependency)
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View, dependency: View) {
        Log.d(TAG, "onDependentViewRemoved child-dependency ${child.javaClass.simpleName} ${dependency.javaClass.simpleName}")
        super.onDependentViewRemoved(parent, child, dependency)
    }

    override fun getScrimOpacity(parent: CoordinatorLayout, child: View): Float {
        Log.d(TAG, "getScrimOpacity child ${child.javaClass.simpleName}")
        return super.getScrimOpacity(parent, child)
    }

    override fun getScrimColor(parent: CoordinatorLayout, child: View): Int {
        Log.d(TAG, "getScrimColor child ${child.javaClass.simpleName} ")
        return super.getScrimColor(parent, child)
    }

    override fun blocksInteractionBelow(parent: CoordinatorLayout, child: View): Boolean {
        Log.d(TAG, "blocksInteractionBelow ${child.javaClass.simpleName}")
        return super.blocksInteractionBelow(parent, child)
    }
}