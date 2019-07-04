package com.jdev.module_welcome.ui.act

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.jdev.module_welcome.R
import com.jdev.module_welcome.adapter.BaseFragmentStatePagerAdapter
import com.jdev.module_welcome.ui.frag.KtChildBaseFragment
import kotlinx.android.synthetic.main.include_tab_viewpager.*

/**
 * info: create by jd in 2019/7/3
 * @see:
 * @description: 首页;
 *
 */
class MainPageActivity : AppCompatActivity() {

    private var mBaseFragmentStatePagerAdapter: BaseFragmentStatePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_mainpage_container)

        initViewData()
        initialLayouts()
    }

    private fun initViewData() {

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
//        common_viewpager.offscreenPageLimit = tabs.size
        common_viewpager.adapter = mBaseFragmentStatePagerAdapter
        common_tablayout.setViewPager(common_viewpager)
        common_viewpager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
            }
        })
    }

}