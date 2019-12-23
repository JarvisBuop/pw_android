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
import com.example.lib_imagefilter.filter.base.gpuimage.GPUImageFilter
import com.example.lib_imagefilter.filter.helper.MagicFilterType
import com.example.lib_imagefilter.widget.JdGPUDisplayView
import com.example.module_filter.utils.GPUImageFilterTools
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.module_video.R
import com.seu.magicfilter.filter.helper.FilterAdjuster
import com.seu.magicfilter.filter.helper.MagicFilterFactory
import kotlinx.android.synthetic.main.app_frag_jdgpu_single.*

/**
 * info: create by jd in 2019/12/20
 * @see:
 * @description:
 *
 */
class JdGpuImageSingleFrag :BaseViewStubFragment(){
    override fun getViewStubId(): Int {
        return R.layout.app_frag_jdgpu_single
    }

    companion object {
        private const val REQUEST_PHOTOPICKER = 1
        private const val REQUEST_STORAGE_PERMISSION = 2
    }

    private lateinit var filterType: MagicFilterType
    var progress: Int = 50
    var filterAdjuster: FilterAdjuster? = null

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        layout_controller.visibility = View.VISIBLE
        gpuImageView.setImage(BitmapFactory.decodeResource(mContext!!.resources, R.drawable.gpuimage_origin))

        filterType = MagicFilterType.CUSTOM_丑颜
        var filterName = filterType.name
        var filter: GPUImageFilter? = MagicFilterFactory.getFilterByType(filterType)
        filterAdjuster = FilterAdjuster(filter)
        switchFilterTo(filter, gpuImageView, filterAdjuster, filterName)

        txt_style_name.setOnClickListener {
            GPUImageFilterTools.showDialog(mContext!!, txt_style_name.text.toString()) { filter, name ->

                //设置filteradjuster , 切换filter;
                filterAdjuster = FilterAdjuster(filter)
                switchFilterTo(filter, gpuImageView, filterAdjuster, name)
            }
        }

        txt_style_action.setOnClickListener {
            if (filterAdjuster?.canAdjust() ?: false) {
                progress += 10
                if (progress > 100) {
                    progress = 0
                }
                txt_style_action.text = "++ ${filterAdjuster?.canAdjust()} " + if (filterAdjuster?.canAdjust()
                                ?: false) "$progress" else ""

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
                filterAdjuster = FilterAdjuster(filter)
                if (filterAdjuster?.adjuster == null) {
                    filterAdjuster?.adjuster = GPUImageFilterTools.createCustomAdjusterByFilter(filter)
                }
                switchFilterTo(filter, gpuImageView, filterAdjuster, name)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun switchFilterTo(filter: GPUImageFilter?, gpuImageView: JdGPUDisplayView, filterAdjuster: FilterAdjuster?,
                               name: String) {
        //ui
        txt_style_name.text = name
        txt_style_action.text = "++ ${filterAdjuster?.canAdjust()} " + if (filterAdjuster?.canAdjust() == true) "$progress" else ""

        //logic
        if (filter != null && (gpuImageView.filter == null || gpuImageView.filter.javaClass != filter.javaClass)) {
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