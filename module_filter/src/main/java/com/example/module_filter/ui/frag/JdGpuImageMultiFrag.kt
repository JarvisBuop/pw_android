package com.example.module_filter.ui.frag

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.example.libimagefilter.filter.base.gpuimage.GPUImageFilter
import com.example.libimagefilter.filter.helper.FilterAdjuster
import com.example.libimagefilter.filter.helper.MagicFilterFactory
import com.example.libimagefilter.filter.helper.MagicFilterType
import com.example.libimagefilter.widget.JdGPUDisplayView
import com.example.module_filter.R
import com.example.module_filter.utils.GPUImageFilterTools
import com.example.module_filter.utils.KtHandlerSingleton
import com.jdev.kit.baseui.BaseViewStubFragment

/**
 * info: create by jd in 2019/12/20
 * @see:
 * @description: 效果图;
 *
 */
class JdGpuImageMultiFrag : BaseViewStubFragment() {
    private lateinit var mRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var mSeekBar:SeekBar
    private lateinit var mViewPager: androidx.viewpager.widget.ViewPager
    private lateinit var mTxtCurrentTab:TextView
    private lateinit var mTxtCenterName:TextView
    private lateinit var mTxtBottomPercent:TextView
    private lateinit var mGroupLabel:View

    private var viewpagePosition: Int = 0
    private var mPagerAdapter: MyPagerAdapter? = null
    val allFilters = arrayListOf<MagicFilterType>(
            MagicFilterType.NONE,
            //origin
            MagicFilterType.CONTRAST,
            MagicFilterType.BRIGHTNESS,
            MagicFilterType.EXPOSURE,
            MagicFilterType.HUE,
            MagicFilterType.SATURATION,
            MagicFilterType.SHARPEN,
            MagicFilterType.IMAGE_ADJUST
    )

    //图片相关数据
    var arrayFilters = arrayListOf<FilterData>(
            FilterData(R.drawable.gpuimage_origin),
            FilterData(R.drawable.gpuimage_icon_beauty),
            FilterData(R.drawable.gpuiamge_icon_dragon),
            FilterData(R.drawable.gpuimage_origin),
            FilterData(R.drawable.gpuimage_icon_beauty),
            FilterData(R.drawable.gpuiamge_icon_dragon)
    )

    data class FilterData(
            var res: Int = R.drawable.gpuimage_origin,
            var nameFilter: String? = "原图",
            var filterType: MagicFilterType = MagicFilterType.NONE,
            var filterAdjuster: FilterAdjuster? = null,
            var seekBar: Int = 0
    )

    inner class MyPagerAdapter : androidx.viewpager.widget.PagerAdapter() {
        var map = SparseArray<View>()

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return `object` == view
        }

        override fun getCount(): Int {
            return arrayFilters.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var view = map.get(position)
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_gpufilter_picture_main, container, false)
                map.put(position, view)
            }

            var gpuImageView = view.findViewById<JdGPUDisplayView>(R.id.gpuImageView)
            var i = arrayFilters[position]
            gpuImageView.setImage(BitmapFactory.decodeResource(mContext!!.resources, i.res))
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)
        }
    }


    override fun getViewStubId(): Int {
        return R.layout.frag_gpufilter
    }

    companion object {
        //存储权限;
        private const val REQUEST_STORAGE_PERMISSION = 2
    }


    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        if (!hasStoragePermission()) {
            ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION
            )
            return
        }
        initView()
    }

    fun initView() {
        initViewPager()
        initSeekBar()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = findView<androidx.recyclerview.widget.RecyclerView>(R.id.mRecyclerView)
        mRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(mContext, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView.addItemDecoration(object : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                var viewLayoutPosition = (view?.layoutParams as androidx.recyclerview.widget.RecyclerView.LayoutParams).viewLayoutPosition
                if (viewLayoutPosition == (parent?.adapter?.itemCount ?: 0) - 1) {
                    outRect?.set(ConvertUtils.dp2px(15f), 0, ConvertUtils.dp2px(15f), 0)
                } else {
                    outRect?.set(ConvertUtils.dp2px(15f), 0, 0, 0)
                }
            }
        })
        mRecyclerView.adapter = object : BaseQuickAdapter<MagicFilterType, BaseViewHolder>(R.layout.item_gpufilter_controll, allFilters) {
            override fun convert(helper: BaseViewHolder?, item: MagicFilterType) {
                helper?.apply {
                    setText(R.id.filter_txt, if (!StringUtils.isEmpty(GPUImageFilterTools.FilterType2Name(item)))
                        GPUImageFilterTools.FilterType2Name(item)
                    else "原图")

                    var indexOf = allFilters.indexOf(arrayFilters[viewpagePosition].filterType)
                    if (helper.adapterPosition == indexOf) {
                        helper.getView<View>(R.id.filter_selected).visibility = View.VISIBLE
                    } else {
                        helper.getView<View>(R.id.filter_selected).visibility = View.GONE
                    }

                    var view = getView<View>(R.id.filter_pic)

                    view.setOnClickListener {
                        var filterData = arrayFilters.get(viewpagePosition)
                        filterData.filterType = item
                        filterData.nameFilter = GPUImageFilterTools.FilterType2Name(item)
                        notifyDataSetChanged()

                        showMarkByType(name = filterData.nameFilter, type = 0)
                        switchFilterForCurrentImage()
                    }
                }
            }
        }
    }

    private fun initSeekBar() {
        mSeekBar = findView(R.id.mSeekBar)
        mSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    var filterData = arrayFilters[viewpagePosition]
                    filterData.seekBar = progress
                    showMarkByType(percent = progress, type = 1)

                    //更新gpuimage;
                    filterData.filterAdjuster?.adjust(progress)
                    getPrimaryView()?.requestRender()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun initViewPager() {
        mViewPager = findView<androidx.viewpager.widget.ViewPager>(R.id.mViewPager)
        mTxtCurrentTab = findView<TextView>(R.id.mTxtCurrentTab)
        mTxtCenterName = findView<TextView>(R.id.mTxtCenterName)
        mTxtBottomPercent = findView<TextView>(R.id.mTxtBottomPercent)
        mGroupLabel = findView<View>(R.id.mGroupLabel)
        mViewPager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                //上一个还原scale
                var primaryView = getPrimaryView()
                primaryView?.setUpOrigin()

                viewpagePosition = position
                mTxtCurrentTab.text = "${viewpagePosition + 1}/${arrayFilters.size}"
                //滑动底部滤镜至选择  && seekbar 设置;
                var filterData = arrayFilters.get(viewpagePosition)
                var indexOf = allFilters.indexOf(filterData.filterType)

                if (indexOf != -1) {
                    mRecyclerView.adapter?.notifyDataSetChanged()
                    mRecyclerView.smoothScrollToPosition(indexOf)
                    //fromUser attention;
                    mSeekBar.progress = filterData.seekBar

                }

                switchFilterForCurrentImage()
            }

        })

        mPagerAdapter = MyPagerAdapter()
        mViewPager.adapter = mPagerAdapter
        mViewPager.offscreenPageLimit = arrayFilters.size
    }

    private fun switchFilterForCurrentImage() {
        var filterData = arrayFilters.get(viewpagePosition)

        var gpuImageView = getPrimaryView()

        var filter: GPUImageFilter? = MagicFilterFactory.getFilterByType(filterData.filterType)

        if (filter != null && (gpuImageView?.filter == null || gpuImageView.filter.javaClass != filter.javaClass)) {
            gpuImageView?.filter = filter
            filterData.filterAdjuster = FilterAdjuster(filter)
            if (filterData.filterAdjuster?.canAdjust() == true) {
                mSeekBar.visibility = View.VISIBLE
                filterData.filterAdjuster?.adjust(filterData.seekBar)
            } else {
                mSeekBar.visibility = View.INVISIBLE
            }
        } else if (filterData.filterAdjuster != null) {
            if (filterData.filterAdjuster?.canAdjust() == true) {
                mSeekBar.visibility = View.VISIBLE
                filterData.filterAdjuster?.adjust(filterData.seekBar)
            } else {
                mSeekBar.visibility = View.INVISIBLE
            }
        }
        gpuImageView?.requestRender()
    }

    private fun getPrimaryView(): JdGPUDisplayView? {
//        return mViewPager.getChildAt(viewpagePosition).findViewById<JdGPUDisplayView>(R.id.gpuImageView)
        return mPagerAdapter?.map?.get(viewpagePosition)?.findViewById<JdGPUDisplayView>(R.id.gpuImageView)
    }

    private fun showMarkByType(name: String? = null, percent: Int = -1, type: Int = 0) {
        when (type) {
            0 -> {
                if (!StringUtils.isSpace(name)) {
                    mTxtCenterName.text = name
                }

                mTxtCenterName.visibility = View.VISIBLE
                KtHandlerSingleton.getInstance().setCallBackByHandler(1000, activity!!) {
                    mTxtCenterName.visibility = View.GONE
                }
            }
            1 -> {
                if (percent != -1) {
                    mTxtBottomPercent.text = "$percent%"
                } else {
                    mTxtBottomPercent.text = ""
                }

                mGroupLabel.visibility = View.VISIBLE
                KtHandlerSingleton.getInstance().setCallBackByHandler(1000, activity!!) {
                    mGroupLabel.visibility = View.GONE
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_STORAGE_PERMISSION && grantResults.size == 2
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
        ) {
            initView()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

}