package com.example.module_filter.ui.act

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import com.jarvisdong.kotlindemo.ui.BaseActivity
import com.jdev.kit.baseui.BaseFragment
import com.jdev.module_video.R
import com.jdev.wandroid.ui.frg.JdGpuImageCameraFrag
import com.jdev.wandroid.ui.frg.JdGpuImageMultiFrag
import com.jdev.wandroid.ui.frg.JdGpuImageSingleFrag
import kotlinx.android.synthetic.main.act_main.*

/**
 * info: create by jd in 2019/12/23
 * @see:
 * @description:
 *
 */

const val KEY_ANDROID_JDGPU_SINGLE = 10
const val KEY_ANDROID_JDGPU_CAMERA = 11
const val KEY_ANDROID_JDGPU_MULTI = 12

class MainActivity : BaseActivity() {
    var intKey = KEY_ANDROID_JDGPU_SINGLE
    var currentFrag: BaseFragment? = null

    override fun getViewStubId(): Int {
        return R.layout.act_main
    }

    var callback: (() -> Unit)? = null
    var permission = Manifest.permission.CAMERA
    var permissions = arrayOf(
            permission,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    companion object {
        const val EXTRA_KEY = "KEY"

        fun getFragmentByKey(code: Int): BaseFragment? {
            when (code) {
                KEY_ANDROID_JDGPU_SINGLE -> {
                    return JdGpuImageSingleFrag()
                }
                KEY_ANDROID_JDGPU_CAMERA -> {
                    return JdGpuImageCameraFrag()
                }
                KEY_ANDROID_JDGPU_MULTI -> {
                    return JdGpuImageMultiFrag()
                }
                else -> return null
            }
        }
    }

    override fun initIntentData(): Boolean {
        intKey = intent.getIntExtra(EXTRA_KEY, intKey)
        return intKey != -1
    }

    override fun customOperate(savedInstanceState: Bundle?) {
        btn_retry.setOnClickListener {
            isPermission({ fillContainer() }, permission, permissions)
        }
        isPermission({ fillContainer() }, permission, permissions)
    }

    fun fillContainer() {
        currentFrag = getFragmentByKey(intKey)
        if (currentFrag != null) {
            layout_fragment_container.removeAllViews()
            setTextMarkTips(currentFrag!!.javaClass.simpleName)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.layout_fragment_container, currentFrag!!, currentFrag!!.javaClass.simpleName)
                    .commitAllowingStateLoss()
        } else {
            noDataOperate()
        }
    }

    override fun onResume() {
        super.onResume()
        currentFrag?.onResume()
    }

    fun isPermission(callback: (() -> Unit)? = null, permission: String, permissions: Array<out String>, isRequestPermission: Boolean = true): Boolean {
        if (!isRequestPermission) {
            return true
        }
        var checkSelfPermission = PermissionChecker.checkSelfPermission(this, permission)
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            callback?.invoke()
            return true
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1 && permissions.size == 3 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED
        ) {
            callback?.invoke()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

}