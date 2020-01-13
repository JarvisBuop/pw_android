//package com.yhao.floatwindow.simple
//
//import android.graphics.drawable.AnimationDrawable
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.ProgressBar
//import com.example.fixedfloatwindow.R
//import com.yhao.floatwindow.FloatWindow
//import com.yhao.floatwindow.IFloatWindow
//import com.yhao.floatwindow.IFloatWindowImpl
//import com.yhao.floatwindow.MoveType
//
///**
// * info: create by jd in 2020/1/13
// * @see:
// * @description:
// *
// */
//class SimpleFloatUtils() {
//
//    companion object {
//        val TAG = "FloatUtils"
//
//        var mFloatRootView: View? = null
//        var pb: ProgressBar? = null
//        var img: ImageView? = null
//        var imgPlay: ImageView? = null
//        var rootCircle: View? = null
//        var rootViewGroup: View? = null
//        var recyclerView: RecyclerView? = null
//
//        private fun initFloatView(currentTag: String) {
//            mFloatRootView = LayoutInflater.from(BaseApp.getApp()).inflate(R.layout.app_float_controllbar, null)
//            pb = mFloatRootView!!.findViewById(R.id.float_pb)
//            img = mFloatRootView!!.findViewById(R.id.float_img)
//            imgPlay = mFloatRootView!!.findViewById(R.id.float_play)
//            rootCircle = mFloatRootView!!.findViewById(R.id.float_circle)
//            rootViewGroup = mFloatRootView!!.findViewById(R.id.float_viewgroup)
//            recyclerView = mFloatRootView!!.findViewById<RecyclerView>(R.id.float_recyclerview)
//
//            (img!!.drawable as AnimationDrawable).start()
//
//            //触摸关闭列表;
//            rootViewGroup?.setOnTouchListener { v, event ->
//                var floatImplByTag = getFloatImplByTag(currentTag)
//                if (floatImplByTag != null) {
//                    //动画;
//                    rootCircle?.visibility = View.VISIBLE
//                    rootViewGroup?.visibility = View.GONE
//
//                    if (floatImplByTag is IFloatWindowImpl) {
//                        var config = floatImplByTag.config
//                        config.setMoveType(MoveType.inactive)
//                        config.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
//                        config.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
//                        floatImplByTag.config = config
//                    }
//                }
//                return@setOnTouchListener true
//            }
//
//            mFloatRootView!!.setOnClickListener {
//
//                var floatImplByTag = getFloatImplByTag(currentTag)
//                if (floatImplByTag != null) {
//                    /**
//                     * 浮窗落地页 -> 弹出页;
//                     *
//                     * 方式一: 跳入一个新的activity页面;
//                     */
////                    rotateAnimator?.resume()
////                    bindReceiver()
////
////                    goLocationAct(BaseApp.getApp().applicationContext, floatImplByTag)
//
//
//                    /**
//                     * 方式二: 同一个浮窗控制不同view;
//                     */
//
//                    if (floatImplByTag is IFloatWindowImpl) {
//                        var config = floatImplByTag.config
//                        floatImplByTag.updateX(0)
//                        floatImplByTag.updateY(0)
//                        config.setMoveType(MoveType.inactive)
//                        config.setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
//                        config.setHeight(ViewGroup.LayoutParams.MATCH_PARENT)
//                        floatImplByTag.config = config
//                    }
//
//                    //显示列表项,并设置布局属性和是否能够滑动,设置列表加载动画;
//                    rootCircle?.visibility = View.GONE
//                    rootViewGroup?.visibility = View.VISIBLE
//
//                    var datas = arrayListOf<String>("1", "2", "3")
//                    recyclerView?.layoutManager = LinearLayoutManager(recyclerView?.context)
//                    recyclerView?.adapter = object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.app_float_item_include, datas) {
//                        override fun convert(helper: BaseViewHolder?, item: String?) {
//
//                        }
//                    }
//                }
//
//            }
//        }
//
//        private fun getFloatImplByTag(tag: String?): IFloatWindow? {
//            if (!StringUtils.isSpace(tag)) {
//                return FloatWindow.get(tag!!)
//            }
//            return null
//        }
//    }
//}