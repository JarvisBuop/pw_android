package com.zt.base.debug.coordinate

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class BehaviorFrameView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), androidx.coordinatorlayout.widget.CoordinatorLayout.AttachedBehavior {

    lateinit var mBehavior: androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior<View>

    override fun getBehavior(): androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior<View> {
        return mBehavior
    }

    fun setBehavior(mBehavior: androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior<View>) {
        this.mBehavior = mBehavior
    }

}