package com.jdev.module_welcome.ui.act

import android.app.Fragment
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.jdev.module_welcome.R
import com.jdev.module_welcome.adapter.BaseFragmentStatePagerAdapter

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
    }

    private fun initViewData() {

    }

    private fun initialLayouts() {
//        val tabs = arrayOf(/*"精选",*/ "专题", "课程", "素材", "教案", "图库")
//        mBaseFragmentStatePagerAdapter = BaseFragmentStatePagerAdapter(supportFragmentManager,
//                arrayListOf(
////                        KtChoicenessFragment() as Fragment,
//                        KtMemberSpecialSubjectFragment() as Fragment,
//                        KtMemberCourseFragment(),
//                        KtMemberMaterialFragment(),
//                        KtTeachingPlanFragment(),
//                        KtMemberAlbumFragment()),
//                tabs)
//        viewPager.offscreenPageLimit = tabs.size
//
//        viewPager.adapter = mBaseFragmentStatePagerAdapter
//        slidingTabLayout.setViewPager(viewPager)
//        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
//            override fun onPageSelected(position: Int) {
//                isMaterial = position == 2
//
//                try {
//                    MobclickAgentHelper.onSimpleEvent("hy_dbbq", tabs[position])
//                } catch (e: Exception) {
//                }
//            }
//        })
    }

}