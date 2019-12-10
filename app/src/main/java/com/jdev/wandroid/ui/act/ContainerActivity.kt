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
        val EXTRA_KEY = "KEY"

        //----------------
        val KEY_GESTURE = 0
        val KEY_PHOTOVIEW = 1
        val KEY_SHADOW = 2
        val KEY_WEBP = 3

        //----------------
        val KEY_ANDROID_GPUIMAGE = 4

        fun launch(mContext: Context, code: Int) {
            mContext.startActivity(
                    Intent(mContext, ContainerActivity::class.java)
                            .putExtra(EXTRA_KEY, code)
            )
        }

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
                    return GpuImageTestFrag()
                }

                else -> return null
            }
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