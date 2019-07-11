package com.jdev.module_welcome.ui.frag

import android.animation.IntEvaluator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.graphics.drawable.ArgbEvaluator
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v4.widget.NestedScrollView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.*
import com.jdev.module_welcome.R
import com.jdev.module_welcome.adapter.BaseFragmentV4StatePagerAdapter
import com.jdev.module_welcome.ui.helper.HeaderScrollHelper
import com.jdev.module_welcome.ui.widget.FixScrollView
import com.jdev.module_welcome.ui.widget.JdCustomHeader
import kotlinx.android.synthetic.main.act_mainpage_container.*
import kotlinx.android.synthetic.main.include_real_search_item.*
import kotlinx.android.synthetic.main.include_tab_viewpager.*

/**
 * info: create by jd in 2019/7/10
 * @see:
 * @description:
 *
 */
class MainPageFragment : Fragment() {
    val TAG: String = "MainPageFragment"
    var colorIsSetting = false
    var maxHeight: Int = 0
    var paddingOffset = ConvertUtils.dp2px(5f)

    private var mBaseFragmentV4StatePagerAdapter: BaseFragmentV4StatePagerAdapter? = null


    companion object {

        val KEY_BO = "BO"

        fun newInstance(bottomOffset: Int): MainPageFragment {
            var frag = MainPageFragment()
            var bundle = Bundle()
            frag.arguments = bundle
            bundle.putInt(KEY_BO, bottomOffset)
            return frag
        }

    }

    var verticalListener: ScrollVerticalDistanceListener? = null

    interface ScrollVerticalDistanceListener {
        fun scrollDistance(scrollPos: Int, tabMaxHeight: Int, downScroll: Boolean, isVisiable: Boolean)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.act_mainpage_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewData()
        initialLayouts()
    }

    private fun initViewData() {
        common_tablayout.viewTreeObserver.addOnGlobalLayoutListener {
            postInitTabView(arguments?.getInt(KEY_BO) ?: 0)
//                scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
        scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            //            animForAlphaColorBar()
            animForFlexMarginBar()

            LogUtils.e("OnScrollChangeListener", " x " + scrollX + " y " + scrollY + " oldx " + oldScrollX + " oldY " + oldScrollY + " scrollView.ScorllY: " + scrollView.scrollY
                    + " scrollView.maxScrollY " + scrollView.maxScrollY)

            if (verticalListener != null) {
                verticalListener?.scrollDistance(scrollY, scrollView.maxScrollY, oldScrollY > scrollY, false)
            }
        })

        scrollView.setOnScrollListener(object : FixScrollView.OnScrollListener {

            override fun scrolldownToLeaveThreshold(criticalH: Int, dy: Float) {
                scrollOtherListToTop(currentItem, fragments)
            }
        })

        swipeRefreshView.setOnRefreshListener {
            it.finishRefresh(1000/*,false*/);//传入false表示刷新失败
        }
        swipeRefreshView.setRefreshHeader(JdCustomHeader(activity!!))
    }

    fun postInitTabView(bottomOffset: Int) {
        fake_search_item.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        scrollView.setTargetView(common_tablayout, fake_search_item.measuredHeight)

        //todo 需要与全面屏适配;
        var tabHeight = ScreenUtils.getScreenHeight() - fake_search_item.measuredHeight - BarUtils.getStatusBarHeight() - common_tablayout.measuredHeight
        //再减去底部高度;
        tabHeight -= bottomOffset
        common_viewpager.setFixHeight(tabHeight)

    }

    /**
     * 动画二,设置隐藏bar无padding, realitem滑动过程中需要动态改变margin;
     * 同时内部view的间距不改变;
     *
     * 目标: realItem;
     */
    fun animForFlexMarginBar() {
        val location = IntArray(2)
        real_search_item.getLocationInWindow(location)

        Log.e("tagpage: ", " " + real_search_item.y + "  " + maxHeight)

        var criticalH = location[1] - BarUtils.getStatusBarHeight() + paddingOffset
        maxHeight = if (criticalH > maxHeight) criticalH else maxHeight
        //兼容阴影大小;
        var sideMargin = ConvertUtils.dp2px(6f)

        if (criticalH <= 0) {
            fake_search_item.visibility = View.VISIBLE
        } else {
            fake_search_item.visibility = View.GONE

            var fration = criticalH * 1.0f / maxHeight
            var intEvaluator = IntEvaluator()
            var evaluate = intEvaluator.evaluate(fration, 0, sideMargin)

            var layoutParams = real_search_item.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.leftMargin = evaluate
            layoutParams.rightMargin = evaluate
            real_search_item.layoutParams = layoutParams

            var marginLayoutParams = item_inner_four_shortcut.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.leftMargin = sideMargin - evaluate
            marginLayoutParams.rightMargin = sideMargin - evaluate
            item_inner_four_shortcut.layoutParams = marginLayoutParams
        }
    }

    /**
     * 原需求,设置隐藏的bar有padding; fakeitem 改变背景色;
     *
     * 目标: fakeItem;
     */
    @SuppressLint("RestrictedApi")
    fun animForAlphaColorBar() {
        val location = IntArray(2)
        real_search_item.getLocationInWindow(location)

        var criticalH = location[1] - BarUtils.getStatusBarHeight() /*+ fake_search_item.measuredHeight*/
        if (criticalH < 0) {
            fake_search_item.visibility = View.VISIBLE

            val maxColorHeight = 100 //最大滑动颜色改变;
            var relativeHeight = Math.abs(criticalH)
            var fration = relativeHeight * 1.0f / maxColorHeight
            if (fration < 1 && !colorIsSetting) {
                var argbEvaluator = ArgbEvaluator.getInstance()
                var evaluate = argbEvaluator.evaluate(fration, Color.argb(0, 255, 255, 255), Color.argb(255, 255, 255, 255))
                fake_search_item.setBackgroundColor(evaluate as Int)
            } else {
                colorIsSetting = true
                fake_search_item.setBackgroundColor(Color.argb(255, 255, 255, 255))
            }
        } else {
            colorIsSetting = false
            fake_search_item.visibility = View.GONE
        }
    }


    private lateinit var mCurrentFragment: KtChildBaseFragment
    private lateinit var fragments: ArrayList<Fragment>
    private var currentItem: Int = 0
    private fun initialLayouts() {
        val tabs = arrayOf(/*"精选",*/ "专题", "课程", "素材", "教案", "图库")
        fragments = arrayListOf(
                KtChildBaseFragment() as Fragment,
                KtChildBaseFragment(),
                KtChildBaseFragment(),
                KtChildBaseFragment(),
                KtChildBaseFragment())
        mBaseFragmentV4StatePagerAdapter = BaseFragmentV4StatePagerAdapter(childFragmentManager,
                fragments,
                tabs)
        common_viewpager.offscreenPageLimit = tabs.size
        common_viewpager.adapter = mBaseFragmentV4StatePagerAdapter
        common_tablayout.setViewPager(common_viewpager)

        setCurrentContainer(fragments, currentItem)
        common_viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                currentItem = position
                setCurrentContainer(fragments, currentItem)
            }
        })
    }

    fun setCurrentContainer(fragments: ArrayList<Fragment>, position: Int) {
        try {
            val fragment = fragments.get(position)
            mCurrentFragment = fragment as KtChildBaseFragment
            scrollView.setCurrentScrollableContainer(mCurrentFragment)
        } catch (ignore: Exception) {
            ToastUtils.showLong("error")
        }
    }

    fun scrollOtherListToTop(current: Int, fragments: ArrayList<Fragment>) {
        try {
            for (i in fragments.indices) {
                val fragment = fragments.get(i) as KtChildBaseFragment
                HeaderScrollHelper.scrollToTopNoAnimator(fragment.scrollableView)
            }
        } catch (ignore: Exception) {
            ToastUtils.showLong("error")
        }
    }
}