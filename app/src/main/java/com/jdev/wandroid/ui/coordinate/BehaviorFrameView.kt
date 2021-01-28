package com.zt.base.debug.coordinate

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class BehaviorFrameView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    lateinit var mBehavior: CoordinatorLayout.Behavior<View>

    override fun getBehavior(): CoordinatorLayout.Behavior<View> {
        return mBehavior
    }

    fun setBehavior(mBehavior: CoordinatorLayout.Behavior<View>) {
        this.mBehavior = mBehavior
    }

}