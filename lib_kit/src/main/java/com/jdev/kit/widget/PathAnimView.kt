package com.jdev.kit.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator


class PathAnimView(context: Context?) : View(context) {
    private var mPathMeasure: PathMeasure? = null
    private lateinit var mPaint: Paint
    private lateinit var mPath: Path
    private var mCurrentPosition = FloatArray(2)

    private var isQuad = false

    private fun init() {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.setStyle(Paint.Style.STROKE)
        mPaint.setStrokeWidth(PATH_WIDTH)
        mPaint.setColor(Color.RED)
        mPath = Path()
        mPath.moveTo(
            START_POINT[0],
            START_POINT[1]
        )
        if (isQuad) {
            mPath.quadTo(
                RIGHT_CONTROL_POINT[0],
                RIGHT_CONTROL_POINT[1],
                BOTTOM_POINT[0],
                BOTTOM_POINT[1]
            )
            mPath.quadTo(
                LEFT_CONTROL_POINT[0],
                LEFT_CONTROL_POINT[1],
                START_POINT[0], START_POINT[1]
            )
        } else {
            mPath.cubicTo(
                LEFT_CONTROL_POINT_1[0],
                LEFT_CONTROL_POINT_1[1],
                LEFT_CONTROL_POINT_2[0],
                LEFT_CONTROL_POINT_2[1],
                BOTTOM_POINT[0],
                BOTTOM_POINT[1]
            )
            mPath.cubicTo(
                RIGHT_CONTROL_POINT_2[0],
                RIGHT_CONTROL_POINT_2[1],
                RIGHT_CONTROL_POINT_1[0],
                RIGHT_CONTROL_POINT_1[1],
                START_POINT[0], START_POINT[1]
            )
        }
        mPathMeasure = PathMeasure(mPath, true)
        mCurrentPosition = FloatArray(2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.WHITE)
        canvas.drawPath(mPath, mPaint)
        if (isQuad) {
            canvas.drawCircle(
                RIGHT_CONTROL_POINT[0],
                RIGHT_CONTROL_POINT[1], 5f, mPaint
            )
            canvas.drawCircle(
                LEFT_CONTROL_POINT[0],
                LEFT_CONTROL_POINT[1], 5f, mPaint
            )
        } else {
            canvas.drawCircle(
                RIGHT_CONTROL_POINT_1[0],
                RIGHT_CONTROL_POINT_1[1],5f,mPaint
            )
            canvas.drawCircle(
                RIGHT_CONTROL_POINT_2[0],
                RIGHT_CONTROL_POINT_2[1],5f,mPaint
            )
            canvas.drawCircle(
                LEFT_CONTROL_POINT_1[0],
                LEFT_CONTROL_POINT_1[1],5f,mPaint
            )
            canvas.drawCircle(
                LEFT_CONTROL_POINT_2[0],
                LEFT_CONTROL_POINT_2[1],5f,mPaint
            )
        }

        // 绘制对应目标
        canvas.drawCircle(mCurrentPosition[0], mCurrentPosition[1], 10f, mPaint)
    }

    // 开启路径动画
    fun startPathAnim(duration: Long) {
        // 0 － getLength()
        val valueAnimator = ValueAnimator.ofFloat(0f, mPathMeasure!!.length)
        Log.i(TAG, "measure length = " + mPathMeasure!!.length)
        valueAnimator.duration = duration
        // 减速插值器
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            // 获取当前点坐标封装到mCurrentPosition
            mPathMeasure!!.getPosTan(value, mCurrentPosition, null)
            postInvalidate()
        }
        valueAnimator.start()
    }

    companion object {
        private const val TAG = "DynamicHeartView"
        private const val PATH_WIDTH = 2f

        // 起始点
        private val START_POINT = floatArrayOf(
            300f, 300f
        )

        // 爱心下端点
        private val BOTTOM_POINT = floatArrayOf(
            300f, 600f
        )

        // 左侧控制点
        private val LEFT_CONTROL_POINT = floatArrayOf(
            450f, 100f
        )

        // 右侧控制点
        private val RIGHT_CONTROL_POINT = floatArrayOf(
            150f, 100f
        )

        //-----------------
        private val LEFT_CONTROL_POINT_1 = floatArrayOf(
            500f, 100f
        )

        private val LEFT_CONTROL_POINT_2 = floatArrayOf(
            500f, 400f
        )

        private val RIGHT_CONTROL_POINT_1 = floatArrayOf(
            100f, 100f
        )

        private val RIGHT_CONTROL_POINT_2 = floatArrayOf(


            100f, 400f
        )
    }

    init {
        init()
    }
}