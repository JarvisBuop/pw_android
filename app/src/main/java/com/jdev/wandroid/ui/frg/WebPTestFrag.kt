package com.jdev.wandroid.ui.frg

import android.os.Bundle
import android.view.Gravity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.jdev.kit.baseui.BaseFragment
import com.jdev.wandroid.R
import com.jdev.wandroid.mockdata.MockData
import com.jdev.wandroid.popwindow.impl.KtVersionMainPop
import com.jdev.wandroid.utils.ViewUtils
import kotlinx.android.synthetic.main.app_frag_webp.*

/**
 * info: create by jd in 2019/12/9
 * @see:
 * @description:
 *
 */
class WebPTestFrag : BaseFragment() {
    lateinit var arr: Array<String>
    var index: Int = 0

    override fun getViewStubId(): Int {
        return R.layout.app_frag_webp
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {

        btn_test.setOnClickListener {
            initPop()
        }

        initWebp2()
    }

    private fun initWebp2() {
        setWebpInImage("file:///android_asset/small.webp")
        arr = MockData.ALPHA_WEBP

        img_btn.setOnClickListener {
            arr = MockData.ANIM_WEBP
            index = 0
            setWebpInImage(arr.get(index % arr.size))
        }

        img_webp.setOnClickListener {
            setWebpInImage(arr.get(index % arr.size))
            index++
        }

    }

    private fun initPop() {
        var pop = KtVersionMainPop(mContext!!, ViewUtils.OnCallback<Any> {

        })
        pop.showAtLocation(mRootView, Gravity.CENTER, 0, 0)
    }

    fun setWebpInImage(webpUrl: String): RequestOptions {
        val options = RequestOptions()
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        Glide.with(this)
                .load(webpUrl)
                .apply(options).transition(DrawableTransitionOptions().crossFade(200))
                .into(img_webp)
        return options
    }

}