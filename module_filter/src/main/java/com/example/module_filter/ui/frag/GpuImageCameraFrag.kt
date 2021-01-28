package com.jdev.wandroid.ui.frg

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.example.libimagefilter.filter.base.gpuimage.GPUImageFilter
import com.example.libimagefilter.filter.helper.FilterAdjuster
import com.example.libimagefilter.utils.Rotation
import com.example.libimagefilter.widgetimport.gpuwidget.GPUImageView
import com.example.module_filter.R
import com.example.module_filter.utils.GPUImageFilterTools
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.utils.gputils.Camera1Loader
import com.jdev.wandroid.utils.gputils.Camera2Loader
import com.jdev.wandroid.utils.gputils.CameraLoader
import com.jdev.wandroid.utils.gputils.doOnLayout

/**
 * info: create by jd in 2019/12/12
 * @see:
 * @description: gpuimage camera
 * @description: gpuimage camera
 *
 */
class GpuImageCameraFrag : BaseViewStubFragment() {
    private val cameraLoader: CameraLoader by lazy {
        if (Build.VERSION.SDK_INT < 21) {
            Camera1Loader(activity!!)
        } else {
            Camera2Loader(activity!!)
        }
    }
    private var filterAdjuster: FilterAdjuster? = null

    private lateinit var seekBar:SeekBar
    private lateinit var button_choose_filter:TextView
    private lateinit var button_capture:ImageView
    private lateinit var img_switch_camera:ImageView
    private lateinit var gpuImageView: GPUImageView

    override fun getViewStubId(): Int {
        return R.layout.app_frag_gpucamera
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        seekBar = findView(R.id.seekBar)
        button_choose_filter = findView(R.id.button_choose_filter)
        button_capture = findView(R.id.button_capture)
        img_switch_camera = findView(R.id.img_switch_camera)
        gpuImageView = findView(R.id.gpuImageView)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                filterAdjuster?.adjust(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        button_choose_filter.setOnClickListener {
            GPUImageFilterTools.showDialog(mContext!!) { filter, name -> switchFilterTo(filter) }
        }
        button_capture.setOnClickListener {
            saveSnapshot()
        }
        img_switch_camera.run {
            if (!cameraLoader.hasMultipleCamera()) {
                visibility = View.GONE
            }
            setOnClickListener {
                cameraLoader.switchCamera()
                gpuImageView.setRotation(getRotation(cameraLoader.getCameraOrientation()))
            }
        }
        cameraLoader.setOnPreviewFrameListener { data, width, height ->
            gpuImageView.updatePreviewFrame(data, width, height)
        }
        gpuImageView.setRotation(getRotation(cameraLoader.getCameraOrientation()))
        gpuImageView.setRenderMode(GPUImageView.RENDERMODE_CONTINUOUSLY)
    }

    override fun onResume() {
        super.onResume()
        gpuImageView.doOnLayout {
            cameraLoader.onResume(it.width, it.height)
        }
    }

    override fun onPause() {
        cameraLoader.onPause()
        super.onPause()
    }

    private fun saveSnapshot() {
        val folderName = "GPUImage"
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        gpuImageView.saveToPictures(folderName, fileName) {
            Toast.makeText(activity, "$folderName/$fileName saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRotation(orientation: Int): Rotation {
        return when (orientation) {
            90 -> Rotation.ROTATION_90
            180 -> Rotation.ROTATION_180
            270 -> Rotation.ROTATION_270
            else -> Rotation.NORMAL
        }
    }

    private fun switchFilterTo(filter: GPUImageFilter?) {
        if (gpuImageView.filter == null || gpuImageView.filter!!.javaClass != filter?.javaClass) {
            gpuImageView.filter = filter
            filterAdjuster = FilterAdjuster(filter!!)
            filterAdjuster?.adjust(seekBar.progress)
        }
    }

}