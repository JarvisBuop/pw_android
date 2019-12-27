package com.jdev.module_welcome.ui.frag

import android.animation.IntEvaluator
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.jdev.kit.baseui.BaseFragment
import com.jdev.kit.helper.HeaderScrollHelper
import com.jdev.module_welcome.R
import com.jdev.module_welcome.adapter.BaseFragmentV4StatePagerAdapter
import com.jdev.module_welcome.ui.widget.JdCustomHeader
import kotlinx.android.synthetic.main.mw_act_mainpage_container.*
import kotlinx.android.synthetic.main.mw_include_real_search_item.*
import java.util.ArrayList

/**
 * info: create by jd in 2019/12/10
 * @see:
 * @description:
 *
 */
class HomeFragment : BaseFragment() {
    private lateinit var mCurrentFragment: KtChildBaseFragment
    private var fragments: ArrayList<Fragment> = arrayListOf()
    private var tabs: ArrayList<String> = arrayListOf("")
    private var currentItem: Int = 1
    private var fakeIsShow: Boolean = false
    var verticalListener: ScrollVerticalDistanceListener? = null

    interface ScrollVerticalDistanceListener {
        fun scrollDistance(scrollPos: Int, tabMaxHeight: Int)
    }

    companion object {
        val searchItemHeight = ConvertUtils.dp2px(60f)
        val tabHeight = ConvertUtils.dp2px(45f)
        val floatViewMarginTop = ConvertUtils.dp2px(132f)
        //兼容阴影padding;
        val paddingOffset = ConvertUtils.dp2px(6f)

    }

    override fun getLayoutId(): Int {
        return R.layout.mw_act_mainpage_container
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {

        swipeRefreshView.setOnRefreshListener {
            if (swipeRefreshView != null) {
                swipeRefreshView.finishRefresh(1000/*,false*/);//传入false表示刷新失败
            }
        }
        swipeRefreshView.setRefreshHeader(JdCustomHeader(mContext!!))

        scrollView.setTopOffset(searchItemHeight + tabHeight + getStateBarByVersion())
        scrollView.setOnScrollListener { currentY, maxScrollY ->
            //            LogUtils.e(TAG, " currentY $currentY maxScrollY $maxScrollY")
            if (verticalListener != null) {
                verticalListener?.scrollDistance(currentY, maxScrollY)
                val isMainRocketNow = currentY >= maxScrollY
                if (!isMainRocketNow) {
                    scrollOtherListToTop(currentItem, fragments)
                }
            }
            animForFlexMarginBar(currentY, floatViewMarginTop - getStateBarByVersion() + paddingOffset)
        }

        common_viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                currentItem = position
                setCurrentContainer(fragments, currentItem)
            }
        })

        fetchDatas()
    }

    private fun fetchDatas() {
        tabs.clear()
        fragments.clear()
        //更新tab
        tabs.addAll(arrayOf("测试1", "测试1", "测试1", "测试1", "测试1"))
        fragments.addAll(getFragmentByTabs(tabs))
        notifyTabAdapterChanged()
    }

    private fun getFragmentByTabs(tabs: ArrayList<String>): ArrayList<Fragment> {
        var fragments: ArrayList<Fragment> = arrayListOf()
        for (i in tabs.indices) {
            fragments.add(KtChildBaseFragment.newInstance(i) { onInitView(i) })
        }
        return fragments
    }

    fun notifyTabAdapterChanged() {
        var mBaseFragmentStatePagerAdapter = BaseFragmentV4StatePagerAdapter(fragmentManager, fragments, tabs)
        common_viewpager.offscreenPageLimit = 1
        common_viewpager.adapter = mBaseFragmentStatePagerAdapter
        common_tablayout.setViewPager(common_viewpager)

        common_viewpager.currentItem = currentItem

        //无效,没有初始化;
//        setCurrentContainer(fragments, currentItem)
    }

    private fun getStateBarByVersion(): Int {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) BarUtils.getStatusBarHeight() else 0
    }

    fun animForFlexMarginBar(currentY: Int, maxScrollY: Int) {
        //兼容阴影大小;
        var sideMargin = ConvertUtils.dp2px(6f)

        if (currentY >= maxScrollY) {
            fakeIsShow = true
            fake_search_item_compat.visibility = View.VISIBLE
        } else {
            fakeIsShow = false
            fake_search_item_compat.visibility = View.GONE

            var fration = currentY * 1.0f / maxScrollY
            var intEvaluator = IntEvaluator()
            var evaluate = intEvaluator.evaluate(fration, sideMargin, -sideMargin)

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

    //外部使用滑到头部;
    fun scrollToTop() {
        if (scrollView != null) {
            //全部滑动到顶;
            scrollView.scrollTo(0, 0)
            scrollOtherListToTop(currentItem, fragments)
        }
    }

    fun setCurrentContainer(fragments: ArrayList<Fragment>, position: Int) {
        try {
            if (position < fragments.size) {
                val fragment = fragments.get(position)
                mCurrentFragment = fragment as KtChildBaseFragment
                scrollView.setCurrentScrollableContainer(mCurrentFragment)
            }
        } catch (ignore: Exception) {
            LogUtils.e("error")
        }
    }

    fun scrollOtherListToTop(current: Int, fragments: ArrayList<Fragment>) {
        try {
            for (i in fragments.indices) {
                val fragment = fragments.get(i) as KtChildBaseFragment
                if (fragment.isVisible) {
                    HeaderScrollHelper.scrollToTopNoAnimator(fragment.scrollableView)
                }
            }
        } catch (ignore: Exception) {
            LogUtils.e("error")
        }
    }

    fun onInitView(currentPos: Int) {
        if (currentPos == currentItem) {
            setCurrentContainer(fragments, currentItem)
        }
    }
}