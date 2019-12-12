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
import android.widget.RelativeLayout
import com.blankj.utilcode.util.ScreenUtils
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import com.jdev.wandroid.ui.adapter.FilterAdapter
import com.seu.magicfilter.MagicEngine
import com.seu.magicfilter.filter.helper.MagicFilterType
import com.seu.magicfilter.utils.MagicParams
import kotlinx.android.synthetic.main.app_filter_layout.*
import kotlinx.android.synthetic.main.app_frag_magiccamera.*
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
    override fun getViewStubId(): Int {
        return R.layout.app_frag_magiccamera
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        magicEngine = MagicEngine.Builder().build(glsurfaceview_camera)
        initView()

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

    private val types = arrayOf(
            MagicFilterType.NONE,
            MagicFilterType.FAIRYTALE,
            MagicFilterType.SUNRISE,
            MagicFilterType.SUNSET,
            MagicFilterType.WHITECAT,
            MagicFilterType.BLACKCAT,
            MagicFilterType.SKINWHITEN,
            MagicFilterType.HEALTHY,
            MagicFilterType.SWEETS,
            MagicFilterType.ROMANCE,
            MagicFilterType.SAKURA,
            MagicFilterType.WARM,
            MagicFilterType.ANTIQUE,
            MagicFilterType.NOSTALGIA,
            MagicFilterType.CALM,
            MagicFilterType.LATTE,
            MagicFilterType.TENDER,
            MagicFilterType.COOL,
            MagicFilterType.EMERALD,
            MagicFilterType.EVERGREEN,
            MagicFilterType.CRAYON,
            MagicFilterType.SKETCH,
            MagicFilterType.AMARO,
            MagicFilterType.BRANNAN,
            MagicFilterType.BROOKLYN,
            MagicFilterType.EARLYBIRD,
            MagicFilterType.FREUD,
            MagicFilterType.HEFE,
            MagicFilterType.HUDSON,
            MagicFilterType.INKWELL,
            MagicFilterType.KEVIN,
            MagicFilterType.LOMO,
            MagicFilterType.N1977,
            MagicFilterType.NASHVILLE,
            MagicFilterType.PIXAR,
            MagicFilterType.RISE,
            MagicFilterType.SIERRA,
            MagicFilterType.SUTRO,
            MagicFilterType.TOASTER2,
            MagicFilterType.VALENCIA,
            MagicFilterType.WALDEN,
            MagicFilterType.XPROII,

            MagicFilterType.CONTRAST,
            MagicFilterType.BRIGHTNESS,
            MagicFilterType.EXPOSURE,
            MagicFilterType.HUE,
            MagicFilterType.SATURATION,
            MagicFilterType.SHARPEN,
            MagicFilterType.IMAGE_ADJUST,
            MagicFilterType.GRAYSCALE,
            MagicFilterType.SEPIA,
            MagicFilterType.SOBEL_EDGE_DETECTION,
            MagicFilterType.THRESHOLD_EDGE_DETECTION,
            MagicFilterType.THREE_X_THREE_CONVOLUTION,
            MagicFilterType.FILTER_GROUP,
            MagicFilterType.EMBOSS,
            MagicFilterType.POSTERIZE,
            MagicFilterType.GAMMA,
            MagicFilterType.INVERT,
            MagicFilterType.PIXELATION,
            MagicFilterType.HIGHLIGHT_SHADOW,
            MagicFilterType.MONOCHROME,
            MagicFilterType.OPACITY,
            MagicFilterType.RGB,
            MagicFilterType.WHITE_BALANCE,
            MagicFilterType.VIGNETTE,
            MagicFilterType.TONE_CURVE,
            MagicFilterType.LUMINANCE,
            MagicFilterType.LUMINANCE_THRESHSOLD,
            MagicFilterType.BLEND_COLOR_BURN,
            MagicFilterType.BLEND_COLOR_DODGE,
            MagicFilterType.BLEND_DARKEN,
            MagicFilterType.BLEND_DIFFERENCE,
            MagicFilterType.BLEND_DISSOLVE,
            MagicFilterType.BLEND_EXCLUSION,
            MagicFilterType.BLEND_SOURCE_OVER,
            MagicFilterType.BLEND_HARD_LIGHT,
            MagicFilterType.BLEND_LIGHTEN,
            MagicFilterType.BLEND_ADD,
            MagicFilterType.BLEND_DIVIDE,
            MagicFilterType.BLEND_MULTIPLY,
            MagicFilterType.BLEND_OVERLAY,
            MagicFilterType.BLEND_SCREEN,
            MagicFilterType.BLEND_ALPHA,
            MagicFilterType.BLEND_COLOR,
            MagicFilterType.BLEND_HUE,
            MagicFilterType.BLEND_SATURATION,
            MagicFilterType.BLEND_LUMINOSITY,
            MagicFilterType.BLEND_LINEAR_BURN,
            MagicFilterType.BLEND_SOFT_LIGHT,
            MagicFilterType.BLEND_SUBTRACT,
            MagicFilterType.BLEND_CHROMA_KEY,
            MagicFilterType.BLEND_NORMAL,
            MagicFilterType.LOOKUP_AMATORKA,
            MagicFilterType.GAUSSIAN_BLUR,
            MagicFilterType.CROSSHATCH,
            MagicFilterType.BOX_BLUR,
            MagicFilterType.CGA_COLORSPACE,
            MagicFilterType.DILATION,
            MagicFilterType.KUWAHARA,
            MagicFilterType.RGB_DILATION,
            MagicFilterType.TOON,
            MagicFilterType.SMOOTH_TOON,
            MagicFilterType.BULGE_DISTORTION,
            MagicFilterType.GLASS_SPHERE,
            MagicFilterType.HAZE,
            MagicFilterType.LAPLACIAN,
            MagicFilterType.NON_MAXIMUM_SUPPRESSION,
            MagicFilterType.SPHERE_REFRACTION,
            MagicFilterType.SWIRL,
            MagicFilterType.WEAK_PIXEL_INCLUSION,
            MagicFilterType.FALSE_COLOR,
            MagicFilterType.COLOR_BALANCE,
            MagicFilterType.LEVELS_FILTER_MIN,
            MagicFilterType.BILATERAL_BLUR,
            MagicFilterType.ZOOM_BLUR,
            MagicFilterType.HALFTONE,
            MagicFilterType.TRANSFORM2D,
            MagicFilterType.SOLARIZE,
            MagicFilterType.VIBRANCE,
            MagicFilterType.CUSTOM_丑颜,
            MagicFilterType.CUSTOM_美颜
    )

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

        val params = glsurfaceview_camera.layoutParams as RelativeLayout.LayoutParams
        params.width = ScreenUtils.getScreenWidth()
        params.height = ScreenUtils.getScreenWidth() * 4 / 3
        glsurfaceview_camera.layoutParams = params
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
    }

    private fun takePhoto() {
        magicEngine!!.savePicture(getOutputMediaFile(), null)
    }

    private fun takeVideo() {
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