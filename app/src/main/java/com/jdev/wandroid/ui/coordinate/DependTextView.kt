package com.jdev.wandroid.ui.coordinate

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

class DependTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr),CoordinatorLayout.AttachedBehavior {
    init {
        text = "bottombottombottombottombottombottom"
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> {
        return FixBehavior()
    }
}