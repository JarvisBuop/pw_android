package com.jdev.wandroid.ui.frg

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import com.jdev.wandroid.mockdata.FilterType
import com.jdev.wandroid.utils.GPUImageFilterTools
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import kotlinx.android.synthetic.main.app_frag_gpuimage.*

/**
 * info: create by jd in 2019/12/10
 * @see:
 * @description: gpuimage test
 *
 * https://github.com/cats-oss/android-gpuimage
 *
 * Android filters based on OpenGL (idea from GPUImage for iOS)
 *
 * tips: 列表中不显示, 可能多次设置导致运算慢出现问题;
 */
class GpuImageTestFrag : BaseViewStubFragment() {
    private lateinit var myAdapter: MyAdapter<FilterVo>
    private var progress:Int = 0
    override fun getViewStubId(): Int {
        return R.layout.app_frag_gpuimage
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {

        initRecyclerView()
        initFilterDatas()
        fetchDatas()
    }

    private fun initFilterDatas() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                this@GpuImageTestFrag.progress = progress
                changeFilter(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun changeFilter(progress: Int) {
        var data = myAdapter.data
        for (value in data) {
            if (value.adjuster.canAdjust()) {
                value.adjuster.adjust(progress)
                //更新recycelrview中
                myAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun fetchDatas() {
        var filterObj = GPUImageFilterTools.initFilterListObj()
        var arr = arrayListOf<FilterVo>()
        arr.add(FilterVo())
        for ((index, value) in filterObj.filters.withIndex()) {
            var filter = GPUImageFilterTools.createFilterForType(mContext!!, value)
            arr.add(FilterVo(value, filterObj.names.get(index),
                    filter,
                    GPUImageFilterTools.FilterAdjuster(filter)
            ))
        }
        changeFilter(50)
        myAdapter.setNewData(arr)
    }

    private fun initRecyclerView() {
        recyclerview.layoutManager = GridLayoutManager(mContext, 2)
        myAdapter = MyAdapter<FilterVo>(R.layout.app_item_gpuimage)
        recyclerview.adapter = myAdapter
    }


    inner class MyAdapter<T>(layoutId: Int, mDataList: List<T>? = null) : BaseQuickAdapter<T, BaseViewHolder>(layoutId, mDataList) {

        override fun convert(helper: BaseViewHolder, item: T?) {
            LogUtils.e("convert :: ${helper.adapterPosition}")
            var gpuImageView = helper.getView<GPUImageView>(R.id.gpuImageView)
            var imageView = helper.getView<ImageView>(R.id.image_origin)
            if (item is FilterVo) {
                helper.setText(R.id.txt_style_name, item.filterName ?: "origin")
                if (helper.adapterPosition == 0) {
                    imageView.visibility = View.VISIBLE
                    gpuImageView.visibility = View.GONE
                    imageView.setImageResource(R.drawable.gpuimage_origin)
                } else {
                    imageView.visibility = View.GONE
                    gpuImageView.visibility = View.VISIBLE
                    gpuImageView.setImage(BitmapFactory.decodeResource(mContext.resources,R.drawable.gpuimage_origin))
                    //滤镜;
                    if (gpuImageView.filter == null || gpuImageView.filter.javaClass != item.filter.javaClass) {
                        gpuImageView.filter = item.filter
                        gpuImageView.requestRender()
                    }

                }
            }
        }
    }

    data class FilterVo(
            var filterType: FilterType = FilterType.CONTRAST,
            var filterName: String? = null,
            var filter: GPUImageFilter = GPUImageContrastFilter(2.0f),
            var adjuster: GPUImageFilterTools.FilterAdjuster = GPUImageFilterTools.FilterAdjuster(filter)
    )

}