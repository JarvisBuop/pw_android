package com.jdev.wandroid.ui.frg

import android.os.Bundle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.view.ViewGroup
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.kit.custom.photoview.PhotoView
import com.jdev.wandroid.R

/**
 * info: create by jd in 2019/12/9
 * @see:
 * @description: photoview 自定义view测试和改造
 *
 */
class PhotoViewTestFrag : BaseViewStubFragment() {
    override fun getViewStubId(): Int {
        return R.layout.app_frag_test
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        initScrollImg()
    }

    lateinit var mPager: androidx.viewpager.widget.ViewPager
    private fun initScrollImg() {
        mPager = mRootView.findViewById<androidx.viewpager.widget.ViewPager>(R.id.pager)
        val mPagerAdapter = object : androidx.viewpager.widget.PagerAdapter() {
            override fun getCount(): Int {
                return 3
            }

            override fun isViewFromObject(view: View, obj: Any): Boolean {
                return view === obj
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val view = PhotoView(mContext)
                val lp = androidx.viewpager.widget.ViewPager.LayoutParams()
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