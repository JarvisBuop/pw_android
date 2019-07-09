package com.jdev.module_welcome.ui.act

import android.animation.IntEvaluator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.graphics.drawable.ArgbEvaluator
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.jdev.module_welcome.R
import com.jdev.module_welcome.adapter.BaseFragmentV4StatePagerAdapter
import com.jdev.module_welcome.ui.frag.KtChildBaseFragment
import com.jdev.module_welcome.ui.widget.JdCustomHeader
import kotlinx.android.synthetic.main.act_mainpage_container.*
import kotlinx.android.synthetic.main.include_real_search_item.*
import kotlinx.android.synthetic.main.include_tab_viewpager.*


/**
 * info: create by jd in 2019/7/3
 * @see:
 * @description: 首页;
 *
 * 搜索框采用 隐藏fakeview 浮顶实现;
 *
 * tab 采用 处理滑动事件处理;
 *
 *
 * --------------------
 *
 * 2. scrollview 的滑动不连贯的解决;
 */
class MainPageActivity : AppCompatActivity() {
    val TAG: String = "MainPageActivity"
    var colorIsSetting = false
    var maxHeight: Int = 0
    var paddingOffset = ConvertUtils.dp2px(5f)

    private var mBaseFragmentV4StatePagerAdapter: BaseFragmentV4StatePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_mainpage_container)

        initViewData()
        initialLayouts()
    }


    private fun initViewData() {
        scrollView.post {
            fake_search_item.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
            scrollView.setTargetView(common_tablayout, fake_search_item.measuredHeight)

            //todo 需要与全面屏适配;
            var tabHeight = ScreenUtils.getScreenHeight() - fake_search_item.measuredHeight - BarUtils.getStatusBarHeight() - common_tablayout.measuredHeight
            common_viewpager.setFixHeight(tabHeight)
        }
        scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            //            animForAlphaColorBar()
            animForFlexMarginBar()
        })

        swipeRefreshView.setOnRefreshListener {
            it.finishRefresh(1000/*,false*/);//传入false表示刷新失败
        }
        swipeRefreshView.setRefreshHeader(JdCustomHeader(this))
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

    private fun initialLayouts() {
        val tabs = arrayOf(/*"精选",*/ "专题", "课程", "素材", "教案", "图库")
        mBaseFragmentV4StatePagerAdapter = BaseFragmentV4StatePagerAdapter(supportFragmentManager,
                arrayListOf(
                        KtChildBaseFragment() as Fragment,
                        KtChildBaseFragment(),
                        KtChildBaseFragment(),
                        KtChildBaseFragment(),
                        KtChildBaseFragment()),
                tabs)
        common_viewpager.offscreenPageLimit = tabs.size
        common_viewpager.adapter = mBaseFragmentV4StatePagerAdapter
        common_tablayout.setViewPager(common_viewpager)
        common_viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
            }
        })
    }

}