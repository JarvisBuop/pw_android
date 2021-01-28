package com.zt.base.debug.coordinate

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import android.view.View
import com.jdev.wandroid.R

class BottomBehavior : androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior<View>() {
    override fun layoutDependsOn(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency.id == R.id.dynamicView
    }

    override fun onDependentViewChanged(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: View, dependency: View): Boolean {
        val behavior = (dependency.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams).behavior
        if (behavior is MoveBehavior) {
            val left = child.left + behavior.dx
            val right = child.right + behavior.dx
            val top = child.top + behavior.dy
            val bottom = child.bottom + behavior.dy

            val clampLeft = MathUtils.clamp(left, 0, parent.measuredWidth - child.measuredWidth)
            val clampRight = MathUtils.clamp(right, child.measuredWidth, parent.measuredWidth)
            val clampTop = MathUtils.clamp(top, parent.measuredHeight - child.measuredHeight, parent.measuredHeight)
            val clampBottom = MathUtils.clamp(bottom, parent.measuredHeight, parent.measuredHeight + child.measuredHeight)

            child.layout(clampLeft, clampTop, clampRight, clampBottom)
        }
        return true
    }
}