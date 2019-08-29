package com.jdev.kit.widget

import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.annotation.ColorRes
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.ConvertUtils
import com.jdev.kit.R


/**
 * info: create by jd in 2019/8/12
 * @see:
 * @description: 用于阴影view;
 *
 */
class ShadowViewWrapper : FrameLayout {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initDefaultByAttrs(context, attrs)
    }

    private var childView: View? = null
    private var mShadowPaint: Paint? = null

    private var cornerRadius: Float = ConvertUtils.dp2px(12f).toFloat() //绘制有圆角的背景;
    private var shadowColor: Int = Color.RED
    private var deltaLength: Float = ConvertUtils.dp2px(6f).toFloat() //shadow扩展的距离;
    private var shadowRadius: Float = ConvertUtils.dp2px(6f).toFloat() //shadow的圆角;
    private var drawShadow: Boolean = true
    private var shadowOffsetX: Float = 0f
    private var shadowOffsetY: Float = 2f

    private var shadowMode = SHADOW_MODE_NORMAL
    private var shadowRoundExtraHeight = 0

    companion object {
        public val SHADOW_MODE_NORMAL = 0
        public val SHADOW_MODE_BOTTOMROUND = 1
    }

    private fun initDefaultByAttrs(context: Context, attrs: AttributeSet?) {
        var ta = context.obtainStyledAttributes(attrs, R.styleable.ShadowViewWrapper)
        var indexCount = ta.indexCount
        for (i in 0 until indexCount) {
            if (ta.getIndex(i) == R.styleable.ShadowViewWrapper_shadow_color) {
                shadowColor = ta.getColor(ta.getIndex(i), shadowColor)
            } else if (ta.getIndex(i) == R.styleable.ShadowViewWrapper_shadow_radius) {
                shadowRadius = ta.getDimension(ta.getIndex(i), shadowRadius)
            } else if (ta.getIndex(i) == R.styleable.ShadowViewWrapper_shadow_deltaLength) {
                deltaLength = ta.getDimension(ta.getIndex(i), deltaLength)
            } else if (ta.getIndex(i) == R.styleable.ShadowViewWrapper_shadow_container_corner) {
                cornerRadius = ta.getDimension(ta.getIndex(i), cornerRadius)
            } else if (ta.getIndex(i) == R.styleable.ShadowViewWrapper_shadow_offsetX) {
                shadowOffsetX = ta.getDimension(ta.getIndex(i), shadowOffsetX)
            } else if (ta.getIndex(i) == R.styleable.ShadowViewWrapper_shadow_offsetY) {
                shadowOffsetY = ta.getDimension(ta.getIndex(i), shadowOffsetY)
            } else if (ta.getIndex(i) == R.styleable.ShadowViewWrapper_shadow_mode) {
                shadowMode = ta.getInt(ta.getIndex(i), SHADOW_MODE_NORMAL)
            } else if (ta.getIndex(i) == R.styleable.ShadowViewWrapper_shadow_extra_height) {
                shadowRoundExtraHeight = ta.getInt(ta.getIndex(i), shadowRoundExtraHeight)
            }
        }
        ta.recycle()

        mShadowPaint = Paint()
        mShadowPaint!!.style = Paint.Style.FILL
        mShadowPaint!!.color = shadowColor
        mShadowPaint!!.isAntiAlias = true
        mShadowPaint!!.setShadowLayer(shadowRadius, shadowOffsetX, shadowOffsetY, shadowColor)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        var childCount = childCount
        if (childCount != 0) {
            childView = getChildAt(0)
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (drawShadow && childView != null) {
            var left = childView!!.getLeft().toFloat()
            var top = childView!!.getTop().toFloat()
            var right = childView!!.getRight().toFloat()
            var bottom = childView!!.getBottom().toFloat()

            when (shadowMode) {
                SHADOW_MODE_NORMAL -> {
                    if (getLayerType() != LAYER_TYPE_SOFTWARE) {
                        setLayerType(LAYER_TYPE_SOFTWARE, null)
                    }
                }
                SHADOW_MODE_BOTTOMROUND -> {
                    if (layerType != LAYER_TYPE_NONE) {
                        setLayerType(LAYER_TYPE_NONE, null)
                    }
                    left += deltaLength
                    right -= deltaLength
                    bottom += deltaLength
                }
                else -> {
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas?.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, mShadowPaint)
            } else {
                var drawablePath = Path()
                drawablePath.moveTo(left + cornerRadius, top)
                drawablePath.arcTo(RectF(left, top, left + 2 * cornerRadius, top + 2 * cornerRadius), -90f, -90f, false)
                drawablePath.lineTo(left, bottom - cornerRadius)
                drawablePath.arcTo(RectF(left, bottom - 2 * cornerRadius, left + 2 * cornerRadius, bottom), 180f, -90f, false)
                drawablePath.lineTo(right - cornerRadius, bottom)
                drawablePath.arcTo(RectF(right - 2 * cornerRadius, bottom - 2 * cornerRadius, right, bottom), 90f, -90f, false)
                drawablePath.lineTo(right, top + cornerRadius)
                drawablePath.arcTo(RectF(right - 2 * cornerRadius, top, right, top + 2 * cornerRadius), 0f, -90f, false)
                drawablePath.close()
                canvas?.drawPath(drawablePath, mShadowPaint)
            }

        }
        super.dispatchDraw(canvas)
    }

    fun setDrawShadow(drawShadow: Boolean) {
        if (this.drawShadow === drawShadow) {
            return
        }
        this.drawShadow = drawShadow
        postInvalidate()
    }

    fun setShadowMode(shadowMode: Int) {
        this.shadowMode = shadowMode
        postInvalidate()
    }

    fun setShadowColor(@ColorRes color: Int) {
        this.shadowColor = context.resources.getColor(color)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (childCount != 1) {
            throw IllegalStateException("子View只能有一个")
        }

        val measuredWidth = measuredWidth
        val measuredHeight = measuredHeight
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val child = getChildAt(0)
        val layoutParams = child.layoutParams as LayoutParams
        val childBottomMargin = Math.max(deltaLength, layoutParams.bottomMargin.toFloat()) + 1
        val childLeftMargin = Math.max(deltaLength, layoutParams.leftMargin.toFloat()) + 1
        val childRightMargin = Math.max(deltaLength, layoutParams.rightMargin.toFloat()) + 1
        val childTopMargin = Math.max(deltaLength, layoutParams.topMargin.toFloat()) + 1
        val widthMeasureSpecMode: Int
        val widthMeasureSpecSize: Int
        val heightMeasureSpecMode: Int
        val heightMeasureSpecSize: Int
        if (widthMode == View.MeasureSpec.UNSPECIFIED) {
            widthMeasureSpecMode = View.MeasureSpec.UNSPECIFIED
            widthMeasureSpecSize = View.MeasureSpec.getSize(widthMeasureSpec)
        } else {
            if (layoutParams.width == LayoutParams.MATCH_PARENT) {
                widthMeasureSpecMode = View.MeasureSpec.EXACTLY
                widthMeasureSpecSize = (measuredWidth - childLeftMargin - childRightMargin - paddingLeft - paddingRight).toInt()
            } else if (LayoutParams.WRAP_CONTENT == layoutParams.width) {
                widthMeasureSpecMode = View.MeasureSpec.AT_MOST
                widthMeasureSpecSize = (measuredWidth - childLeftMargin - childRightMargin - paddingLeft - paddingRight).toInt()
            } else {
                widthMeasureSpecMode = View.MeasureSpec.EXACTLY
                widthMeasureSpecSize = layoutParams.width - paddingLeft - paddingRight
            }
        }
        if (heightMode == View.MeasureSpec.UNSPECIFIED) {
            heightMeasureSpecMode = View.MeasureSpec.UNSPECIFIED
            heightMeasureSpecSize = View.MeasureSpec.getSize(heightMeasureSpec)
        } else {
            if (layoutParams.height == LayoutParams.MATCH_PARENT) {
                heightMeasureSpecMode = View.MeasureSpec.EXACTLY
                heightMeasureSpecSize = (measuredHeight - childBottomMargin - childTopMargin - paddingTop - paddingBottom).toInt()
            } else if (LayoutParams.WRAP_CONTENT == layoutParams.height) {
                heightMeasureSpecMode = View.MeasureSpec.AT_MOST
//                heightMeasureSpecSize = (measuredHeight - childBottomMargin - childTopMargin - paddingTop - paddingBottom).toInt()
                heightMeasureSpecSize = (measuredHeight - childBottomMargin - childTopMargin - paddingTop - paddingBottom).toInt()
            } else {
                heightMeasureSpecMode = View.MeasureSpec.EXACTLY
                heightMeasureSpecSize = layoutParams.height - paddingTop - paddingBottom
            }
        }
//        measureChild(child, View.MeasureSpec.makeMeasureSpec(widthMeasureSpecSize, widthMeasureSpecMode), View.MeasureSpec.makeMeasureSpec(heightMeasureSpecSize, heightMeasureSpecMode))
        measureChild(child, View.MeasureSpec.makeMeasureSpec(widthMeasureSpecSize, widthMeasureSpecMode), heightMeasureSpec)

        val parentWidthMeasureSpec = View.MeasureSpec.getMode(widthMeasureSpec)
        val parentHeightMeasureSpec = View.MeasureSpec.getMode(heightMeasureSpec)
        var newHeight = measuredHeight
        var newWidth = measuredWidth
        val childHeight = child.measuredHeight
        val childWidth = child.measuredWidth
        Log.e("Shadow: : :", "" + newHeight + "/" + newWidth + "/" + childHeight + "/" + childWidth)

        if (parentHeightMeasureSpec == View.MeasureSpec.AT_MOST) {
            newHeight = (childHeight + childTopMargin + childBottomMargin + paddingTop + paddingBottom).toInt()
        }
        if (parentWidthMeasureSpec == View.MeasureSpec.AT_MOST) {
            newWidth = (childWidth + childRightMargin + childLeftMargin + paddingLeft + paddingRight).toInt()
        }
        if (newWidth < childWidth + 2 * deltaLength) {
            newWidth = (childWidth + 2 * deltaLength).toInt()
        }
        if (newHeight < childHeight + 2 * deltaLength) {
            newHeight = (childHeight + 2 * deltaLength).toInt()
        }
        if (newHeight != measuredHeight || newWidth != measuredWidth) {
            if (shadowMode == SHADOW_MODE_BOTTOMROUND) {
                setMeasuredDimension(newWidth, newHeight + shadowRoundExtraHeight)
                return
            }
            setMeasuredDimension(newWidth, newHeight)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val child = getChildAt(0)
        val measuredWidth = measuredWidth
        var measuredHeight = measuredHeight
        val childMeasureWidth = child.measuredWidth
        val childMeasureHeight = child.measuredHeight
        if (shadowMode == SHADOW_MODE_BOTTOMROUND) {
            measuredHeight -= shadowRoundExtraHeight
            child.layout((measuredWidth - childMeasureWidth) / 2, (measuredHeight - childMeasureHeight) / 2,
                    (measuredWidth + childMeasureWidth) / 2, (measuredHeight + childMeasureHeight) / 2)
            return
        }
        child.layout((measuredWidth - childMeasureWidth) / 2, (measuredHeight - childMeasureHeight) / 2,
                (measuredWidth + childMeasureWidth) / 2, (measuredHeight + childMeasureHeight) / 2)
    }
}