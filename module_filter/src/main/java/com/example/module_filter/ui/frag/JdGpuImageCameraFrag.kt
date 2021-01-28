package com.example.module_filter.ui.frag

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.libimagefilter.camera.CameraEngine
import com.example.libimagefilter.filter.helper.MagicFilterType
import com.example.libimagefilter.utils.MagicParams
import com.example.libimagefilter.widget.JdGPUDisplayView
import com.example.module_filter.R
import com.example.module_filter.ui.adapter.FilterAdapter
import com.example.module_filter.utils.GPUImageFilterTools
import com.jdev.kit.baseui.BaseViewStubFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * info: create by jd in 2019/12/12
 * @see:
 * @description: gpuimage camera
 *
 */
class JdGpuImageCameraFrag : BaseViewStubFragment() {
    lateinit var btn_camera_filter:ImageView
    lateinit var btn_camera_closefilter:ImageView
    lateinit var btn_camera_shutter:ImageView
    lateinit var btn_camera_switch:ImageView
    lateinit var btn_camera_mode:ImageView
    lateinit var btn_camera_beauty:ImageView
    lateinit var layout_filter:View
    lateinit var filter_listView: androidx.recyclerview.widget.RecyclerView
    lateinit var displayView: JdGPUDisplayView



    override fun getViewStubId(): Int {
        return R.layout.app_frag_jdgpucamera
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        bindView()
        initView()
    }

    private fun bindView() {
        btn_camera_filter = findView(R.id.btn_camera_filter)
        btn_camera_closefilter = findView(R.id.btn_camera_closefilter)
        btn_camera_shutter = findView(R.id.btn_camera_shutter)
        btn_camera_switch = findView(R.id.btn_camera_switch)
        btn_camera_mode = findView(R.id.btn_camera_mode)
        btn_camera_beauty = findView(R.id.btn_camera_beauty)
        layout_filter = findView(R.id.layout_filter)

        filter_listView = findView(R.id.filter_listView)
        displayView = findView(R.id.displayView)

    }

    private lateinit var mFilterLayout: View
    private lateinit var mFilterListView: androidx.recyclerview.widget.RecyclerView
    private lateinit var mAdapter: FilterAdapter
    private lateinit var btn_shutter: ImageView
    private lateinit var btn_mode: ImageView

    private var isRecording = false
    private val MODE_PIC = 1
    private val MODE_VIDEO = 2
    private var mode = MODE_PIC

    private var animator: ObjectAnimator? = null

    private val types = GPUImageFilterTools.initFilterListObj().toTypedArray()

    private fun initView() {
        mFilterLayout = layout_filter
        mFilterListView = filter_listView

        btn_shutter = btn_camera_shutter
        btn_mode = btn_camera_mode

        btn_camera_filter.setOnClickListener(btn_listener)
        btn_camera_closefilter.setOnClickListener(btn_listener)
        btn_camera_shutter.setOnClickListener(btn_listener)
        btn_camera_switch.setOnClickListener(btn_listener)
        btn_camera_mode.setOnClickListener(btn_listener)
        btn_camera_beauty.setOnClickListener(btn_listener)

        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(mContext)
        linearLayoutManager.orientation = androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
        mFilterListView!!.layoutManager = linearLayoutManager

        mAdapter = FilterAdapter(mContext, types)
        mFilterListView!!.adapter = mAdapter
        mAdapter!!.setOnFilterChangeListener(onFilterChangeListener)


        animator = ObjectAnimator.ofFloat(btn_shutter, "rotation", 0f, 360f)
        animator!!.duration = 500
        animator!!.repeatCount = ValueAnimator.INFINITE

//        val params = displayView.layoutParams as ViewGroup.LayoutParams
//        params.width = ScreenUtils.getScreenWidth()
//        params.height = ScreenUtils.getScreenWidth() * 4 / 3
//        displayView.layoutParams = params
    }

    private val onFilterChangeListener = object : FilterAdapter.onFilterChangeListener {

        override fun onFilterChanged(filterType: MagicFilterType) {
            displayView.setFilter(filterType)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (grantResults.size != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mode == MODE_PIC)
                takePhoto()
            else
                takeVideo()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private val btn_listener = object : View.OnClickListener {

        override fun onClick(v: View) {
            when (v.id) {
                btn_camera_mode.id -> switchMode()
                btn_camera_shutter.id -> if (
                        (PermissionChecker.checkSelfPermission(mContext!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) &&
                        (PermissionChecker.checkSelfPermission(mContext!!, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) &&
                        (PermissionChecker.checkSelfPermission(mContext!!, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)

                ) {
                    ActivityCompat.requestPermissions(mContext as Activity, arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO
                    ), v.id)
                } else {
                    if (mode == MODE_PIC)
                        takePhoto()
                    else
                        takeVideo()
                }
                btn_camera_filter.id -> showFilters()
                btn_camera_switch.id -> {
                    CameraEngine.switchCamera()

                }
                btn_camera_beauty.id -> AlertDialog.Builder(mContext!!)
                        .setSingleChoiceItems(arrayOf("关闭", "1", "2", "3", "4", "5"), MagicParams.beautyLevel
                        ) { dialog, which ->
//                            magicEngine!!.setBeautyLevel(which)
                            MagicParams.beautyLevel = which
//                            displayView.setbea
                            dialog.dismiss()
                        }
                        .setNegativeButton("取消", null)
                        .show()
                btn_camera_closefilter.id -> hideFilters()
            }
        }
    }

    private fun switchMode() {
        if (mode == MODE_PIC) {
            mode = MODE_VIDEO
            btn_mode!!.setImageResource(R.drawable.icon_camera)
        } else {
            mode = MODE_PIC
            btn_mode!!.setImageResource(R.drawable.icon_video)
        }
    }

    private fun takePhoto() {
        val folderName = "GPUImage"
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        displayView.saveToPictures(folderName, fileName) {
            Toast.makeText(activity, "$folderName/$fileName saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun takeVideo() {
        if (isRecording) {
            animator!!.end()
            displayView.changeRecordingState(false)
        } else {
            animator!!.start()
            displayView.changeRecordingState(true)
        }
        isRecording = !isRecording
    }

    private fun showFilters() {
        val animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", (mFilterLayout!!.height).toFloat(), 0f)
        animator.setDuration(200)
        animator.addListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(animation: Animator) {
                btn_camera_shutter.setClickable(false)
                mFilterLayout!!.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {

            }

            override fun onAnimationCancel(animation: Animator) {

            }
        })
        animator.start()
    }

    private fun hideFilters() {
        val animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", 0f, mFilterLayout!!.height.toFloat())
        animator.setDuration(200)
        animator.addListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(animation: Animator) {
                // TODO Auto-generated method stub
            }

            override fun onAnimationRepeat(animation: Animator) {
                // TODO Auto-generated method stub

            }

            override fun onAnimationEnd(animation: Animator) {
                // TODO Auto-generated method stub
                mFilterLayout!!.visibility = View.INVISIBLE
                btn_camera_shutter.setClickable(true)
            }

            override fun onAnimationCancel(animation: Animator) {
                // TODO Auto-generated method stub
                mFilterLayout!!.visibility = View.INVISIBLE
                btn_camera_shutter.setClickable(true)
            }
        })
        animator.start()
    }

    fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MagicCamera")
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(Date())

        return File((mediaStorageDir.path + File.separator +
                "IMG_" + timeStamp + ".jpg"))
    }

}