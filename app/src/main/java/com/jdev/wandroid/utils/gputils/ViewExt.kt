package com.jdev.wandroid.utils.gputils

import android.support.v4.view.ViewCompat
import android.view.View

/**
 * View的扩展方法;
 *
 * 确定view已经被layout过
 */
inline fun View.doOnLayout(crossinline action: (view: View) -> Unit) {
    //至少一次layout && 并不要求重新layout
    if (ViewCompat.isLaidOut(this) && !isLayoutRequested) {
        action(this)
    } else {
        doOnNextLayout { action(it) }
    }
}

inline fun View.doOnNextLayout(crossinline action: (view: View) -> Unit) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(
                view: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
        ) {
            view.removeOnLayoutChangeListener(this)
            action(view)
        }
    })
}
