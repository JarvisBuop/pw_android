package com.jdev.wandroid.ui.frg

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.jarvisdong.kit.baseui.BaseApp
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import com.jdev.wandroid.ui.act.ContainerActivity
import com.yhao.floatwindow.*

/**
 * info: create by jd in 2019/12/26
 * @see:
 * @description:
 *
 */
class FloatWindowFrag : BaseViewStubFragment() {
    private var countSend = 0

    @SuppressLint("HandlerLeak")
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            countSend++
            val intent = Intent(EXTRA_PROGRESSBAR_COUNT)
            intent.putExtra(PROGRESSBAR_EXTRA, countSend)
            mContext!!.sendBroadcast(intent)

            if (countSend <= 100) {
                this.sendEmptyMessageDelayed(0, 100)
            } else {
                this.removeCallbacksAndMessages(null)
            }
        }
    }


    internal val EXTRA_PROGRESSBAR_COUNT = "progressbar_count_type"
    internal val PROGRESSBAR_EXTRA = "key_pb_extra"
    private var count = 0
    private var pb: ProgressBar? = null
    private var img: ImageView? = null

    override fun getViewStubDefault(): View? {
        var textView = Button(mContext)
        textView.text = "btn_floatview"
        textView.setOnClickListener {

        }
        return textView
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        initFloatView()

        handler.sendEmptyMessage(0)
    }

    private fun initFloatView() {
        val imageView = ImageView(BaseApp.getApp())
        imageView.setImageResource(R.drawable.icon_header_use)

        FloatWindow
                .with(BaseApp.getApp())
                .setView(imageView)
                .setWidth(Screen.width, 0.2f) //设置悬浮控件宽高
                .setHeight(Screen.width, 0.2f)
                .setX(Screen.width, 0.5f)
                .setY(Screen.height, 0.3f)
                .setMoveType(MoveType.slide, 0, 0)
                .setMoveStyle(500, BounceInterpolator())
                .setFilter(true, ContainerActivity::class.java)
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .setDesktopShow(true)
                .build()


        val view = LayoutInflater.from(BaseApp.getApp()).inflate(R.layout.app_float_controllbar, null)
        pb = view.findViewById(R.id.float_pb)
        img = view.findViewById(R.id.float_img)

        pb!!.setProgress(count)
        img!!.setVisibility(View.VISIBLE)
        (img!!.getDrawable() as AnimationDrawable).start()
        view.setOnClickListener(View.OnClickListener {
            (img!!.getDrawable() as AnimationDrawable).start()
        })

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if (intent == null || EXTRA_PROGRESSBAR_COUNT !== intent!!.getAction()) {
                    return
                }
                count = intent!!.getIntExtra(PROGRESSBAR_EXTRA, count)
                pb!!.setProgress(count)

                if (count >= 100) {
                    (img!!.getDrawable() as AnimationDrawable).stop()
                    mContext!!.unregisterReceiver(this)
                } else {

                }
            }
        }
        mContext!!.registerReceiver(receiver, IntentFilter(EXTRA_PROGRESSBAR_COUNT))


        FloatWindow
                .with(BaseApp.getApp())
                .setView(view)
                .setWidth(Screen.width, 0.15f) //设置悬浮控件宽高
                .setHeight(Screen.width, 0.15f)
                .setX(Screen.width, 0.5f)
                .setY(Screen.height, 0.3f)
                .setMoveType(MoveType.slide, 0, 0)
                .setMoveStyle(500, FastOutLinearInInterpolator())
                .setFilter(true, ContainerActivity::class.java)
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(mPermissionListener)
                .setDesktopShow(true)
                .setTag("multi")
                .build()


        imageView.setOnClickListener(View.OnClickListener {
            Toast.makeText(BaseApp.getApp(), "onClick", Toast.LENGTH_SHORT).show()
        })
    }

    private val mPermissionListener = object : PermissionListener {
        override fun onSuccess() {
            Log.d(TAG, "onSuccess")
        }

        override fun onFail() {
            Log.d(TAG, "onFail")
        }
    }

    private val mViewStateListener = object : ViewStateListener {
        override fun onPositionUpdate(x: Int, y: Int) {
            Log.d(TAG, "onPositionUpdate: x=$x y=$y")
        }

        override fun onShow() {
            Log.d(TAG, "onShow")
        }

        override fun onHide() {
            Log.d(TAG, "onHide")
        }

        override fun onDismiss() {
            Log.d(TAG, "onDismiss")
        }

        override fun onMoveAnimStart() {
            Log.d(TAG, "onMoveAnimStart")
        }

        override fun onMoveAnimEnd() {
            Log.d(TAG, "onMoveAnimEnd")
        }

        override fun onBackToDesktop() {
            Log.d(TAG, "onBackToDesktop")
        }
    }

}