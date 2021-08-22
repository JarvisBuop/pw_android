package com.jdev.wandroid.ui.frg

import android.graphics.Camera
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.Transformation
import android.widget.Button
import android.widget.LinearLayout
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.Utils
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.kit.widget.PathAnimView

class Camera3DAnimationFrag : BaseViewStubFragment() {

    /**
     * Camera 封装opengl的3D动画
     */
    class C3DAnimation() : Animation() {
        var mCamera: Camera = Camera()
        var mCenterW: Float = 0f
        var mCenterH: Float = 0f
        var mRotateY = 45
        override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
            super.initialize(width, height, parentWidth, parentHeight)
            duration = 2000
            fillAfter = true
            interpolator = BounceInterpolator()
            mCenterW = (width / 2).toFloat()
            mCenterH = (height / 2).toFloat()
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            t?.matrix?.apply {
                mCamera.save()
                mCamera.rotateY(mRotateY * interpolatedTime)
                mCamera.getMatrix(this)
                mCamera.restore()

                //1.左乘缩放矩阵
                //2.右乘平移矩阵
                //3.左乘平移矩阵
                //图像处理,优先执行右边的矩阵;
                postScale(0.5f, 0.5f)
                preTranslate(-mCenterW, -mCenterH)
                postTranslate(mCenterW, mCenterH)
            }
        }
    }

    override fun getViewStubDefault(): View? {
        return LinearLayout(mContext).apply {
            orientation = LinearLayout.VERTICAL
            var apply = Button(mContext).apply {
                text = "BUTTON"

                setOnClickListener {
                    val anim = C3DAnimation()
                    startAnimation(anim)
                }
            }
            addView(apply, ConvertUtils.dp2px(100f), ConvertUtils.dp2px(50f))

            var pathAnimView = PathAnimView(mContext)
            pathAnimView.setOnClickListener {
                pathAnimView.startPathAnim(5000)
            }
            addView(pathAnimView,ConvertUtils.dp2px(300f), ConvertUtils.dp2px(300f))
        }
    }

    override fun generateLayoutParams(): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ).apply {
            leftMargin = ConvertUtils.dp2px(50f)
            topMargin = ConvertUtils.dp2px(50f)
        }
    }

    override fun customOperate(savedInstanceState: Bundle?) {


    }


}