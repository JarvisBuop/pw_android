package com.jdev.kit.widget.home

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import com.jdev.kit.R

/**
 * info: create by jd in 2019/7/17
 * @see:
 * @description:
 *
 */
class KtStatusBarHeightView : FrameLayout {
    private var statusBarHeight: Int = 0
    private var type: Int = 1

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(attrs)
    }


    private fun initView(attrs: AttributeSet?) {

        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId)
            }
        } else {
            statusBarHeight = 0
        }

        var ta = context.obtainStyledAttributes(attrs, R.styleable.KtStatusBarHeightView)
        type = ta.getInt(R.styleable.KtStatusBarHeightView_status_type, 1)
        ta.recycle()
        if (type == 1) {
            UsePaddingByType(statusBarHeight)
        }
    }

    fun UsePaddingByType(statusBarHeight: Int) {
        setPadding(paddingLeft, statusBarHeight, paddingRight, paddingBottom);
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        when (type) {
            0 -> {
                //height
                setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                        statusBarHeight)
            }
            else -> {
                //paddingtop
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
        }

    }
}