package com.zt.base.debug.coordinate

import android.support.design.widget.CoordinatorLayout
import android.support.v4.math.MathUtils
import android.view.View
import com.jdev.wandroid.R

class TopBehavior : CoordinatorLayout.Behavior<View>() {
    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency.id == R.id.dynamicView
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        val behavior = (dependency.layoutParams as CoordinatorLayout.LayoutParams).behavior
        if (behavior is MoveBehavior) {
            val left = child.left + behavior.dx
            val right = child.right + behavior.dx
            val top = child.top + behavior.dy
            val bottom = child.bottom + behavior.dy

            val clampLeft = MathUtils.clamp(left, 0, parent.measuredWidth - child.measuredWidth)
            val clampRight = MathUtils.clamp(right, child.measuredWidth, parent.measuredWidth)
            val clampTop = MathUtils.clamp(top, -child.measuredHeight, 0)
            val clampBottom = MathUtils.clamp(bottom, 0, child.measuredHeight)

            child.layout(clampLeft, clampTop, clampRight, clampBottom)
        }
        return true
    }
}