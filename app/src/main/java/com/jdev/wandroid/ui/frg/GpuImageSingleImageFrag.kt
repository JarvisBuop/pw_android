package com.jdev.wandroid.ui.frg

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import com.jdev.wandroid.mockdata.FilterType
import com.jdev.wandroid.utils.gputils.GPUImageFilterTools
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.android.synthetic.main.app_item_gpuimage.*

/**
 * info: create by jd in 2019/12/10
 * @see:
 * @description: gpuimage test
 *
 * https://github.com/cats-oss/android-gpuimage
 *
 * Android filters based on OpenGL (idea from GPUImage for iOS)
 */
class GpuImageSingleImageFrag : BaseViewStubFragment() {
    companion object {
        private const val REQUEST_PHOTOPICKER = 1
        private const val REQUEST_STORAGE_PERMISSION = 2
    }

    var progress: Int = 50
    var filterAdjuster: GPUImageFilterTools.FilterAdjuster? = null
    override fun getViewStubId(): Int {
        return R.layout.app_item_gpuimage
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        layout_controller.visibility = View.VISIBLE

        var filterType = FilterType.CUSTOM_丑颜
        var filterName = filterType.name
        var filter: GPUImageFilter = GPUImageFilterTools.createFilterForType(mContext!!, filterType)
        filterAdjuster = GPUImageFilterTools.FilterAdjuster(filter)

        gpuImageView.setImage(BitmapFactory.decodeResource(mContext!!.resources, R.drawable.gpuimage_origin))
        switchFilterTo(filter, gpuImageView, filterAdjuster, filterName)

        txt_style_name.setOnClickListener {
            GPUImageFilterTools.showDialog(mContext!!, txt_style_name.text.toString()) { filter, name ->

                //设置filteradjuster , 切换filter;
                filterAdjuster = GPUImageFilterTools.FilterAdjuster(filter)
                switchFilterTo(filter, gpuImageView, filterAdjuster, name)
            }
        }

        txt_style_action.setOnClickListener {
            if (filterAdjuster?.canAdjust()?:false) {
                progress += 10
                if (progress > 100) {
                    progress = 0
                }
                txt_style_action.text = "++ ${filterAdjuster?.canAdjust()} " + if (filterAdjuster?.canAdjust()?:false) "$progress" else ""

                //更新gpuimage;
                filterAdjuster?.adjust(progress)
                gpuImageView.requestRender()
            }
        }

        txt_style_save.setOnClickListener {
            if (!hasCameraPermission() || !hasStoragePermission()) {
                ActivityCompat.requestPermissions(
                        activity!!,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                        REQUEST_STORAGE_PERMISSION
                )
            } else {
                saveImage()
            }
        }

        txt_style_select.setOnClickListener {
            startPhotoPicker()
        }

        txt_style_custom.setOnClickListener {
            GPUImageFilterTools.showCustomFilterDialog(mContext!!, txt_style_name.text.toString()) { filter, name ->
                //设置filteradjuster , 切换filter;
                filterAdjuster = GPUImageFilterTools.FilterAdjuster(filter)
                switchFilterTo(filter, gpuImageView, filterAdjuster, name)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun switchFilterTo(filter: GPUImageFilter, gpuImageView: GPUImageView, filterAdjuster: GPUImageFilterTools.FilterAdjuster?,
                               name: String) {
        //ui
        txt_style_name.text = name
        txt_style_action.text = "++ ${filterAdjuster?.canAdjust()} " + if (filterAdjuster?.canAdjust() == true) "$progress" else ""

        //logic
        if (gpuImageView.filter == null || gpuImageView.filter.javaClass != filter.javaClass) {
            gpuImageView.filter = filter
            if (filterAdjuster?.canAdjust() == true) {
                filterAdjuster?.adjust(progress)
            }
        }

        gpuImageView.requestRender()
    }

    /**
     * 保存文件至指定路径;
     * 返回一个contentprovider的路径;
     */
    private fun saveImage() {
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        gpuImageView.saveToPictures("GPUImage", fileName) { uri ->
            Toast.makeText(mContext!!, "Saved: $uri", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startPhotoPicker() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, REQUEST_PHOTOPICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PHOTOPICKER -> if (resultCode == Activity.RESULT_OK) {
                gpuImageView.setImage(data!!.data)
                image_origin.setImageURI(data!!.data)
            } else {
                ToastUtils.showShort("error result")
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_STORAGE_PERMISSION && grantResults.size == 3
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
        ) {
            saveImage()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    //---------------------------
    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}