package com.jdev.wandroid.ui.frg

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.blankj.utilcode.util.ToastUtils
import com.example.libimagefilter.filter.helper.MagicFilterType
import com.example.libimagefilter.utils.MagicParams
import com.example.libimagefilter.widgetimport.magicwidget.MagicCameraView
import com.example.libimagefilter.widgetimport.magicwidget.MagicEngine
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
 * @description: magic camera demo
 *
 * @see https\://github.com/jameswanliu/MagicCamera_master
 *
 */
class GpuMagicCameraFrag : BaseViewStubFragment() {
    lateinit var btn_camera_filter:ImageView
    lateinit var btn_camera_closefilter:ImageView
    lateinit var btn_camera_shutter:ImageView
    lateinit var btn_camera_switch:ImageView
    lateinit var btn_camera_mode:ImageView
    lateinit var btn_camera_beauty:ImageView
    lateinit var layout_filter:View
    lateinit var filter_listView:RecyclerView
    lateinit var glsurfaceview_camera: MagicCameraView

    override fun getViewStubId(): Int {
        return R.layout.app_frag_magiccamera
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        bindView()
        magicEngine = MagicEngine.Builder().build(glsurfaceview_camera)
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
        glsurfaceview_camera = findView(R.id.glsurfaceview_camera)
    }

    private lateinit var mFilterLayout: View
    private lateinit var mFilterListView: RecyclerView
    private lateinit var mAdapter: FilterAdapter
    private lateinit var magicEngine: MagicEngine
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

        val linearLayoutManager = LinearLayoutManager(mContext)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mFilterListView!!.layoutManager = linearLayoutManager

        mAdapter = FilterAdapter(mContext, types)
        mFilterListView!!.adapter = mAdapter
        mAdapter!!.setOnFilterChangeListener(onFilterChangeListener)


        animator = ObjectAnimator.ofFloat(btn_shutter, "rotation", 0f, 360f)
        animator!!.duration = 500
        animator!!.repeatCount = ValueAnimator.INFINITE

//        val params = glsurfaceview_camera.layoutParams as RelativeLayout.LayoutParams
//        params.width = ScreenUtils.getScreenWidth()
//        params.height = ScreenUtils.getScreenWidth() * 4 / 3
//        glsurfaceview_camera.layoutParams = params
    }

    private val onFilterChangeListener = object : FilterAdapter.onFilterChangeListener {

        override fun onFilterChanged(filterType: MagicFilterType) {
            magicEngine!!.setFilter(filterType)
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
                btn_camera_shutter.id -> if ((PermissionChecker.checkSelfPermission(mContext!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)) {
                    ActivityCompat.requestPermissions(mContext as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), v.id)
                } else {
                    if (mode == MODE_PIC)
                        takePhoto()
                    else
                        takeVideo()
                }
                btn_camera_filter.id -> showFilters()
                btn_camera_switch.id -> magicEngine!!.switchCamera()
                btn_camera_beauty.id -> AlertDialog.Builder(mContext!!)
                        .setSingleChoiceItems(arrayOf("关闭", "1", "2", "3", "4", "5"), MagicParams.beautyLevel
                        ) { dialog, which ->
                            magicEngine!!.setBeautyLevel(which)
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
        ToastUtils.showShort(if (mode == MODE_PIC) "拍摄" else "录制")
    }

    private fun takePhoto() {
        ToastUtils.showShort("开始拍照")
        magicEngine!!.savePicture(getOutputMediaFile(), null)
    }

    private fun takeVideo() {
        ToastUtils.showShort("开始录制")
        if (isRecording) {
            animator!!.end()
            magicEngine!!.stopRecord()
        } else {
            animator!!.start()
            magicEngine!!.startRecord()
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