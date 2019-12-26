package com.jdev.wandroid.ui.act

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import com.jarvisdong.kotlindemo.ui.BaseActivity
import com.jdev.kit.baseui.BaseFragment
import com.jdev.wandroid.R
import com.jdev.wandroid.ui.frg.*
import kotlinx.android.synthetic.main.app_activity_container.*

/**
 * info: create by jd in 2019/12/9
 * @see:
 * @description:
 *
 */
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
const val KEY_ANDROID_GPU_TEST = 9
const val KEY_ANDROID_OPENGL_SIMGLE_DEMO = 8

//------------window-------------
const val KEY_ANDROID_FLOAT_WINDOW = 10

class ContainerActivity : BaseActivity() {
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
                    return GpuImageMultiImageFrag()
                }
                KEY_ANDROID_GPUIMAGE_SIMPLE -> {
                    return GpuImageSingleImageFrag()
                }
                KEY_ANDROID_GPUIMAGE_CAMERA -> {
                    return GpuImageCameraFrag()
                }
                KEY_ANDROID_MAGIC_CAMERA -> {
                    return GpuMagicCameraFrag()
                }
                KEY_ANDROID_OPENGL_SIMGLE_DEMO -> {
                    return SimpleOpenglDemoFrag()
                }
                KEY_ANDROID_GPU_TEST -> {
                    return GpuMagicSingleFrag()
                }
                KEY_ANDROID_FLOAT_WINDOW ->{
                    return FloatWindowFrag()
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