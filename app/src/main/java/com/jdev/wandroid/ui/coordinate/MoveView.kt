package com.zt.base.debug.coordinate

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import android.widget.LinearLayout

class MoveView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), androidx.coordinatorlayout.widget.CoordinatorLayout.AttachedBehavior {
    override fun getBehavior(): androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior<*> {
        return MoveBehavior()
    }
}