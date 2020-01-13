package com.jdev.wandroid.utils

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Message
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
        var mAdapter: BaseQuickAdapter<String, BaseViewHolder>? = null
        var mDetailDatas = arrayListOf<String>()
        //action
        internal val ACTION_PROGRESSBAR_COUNT = "progressbar_count_type"
        //pb count
        internal val PROGRESSBAR_EXTRA = "key_pb_extra"

        val TAG = "FloatUtils"
        var currentTag: String? = null


        var rotateAnimator: Animator? = null

        var rootView: View? = null

        var mFloatCircleView: View? = null
        var mFloatDetailView: View? = null

        var pb: ProgressBar? = null
        var img: ImageView? = null
        var imgPlay: ImageView? = null

        var floatDetailRecyclerView: RecyclerView? = null
        private var receiver: BroadcastReceiver? = null
        private var isLocationLeft: Boolean = false

        var tempX: Int = 0
        var tempY: Int = 0


        private var countSend = 0
        @SuppressLint("HandlerLeak")
        private val handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    0 -> {
                        countSend++
                        val intent = Intent(FloatUtils.ACTION_PROGRESSBAR_COUNT)
                        intent.putExtra(FloatUtils.PROGRESSBAR_EXTRA, countSend)
                        BaseApp.getApp().sendBroadcast(intent)

                        if (countSend <= 100) {
                            this.sendEmptyMessageDelayed(0, 100)
                        } else {
//                            this.removeCallbacksAndMessages(null)
                        }
                    }


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


            mFloatDetailView = null
            floatDetailRecyclerView = null
            pb = null
            img = null
            imgPlay = null
            mFloatCircleView = null

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
                        .setView(rootView!!)
                        .setX(Util.getScreenWidth(BaseApp.getApp()) - ConvertUtils.dp2px(60f))
                        .setY(Screen.height, 0.3f)
                        .setMoveType(MoveType.slide, 0, 0)
                        .setMoveStyle(200, LinearOutSlowInInterpolator())
                        .setFilter(true, ContainerActivity::class.java, MainActivity::class.java)
                        .setViewStateListener(mViewStateListener)
                        .setPermissionListener(mPermissionListener)
                        .setDesktopShow(true)
                        .setTag(currentTag)
                        .build()
            }
        }

        private fun initFloatView() {
            rootView = LayoutInflater.from(BaseApp.getApp()).inflate(R.layout.app_float_controllbar, null)

            initFloatCircleView(rootView!!)
            initFloatDetailView(rootView!!)

        }

        private fun initFloatDetailView(mFloatView: View) {
            //detail
            mFloatDetailView = mFloatView!!.findViewById(R.id.float_viewgroup)
            //控制gravity和marginTop 显示具体位置;
            floatDetailRecyclerView = mFloatView!!.findViewById<RecyclerView>(R.id.float_recyclerview)

//            触摸关闭列表;
            mFloatDetailView?.setOnTouchListener { v, event ->
                var floatImplByTag = getFloatImplByTag(currentTag)
                if (floatImplByTag != null) {

                    mFloatDetailView?.visibility = View.GONE
                    mFloatCircleView?.visibility = View.VISIBLE
                    mFloatCircleView?.alpha = 1f

                    if (floatImplByTag is IFloatWindowImpl) {

                        var config = floatImplByTag.config
                        config.setMoveType(MoveType.slide)
                        config.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                        config.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        floatImplByTag.config = config

                        floatImplByTag.updateX(tempX)
                        floatImplByTag.updateY(tempY)
                    }
                }
                return@setOnTouchListener true
            }

            //init recyclerview
            floatDetailRecyclerView?.layoutManager = LinearLayoutManager(floatDetailRecyclerView!!.context)
            mAdapter = object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.app_float_item_include, mDetailDatas) {
                override fun convert(helper: BaseViewHolder?, item: String?) {
                    helper?.apply {
                        var view = getView<View>(R.id.float_playview)
                        if(isLocationLeft){
                            view.setBackgroundResource(R.drawable.bg_float_rightcorner)
                        }else {
                            view.setBackgroundResource(R.drawable.bg_float_leftcorner)
                        }
                    }
                }
            }

            mAdapter?.isFirstOnly(false)
            mAdapter?.setDuration(200)
            floatDetailRecyclerView?.adapter = mAdapter
        }

        private fun initFloatCircleView(mFloatView: View) {
            //circle
            mFloatCircleView = mFloatView!!.findViewById(R.id.float_circle)
            pb = mFloatView!!.findViewById(R.id.float_pb)
            img = mFloatView!!.findViewById(R.id.float_img)
            imgPlay = mFloatView!!.findViewById(R.id.float_play)

            mFloatView!!.setOnClickListener {
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
//                    goLocationAct(BaseApp.getApp().applicationContext, floatImplByTag)


                    /**
                     * 方式二: 同一个浮窗控制不同view;
                     *
                     * updatex 瞬移view,体验不好;
                     * 解决方法: 添加动画;
                     */
                    AnimUtils.showAnimtorForAlpha(mFloatCircleView, 1f, 0f, animEndFunc = {
                        //显示列表项,并设置布局属性和是否能够滑动,设置列表加载动画;
                        mFloatCircleView?.visibility = View.GONE
                        mFloatDetailView?.visibility = View.VISIBLE

                        if (floatImplByTag is IFloatWindowImpl) {

                            var config = floatImplByTag.config
                            tempX = floatImplByTag.x
                            tempY = floatImplByTag.y

                            config.setMoveType(MoveType.inactive)
                            config.setView(rootView!!)
                            config.setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                            config.setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
                            floatImplByTag.config = config

                            floatImplByTag.updateX(0)
                            floatImplByTag.updateY(0)
                        }


                        fillDetailRecyclerView()
                    }, animStartFunc = {
                        mFloatCircleView?.visibility = View.VISIBLE
                        mFloatDetailView?.visibility = View.GONE
                    })
                }

            }
        }

        private fun fillDetailRecyclerView() {
            //controll position
            var layoutParams = floatDetailRecyclerView?.layoutParams
            if (layoutParams is ViewGroup.MarginLayoutParams) {
                var marginLayoutParams = layoutParams
                var marginTop = tempY + ConvertUtils.dp2px(60f)
                var itemHeight = ConvertUtils.dp2px(60f) * mDetailDatas.size
                if (marginTop + itemHeight > Util.getScreenHeight(BaseApp.getApp()) - ConvertUtils.dp2px(60f)) {
                    marginTop = tempY - itemHeight
                }
                marginLayoutParams.topMargin = marginTop
                floatDetailRecyclerView?.layoutParams = marginLayoutParams
            }
            if (layoutParams is FrameLayout.LayoutParams) {
                if (isLocationLeft) {
                    layoutParams.gravity = Gravity.LEFT
                } else {
                    layoutParams.gravity = Gravity.RIGHT
                }
            }
            floatDetailRecyclerView?.layoutParams = layoutParams

            //add to datas
            if (isLocationLeft) {
                mAdapter?.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT)
            } else {
                mAdapter?.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT)
            }
            mDetailDatas.clear()
            mDetailDatas.add("1")
            mAdapter?.notifyDataSetChanged()
        }

//        private fun goLocationAct(mContext: Context, floatImplByTag: IFloatWindow) {
//            var intent = Intent(mContext, FloatLocationActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            intent.putExtra(FloatLocationActivity.KEY_LOCATION_PARAMS, FloatLocationActivity.MyLocation(
//                    floatImplByTag.x,
//                    floatImplByTag.y,
//                    mFloatCircleView?.width ?: 0,
//                    mFloatCircleView?.height ?: 0,
//                    isLocationLeft
//            ))
//            mContext.startActivity(intent)
//        }

        private fun bindReceiver() {
            receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent?) {
                    if (intent == null || ACTION_PROGRESSBAR_COUNT != intent.action) {
                        return
                    }
                    var count = intent!!.getIntExtra(PROGRESSBAR_EXTRA, 0)
                    pb?.progress = count


                    if (count >= 100) {
//                        (img!!.drawable as AnimationDrawable).stop()
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
                return FloatWindow.get(tag!!)
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
                        mFloatCircleView?.setBackgroundResource(R.drawable.bg_float_rightcorner)
                        isLocationLeft = true
                    } else if (x >= Util.getScreenWidth(BaseApp.getApp()) - mFloatCircleView?.width!!) {
                        mFloatCircleView?.setBackgroundResource(R.drawable.bg_float_leftcorner)
                        isLocationLeft = false
                    } else {
                        mFloatCircleView?.setBackgroundResource(R.drawable.bg_float_nocorner)
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

    class AnimUtils {
        companion object {

            fun showAnimtorForAlpha(view: View?, startAlpha: Float, endAlpha: Float, animStartFunc: (() -> Unit)? = null, animEndFunc: (() -> Unit)? = null) {
                var animator = ObjectAnimator.ofFloat(view, "alpha", startAlpha, endAlpha)
                animator.duration = 200
                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        animEndFunc?.invoke()
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        animStartFunc?.invoke()
                    }
                })
                animator.start()
            }
        }
    }
}