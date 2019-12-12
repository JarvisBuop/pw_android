package com.jdev.wandroid.ui.act

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jarvisdong.kotlindemo.ui.BaseActivity
import com.jdev.kit.baseui.BaseFragment
import com.jdev.wandroid.R
import com.jdev.wandroid.ui.frg.*

/**
 * info: create by jd in 2019/12/9
 * @see:
 * @description:
 *
 */
class ContainerActivity : BaseActivity() {

    companion object {
        const val EXTRA_KEY = "KEY"

        //--------selftest--------
        const val KEY_GESTURE = 0
        const val KEY_PHOTOVIEW = 1
        const val KEY_SHADOW = 2
        const val KEY_WEBP = 3

        //-------gpuimage---------
        const val KEY_ANDROID_GPUIMAGE = 4
        const val KEY_ANDROID_GPUIMAGE_SIMPLE = 5
        const val KEY_ANDROID_GPUIMAGE_CAMERA = 6
        const val KEY_ANDROID_MAGIC_CAMERA = 7

        fun getFragmentByKey(code: Int): BaseFragment? {
            when (code) {
                KEY_GESTURE -> {
                    return GestureTestFrag()
                }
                KEY_PHOTOVIEW -> {
                    return PhotoViewTestFrag()
                }
                KEY_SHADOW -> {
                    return ShadowTestFrag()
                }
                KEY_WEBP -> {
                    return WebPTestFrag()
                }
                KEY_ANDROID_GPUIMAGE -> {
                    return GpuImageTestMultiImageFrag()
                }
                KEY_ANDROID_GPUIMAGE_SIMPLE -> {
                    return GpuImageTestSingleImageFrag()
                }
                KEY_ANDROID_GPUIMAGE_CAMERA -> {
                    return GpuImageCameraFrag()
                }
                KEY_ANDROID_MAGIC_CAMERA -> {
                    return GpuMagicCameraFrag()
                }

                else -> return null
            }
        }

        fun launch(mContext: Context, code: Int) {
            mContext.startActivity(
                    Intent(mContext, ContainerActivity::class.java)
                            .putExtra(EXTRA_KEY, code)
            )
        }
    }

    var intKey = -1
    var currentFrag: BaseFragment? = null

    override fun getViewStubId(): Int {
        return R.layout.app_activity_container
    }

    override fun initIntentData(): Boolean {
        intKey = intent.getIntExtra(EXTRA_KEY, -1)
        return intKey != -1
    }

    override fun customOperate(savedInstanceState: Bundle?) {
        currentFrag = getFragmentByKey(intKey)
        if (currentFrag != null) {
            setTextMarkTips(currentFrag!!.javaClass.simpleName)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.layout_fragment_container, currentFrag!!, currentFrag!!.javaClass.simpleName)
                    .commitAllowingStateLoss()
        } else {
            noDataOperate()
        }
    }

}