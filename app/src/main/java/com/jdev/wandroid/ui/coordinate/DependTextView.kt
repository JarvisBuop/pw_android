package com.jdev.wandroid.ui.coordinate

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet

class DependTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), androidx.coordinatorlayout.widget.CoordinatorLayout.AttachedBehavior {
    init {
        text = "bottombottombottombottombottombottom"
    }

    override fun getBehavior(): androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior<*> {
        return FixBehavior()
    }
}