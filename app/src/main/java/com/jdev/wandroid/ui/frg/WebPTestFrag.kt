package com.jdev.wandroid.ui.frg

import android.media.Image
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.jdev.kit.baseui.BaseFragment
import com.jdev.kit.baseui.BaseViewStubFragment
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
class WebPTestFrag : BaseViewStubFragment() {
    lateinit var arr: Array<String>
    var index: Int = 0

    lateinit var img_webp:ImageView
    lateinit var btn_test:TextView
    lateinit var img_btn:TextView

    override fun getViewStubId(): Int {
        return R.layout.app_frag_webp
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        img_webp = findView(R.id.img_webp)
        btn_test = findView(R.id.btn_test)
        img_btn = findView(R.id.img_btn)

        btn_test.setOnClickListener {
            initPop()
        }

        initWebp2()
    }

    private fun initWebp2() {
        setWebpInImage("file:///android_asset/small.webp")
        arr = MockData.ALPHA_WEBP

        img_btn.setOnClickListener {
            //clear
            arr = MockData.ANIM_WEBP
            index = 0
            setWebpInImage(arr.get(index % arr.size))
        }

        img_webp.setOnClickListener {
            //next
            setWebpInImage(arr.get(index % arr.size))
            index++
        }

    }

    private fun initPop() {
        var pop = KtVersionMainPop(mContext!!, ViewUtils.OnCallback<Any> {

        })
        pop.showAtLocation(mRootView, Gravity.CENTER, 0, 0)
    }

    //某些机型so包不兼容,是freco的so问题,可根据log查相关资料;
    fun setWebpInImage(webpUrl: String): RequestOptions {
        val options = RequestOptions()
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        Glide.with(activity!!)
                .load(webpUrl)
                .apply(options).transition(DrawableTransitionOptions().crossFade(200))
                .into(img_webp)
        return options
    }

}