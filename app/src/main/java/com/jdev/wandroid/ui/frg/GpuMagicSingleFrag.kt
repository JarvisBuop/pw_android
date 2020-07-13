package com.jdev.wandroid.ui.frg

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import com.seu.magicfilter.filter.helper.MagicFilterType
import com.seu.magicfilter.widget.MagicImageView
import kotlinx.android.synthetic.main.app_frag_magic_singleimage.*

/**
 * info: create by jd in 2019/12/19
 * @see:
 * @description:
 *
 */
class GpuMagicSingleFrag : BaseViewStubFragment() {

    lateinit var magicimageview: MagicImageView
    lateinit var btn: View
    override fun getViewStubId(): Int {
        return R.layout.app_frag_magic_singleimage
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        magicimageview = findView(R.id.magicimageview)
        btn = findView(R.id.btn)
        magicimageview.setImageBitmap(BitmapFactory.decodeResource(mContext!!.resources, R.drawable.gpuimage_origin))
//        magicimageview.setFilter(MagicFilterType.BLACKCAT)

        btn.setOnClickListener {
            magicimageview.setFilter(MagicFilterType.BLACKCAT)
        }
    }

}