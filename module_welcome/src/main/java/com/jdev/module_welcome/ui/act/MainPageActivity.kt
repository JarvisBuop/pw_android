package com.jdev.module_welcome.ui.act

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.graphics.drawable.ArgbEvaluator
import android.support.v4.view.ViewPager
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import com.jdev.module_welcome.R
import com.jdev.module_welcome.adapter.BaseFragmentStatePagerAdapter
import com.jdev.module_welcome.ui.frag.KtChildBaseFragment
import kotlinx.android.synthetic.main.act_mainpage_container.*
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
 * 1. 内嵌recyclerview 需要定义滑动到底部去设置parent的是否拦截的滑动冲突;
 *
 * 2. scrollview 的滑动不连贯的解决;
 *
 * 3.scrollview 滑到底,不拦截;
 */
class MainPageActivity : AppCompatActivity() {
    val TAG: String = "MainPageActivity"
    var colorIsSetting = false

    private var mBaseFragmentStatePagerAdapter: BaseFragmentStatePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_mainpage_container)

        initViewData()
        initialLayouts()
    }

    @SuppressLint("RestrictedApi")
    private fun initViewData() {
        scrollView.post {
            fake_search_item.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
            scrollView.setTargetView(common_tablayout, fake_search_item.measuredHeight)

            var tabHeight = ScreenUtils.getScreenHeight() - fake_search_item.measuredHeight - BarUtils.getStatusBarHeight() - common_tablayout.measuredHeight
            common_viewpager.setFixHeight(tabHeight)
        }
        scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
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
        })
    }

    private fun initialLayouts() {
        val tabs = arrayOf(/*"精选",*/ "专题", "课程", "素材", "教案", "图库")
        mBaseFragmentStatePagerAdapter = BaseFragmentStatePagerAdapter(supportFragmentManager,
                arrayListOf(
//                        KtChoicenessFragment() as Fragment,
                        KtChildBaseFragment() as android.support.v4.app.Fragment,
                        KtChildBaseFragment(),
                        KtChildBaseFragment(),
                        KtChildBaseFragment(),
                        KtChildBaseFragment()),
                tabs)
        common_viewpager.offscreenPageLimit = tabs.size
        common_viewpager.adapter = mBaseFragmentStatePagerAdapter
        common_tablayout.setViewPager(common_viewpager)
        common_viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
            }
        })
    }

}