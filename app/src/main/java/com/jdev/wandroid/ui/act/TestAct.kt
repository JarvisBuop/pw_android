package com.jdev.wandroid.ui.act

import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import com.jdev.kit.custom.photoview.PhotoView
import com.jdev.wandroid.R

class TestAct : AppCompatActivity() {
    lateinit var mRoot:View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_test)
        mRoot = findViewById(R.id.layout_root)
        initScrollImg()
    }

    lateinit var mPager:ViewPager
    private fun initScrollImg() {
        mPager = findViewById<ViewPager>(R.id.pager)
        val mPagerAdapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return 3
            }

            override fun isViewFromObject(view: View, obj: Any): Boolean {
                return view === obj
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val view = PhotoView(this@TestAct)
                val lp = ViewPager.LayoutParams()
                view.layoutParams = lp
                view.enable()
                view.setImageResource(R.drawable.wx_notify_bg)

//                view.setOnClickListener { v -> finish() }
                container.addView(view)
                return view
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }
        }
        mPager.setAdapter(mPagerAdapter)

    }

}