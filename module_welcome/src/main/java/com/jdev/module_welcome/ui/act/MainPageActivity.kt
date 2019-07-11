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
 */
class MainPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_container)

    }

}