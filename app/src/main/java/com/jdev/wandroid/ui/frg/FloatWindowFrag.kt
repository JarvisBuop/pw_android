package com.jdev.wandroid.ui.frg

import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import com.blankj.utilcode.util.*
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import java.lang.reflect.Method

/**
 * info: create by jd in 2019/12/26
 * @see:
 * @description:
 *
 */
class FloatWindowFrag : BaseViewStubFragment() {

    private var wm: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null

    @SuppressLint("SetTextI18n")
    override fun getViewStubDefault(): View? {
        var btn = Button(mContext)
        btn.text = "show window"
        btn.setOnClickListener {
            if (commonROMPermissionCheck(mContext!!)) {
                doFiveWindow()
            } else {
                requestAlertWindowPermission()
            }
        }
        return btn
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
    }

    //window 悬浮window;
    private lateinit var floatButton: TextView
    private var rawX: Float = 0.toFloat()
    private var rawY: Float = 0.toFloat()
    private var isadd: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    private fun doFiveWindow() {
        wm = mContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        floatButton = TextView(mContext)
        floatButton.setText("点我")
        floatButton.setBackgroundColor(Color.GREEN)
        floatButton.setTextColor(resources.getColor(R.color.black))
        floatButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_camera_beauty, 0, 0)
        layoutParams = WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, PixelFormat.RGBA_8888)
        layoutParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED//锁屏;

//        layoutParams!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Android 8.0
            layoutParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            //其他版本
            layoutParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layoutParams!!.gravity = Gravity.LEFT or Gravity.TOP
        layoutParams!!.x = 500
        layoutParams!!.y = 700
        floatButton.setOnTouchListener(View.OnTouchListener { v, event ->
            rawX = event.rawX - floatButton.getMeasuredWidth() / 2
            rawY = event.rawY - floatButton.getMeasuredHeight() / 2
            LogUtils.e("wm$rawX/$rawY")

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                }
                MotionEvent.ACTION_UP -> {
                    val width = ScreenUtils.getScreenWidth()
                    if (rawX > width / 2) {
                        performAnimate(floatButton, rawX.toInt(), width - floatButton.getMeasuredWidth() / 2)
                    } else {
                        performAnimate(floatButton, rawX.toInt(), 0)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    layoutParams!!.x = rawX.toInt()
                    layoutParams!!.y = rawY.toInt()
                    wm!!.updateViewLayout(floatButton, layoutParams)
                }
            }
            false
        })
        floatButton.setOnClickListener(View.OnClickListener { ToastUtils.showShort("一点击") })
        if (!isadd) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(mContext)) {
                    wm!!.addView(floatButton, layoutParams)
                    isadd = true
                } else {
                    ToastUtils.showShort("需自己申请")
                }
            } else {
                ToastUtils.showShort("23版本一下,需自己申请;")
            }
        }
    }

    /**
     * 使用属性动画ValueAnimator ,监听windowmanager的动画;
     *
     * @param target
     * @param start
     * @param ent
     */
    private fun performAnimate(target: View, start: Int, ent: Int) {
        val va = ValueAnimator.ofFloat(1f, 100f)
        va.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            private val ie = IntEvaluator()

            override fun onAnimationUpdate(animation: ValueAnimator) {
                val currentF = animation.animatedFraction.toInt()
                val fraction = va.animatedFraction//0-1
                val evaluate = ie.evaluate(fraction, start, ent)
                layoutParams!!.x = evaluate!!
                layoutParams!!.y = rawY.toInt()
                wm!!.updateViewLayout(target, layoutParams)
            }
        })
        va.setDuration(1000).start()
    }


    private val REQUEST_CODE = 1;

    //判断权限
    fun commonROMPermissionCheck(context: Context): Boolean {
        var result = true;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                var clazz = Settings::class.java
                var canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context::class.java)
                result = canDrawOverlays.invoke(null, context) as Boolean
            } catch (e: Exception) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
        }
        return result
    }

    //申请权限
    fun requestAlertWindowPermission() {
        var intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + AppUtils.getAppPackageName()))
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Settings.canDrawOverlays(mContext)
                    } else {
                        TODO("VERSION.SDK_INT < M")
                    }) {
                LogUtils.eTag(TAG, "onActivityResult granted");
                doFiveWindow()
            }
        }
    }

}