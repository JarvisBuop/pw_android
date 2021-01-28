package com.example.module_filter.ui.act

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.blankj.utilcode.util.StringUtils
import com.example.module_filter.R
import com.example.module_filter.ui.frag.JdGpuImageCameraFrag
import com.example.module_filter.ui.frag.JdGpuImageMultiFrag
import com.example.module_filter.ui.frag.JdGpuImageSingleFrag
import com.jdev.kit.baseui.BaseActivity
import com.jdev.kit.baseui.BaseFragment
import com.jdev.wandroid.ui.frg.*
import kotlinx.android.synthetic.main.act_main.*

/**
 * info: create by jd in 2019/12/23
 * @see:
 * @description:
 *
 */

const val KEY_ANDROID_JDGPU_SINGLE = "JdGpu 图片滤镜"
const val KEY_ANDROID_JDGPU_CAMERA = "JdGpu 视频滤镜"
const val KEY_ANDROID_JDGPU_MULTI = "JdGpu 效果图"
const val KEY_ANDROID_GPUIMAGE_SIMPLE = "GPUImageView 图片滤镜"
const val KEY_ANDROID_GPUIMAGE = "GPUImageView 图片滤镜 在rv中"
const val KEY_ANDROID_GPUIMAGE_CAMERA = "GPUImageView 原demo"
const val KEY_ANDROID_MAGIC_CAMERA = "MagicFilter 视频滤镜原demo"
const val KEY_ANDROID_GPU_TEST = "MagicFilter 图片滤镜原demo"

class MainActivity : BaseActivity() {

    val array = arrayOf(
            KEY_ANDROID_JDGPU_SINGLE,
            KEY_ANDROID_JDGPU_CAMERA,
            KEY_ANDROID_JDGPU_MULTI,
            KEY_ANDROID_GPUIMAGE_SIMPLE,
            KEY_ANDROID_GPUIMAGE,
            KEY_ANDROID_GPUIMAGE_CAMERA,
            KEY_ANDROID_MAGIC_CAMERA,
            KEY_ANDROID_GPU_TEST
    )

    //当前入口;
    var key: String? = null
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

        fun getFragmentByKey(code: String?): BaseFragment? {
            return when (code) {
                KEY_ANDROID_JDGPU_SINGLE -> JdGpuImageSingleFrag()
                KEY_ANDROID_JDGPU_CAMERA -> JdGpuImageCameraFrag()
                KEY_ANDROID_JDGPU_MULTI -> JdGpuImageMultiFrag()
                KEY_ANDROID_GPUIMAGE -> GpuImageMultiImageFrag()
                KEY_ANDROID_GPUIMAGE_SIMPLE -> GpuImageSingleImageFrag()
                KEY_ANDROID_GPUIMAGE_CAMERA -> GpuImageCameraFrag()
                KEY_ANDROID_MAGIC_CAMERA -> GpuMagicCameraFrag()
                KEY_ANDROID_GPU_TEST -> GpuMagicSingleFrag()
                else -> return null
            }
        }
    }

    override fun initIntentData(): Boolean {
        return true
    }

    override fun customOperate(savedInstanceState: Bundle?) {
        key = intent.getStringExtra(EXTRA_KEY) ?: KEY_ANDROID_JDGPU_SINGLE
        btn_select.setOnClickListener {
            AlertDialog.Builder(this).setItems(array) { dialog, which ->
                key = array[which]
                btn_select.text = key
            }.create().show()
        }
        btn_retry.setOnClickListener {
            isPermission({ fillContainer() }, permission, permissions)
        }
    }

    fun fillContainer() {
        currentFrag = getFragmentByKey(key)
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