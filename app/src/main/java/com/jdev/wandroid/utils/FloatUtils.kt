package com.jdev.wandroid.utils

import android.animation.Animator
import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.os.Message
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jarvisdong.kit.baseui.BaseApp
import com.jdev.wandroid.R
import com.jdev.wandroid.ui.act.ContainerActivity
import com.jdev.wandroid.ui.act.FloatLocationActivity
import com.jdev.wandroid.ui.act.MainActivity
import com.yhao.floatwindow.*

/**
 * info: create by jd in 2019/12/30
 * @see:
 * @description:
 *
 */
class FloatUtils {

    @SuppressLint("StaticFieldLeak")
    companion object {
        //action
        internal val ACTION_PROGRESSBAR_COUNT = "progressbar_count_type"
        //pb count
        internal val PROGRESSBAR_EXTRA = "key_pb_extra"

        val TAG = "FloatUtils"
        var currentTag: String? = null


        var rotateAnimator: Animator? = null
        var mFloatRootView: View? = null
        var pb: ProgressBar? = null
        var img: ImageView? = null
        var imgPlay: ImageView? = null
        var rootCircle: View? = null
        var rootViewGroup: View? = null
        var recyclerView: RecyclerView? = null
        private var receiver: BroadcastReceiver? = null
        private var isLocationLeft: Boolean = false


        private var countSend = 0
        @SuppressLint("HandlerLeak")
        private val handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                countSend++
                val intent = Intent(FloatUtils.ACTION_PROGRESSBAR_COUNT)
                intent.putExtra(FloatUtils.PROGRESSBAR_EXTRA, countSend)
                BaseApp.getApp().sendBroadcast(intent)

                if (countSend <= 100) {
                    this.sendEmptyMessageDelayed(0, 100)
                } else {
                    this.removeCallbacksAndMessages(null)
                }
            }
        }

        fun startSendMsg() {
            countSend = 0
            handler.sendEmptyMessage(0)
        }


        fun destroy(tag: String? = currentTag) {
            FloatWindow.destroy(tag)
            currentTag = null
            rotateAnimator?.cancel()
            mFloatRootView = null
            pb = null
            img = null
            imgPlay = null
            rootCircle = null

            handler.removeCallbacksAndMessages(null)
            if (receiver != null) {
                BaseApp.getApp().unregisterReceiver(receiver)
            }
        }

        fun showFloatViewByTag(currentTag: String = "multi") {
            this.currentTag = currentTag
            if (getFloatImplByTag(currentTag) == null) {
                initFloatView()

                bindReceiver()

                FloatWindow
                        .with(BaseApp.getApp())
                        .setView(mFloatRootView!!)
//                        .setWidth(ConvertUtils.dp2px(60f)) //设置悬浮控件宽高
//                        .setHeight(ConvertUtils.dp2px(60f))

                        .setX(Util.getScreenWidth(BaseApp.getApp()) - ConvertUtils.dp2px(60f))
                        .setY(Screen.height, 0.3f)
                        .setMoveType(MoveType.slide, 0, 0)
                        .setMoveStyle(500, LinearOutSlowInInterpolator())
                        .setFilter(true, ContainerActivity::class.java, MainActivity::class.java)
                        .setViewStateListener(mViewStateListener)
                        .setPermissionListener(mPermissionListener)
                        .setDesktopShow(true)
                        .setTag(currentTag)
                        .build()
            }
        }

        private fun initFloatView() {
            mFloatRootView = LayoutInflater.from(BaseApp.getApp()).inflate(R.layout.app_float_controllbar, null)
            pb = mFloatRootView!!.findViewById(R.id.float_pb)
            img = mFloatRootView!!.findViewById(R.id.float_img)
            imgPlay = mFloatRootView!!.findViewById(R.id.float_play)
            rootCircle = mFloatRootView!!.findViewById(R.id.float_circle)
            rootViewGroup = mFloatRootView!!.findViewById(R.id.float_viewgroup)
            recyclerView = mFloatRootView!!.findViewById<RecyclerView>(R.id.float_recyclerview)

            (img!!.drawable as AnimationDrawable).start()

            //触摸关闭列表;
            rootViewGroup?.setOnTouchListener { v, event ->
                var floatImplByTag = getFloatImplByTag(currentTag)
                if (floatImplByTag != null) {
                    //动画;
                    rootCircle?.visibility = View.VISIBLE
                    rootViewGroup?.visibility = View.GONE

                    if (floatImplByTag is IFloatWindowImpl) {
                        floatImplByTag.config.setMoveType(MoveType.slide)
                    }
                }
                return@setOnTouchListener true
            }

            mFloatRootView!!.setOnClickListener {
                LogUtils.e(TAG, "float view is click1 !!!")

                var floatImplByTag = getFloatImplByTag(currentTag)
                if (floatImplByTag != null) {
                    /**
                     * 浮窗落地页 -> 弹出页;
                     *
                     * 方式一: 跳入一个新的activity页面;
                     */
//                    rotateAnimator?.resume()
//                    bindReceiver()
//
//                    goLocationAct(BaseApp.getApp().applicationContext, floatImplByTag)


                    /**
                     * 方式二: 同一个浮窗控制不同view;
                     */

                    if (floatImplByTag is IFloatWindowImpl) {
                        var config = floatImplByTag.config
                        floatImplByTag.updateX(0)
                        floatImplByTag.updateY(0)
                        config.setMoveType(MoveType.inactive)
                    }

                    //显示列表项,并设置布局属性和是否能够滑动,设置列表加载动画;
                    rootCircle?.visibility = View.GONE
                    rootViewGroup?.visibility = View.VISIBLE

                    var datas = arrayListOf<String>("1", "2", "3")
                    recyclerView?.layoutManager = LinearLayoutManager(recyclerView?.context)
                    recyclerView?.adapter = object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.app_float_item_include, datas) {
                        override fun convert(helper: BaseViewHolder?, item: String?) {

                        }
                    }
                }

            }
        }

        private fun goLocationAct(mContext: Context, floatImplByTag: IFloatWindow) {
            var intent = Intent(mContext, FloatLocationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(FloatLocationActivity.KEY_LOCATION_PARAMS, FloatLocationActivity.MyLocation(
                    floatImplByTag.x,
                    floatImplByTag.y,
                    mFloatRootView?.width ?: 0,
                    mFloatRootView?.height ?: 0,
                    isLocationLeft
            ))
            mContext.startActivity(intent)
        }

        private fun bindReceiver() {
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent?) {
                    if (intent == null || ACTION_PROGRESSBAR_COUNT != intent.action) {
                        return
                    }
                    var count = intent!!.getIntExtra(PROGRESSBAR_EXTRA, 0)
                    pb?.progress = count


                    if (count >= 100) {
                        (img!!.drawable as AnimationDrawable).stop()
                        rotateAnimator?.pause()
                        if (receiver != null) {
                            BaseApp.getApp().unregisterReceiver(receiver)
                            receiver = null
                        }
                    } else {
                        startAnimator()
                    }
                }
            }
            BaseApp.getApp().registerReceiver(receiver, IntentFilter(ACTION_PROGRESSBAR_COUNT))
        }

        //旋转动画;
        private fun startAnimator() {
            if (rotateAnimator != null && rotateAnimator!!.isRunning) {
                return
            }
            rotateAnimator = AnimatorInflater.loadAnimator(BaseApp.getApp(), R.animator.audio_rotate_playbar)
            rotateAnimator!!.setTarget(img)
            rotateAnimator!!.start()
        }

        private fun getFloatImplByTag(tag: String?): IFloatWindow? {
            if (!StringUtils.isSpace(tag)) {
                return FloatWindow.get(currentTag!!)
            }
            return null
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
                var floatImplByTag = getFloatImplByTag(currentTag)
                if (floatImplByTag != null) {
                    if (x <= 0) {
                        rootCircle?.setBackgroundResource(R.drawable.bg_float_rightcorner)
                        isLocationLeft = true
                    } else if (x >= Util.getScreenWidth(BaseApp.getApp()) - mFloatRootView?.width!!) {
                        rootCircle?.setBackgroundResource(R.drawable.bg_float_leftcorner)
                        isLocationLeft = false
                    } else {
                        rootCircle?.setBackgroundResource(R.drawable.bg_float_nocorner)
                    }
                }
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
}