package com.zt.base.debug.coordinate

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.widget.LinearLayout

class MoveView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {
    override fun getBehavior(): CoordinatorLayout.Behavior<*> {
        return MoveBehavior()
    }
}