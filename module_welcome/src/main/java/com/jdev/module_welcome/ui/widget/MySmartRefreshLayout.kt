package com.jdev.module_welcome.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.jdev.kit.widget.home.HeaderViewPager
import com.scwang.smartrefresh.layout.SmartRefreshLayout

/**
 * info: create by jd in 2019/12/2
 * @see:
 * @description: 处理与HeaderViewPager的滑动冲突
 *
 * 外部
 */
class MySmartRefreshLayout : SmartRefreshLayout {
    val TAG = "MySmartRefreshLayout"

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(ev)
    }

    override fun dispatchTouchEvent(e: MotionEvent?): Boolean {
        when (e?.action) {
            MotionEvent.ACTION_MOVE -> {
                if (mRefreshContent != null) {
                    if (mRefreshContent.view is HeaderViewPager) {
                        var contentView = mRefreshContent.view as HeaderViewPager
//                        LogUtils.e(TAG, "canPtr: ${contentView.canPtr()}")
                        mEnableRefresh = contentView.canPtr()
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                mEnableRefresh = true
            }
        }

        return super.dispatchTouchEvent(e)
    }
}