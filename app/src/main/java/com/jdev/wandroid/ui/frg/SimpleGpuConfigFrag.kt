package com.jdev.wandroid.ui.frg

import android.graphics.BitmapFactory
import android.os.Bundle
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import com.seu.magicfilter.filter.advanced.MagicBlackCatFilter
import com.seu.magicfilter.filter.helper.MagicFilterType
import com.seu.magicfilter.filter.origin.GPUImageColorInvertFilter
import kotlinx.android.synthetic.main.app_frag_simple_gpuconfig.*

/**
 * info: create by jd in 2019/12/19
 * @see:
 * @description:
 *
 */
class SimpleGpuConfigFrag : BaseViewStubFragment() {


    override fun getViewStubId(): Int {
        return R.layout.app_frag_simple_gpuconfig
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        magicimageview.setImageBitmap(BitmapFactory.decodeResource(mContext!!.resources, R.drawable.gpuimage_origin))

        btn.setOnClickListener {
            magicimageview.setFilter(MagicFilterType.BLACKCAT)
        }
    }

}