package com.jdev.wandroid.ui.frg

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.widget.ImageView
import android.widget.SeekBar
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
 */
class GpuImageTestFrag : BaseViewStubFragment() {

    var adjusterMaps = mutableMapOf<FilterType, GPUImageFilterTools.FilterAdjuster>()


    private lateinit var myAdapter: MyAdapter<FilterVo>
    private var filterAdjuster: GPUImageFilterTools.FilterAdjuster? = null
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
                for ((key, value) in adjusterMaps.entries) {
                    if (value.canAdjust()) {
                        value.adjust(progress)
                        //更新recycelrview中
                        myAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun fetchDatas() {
        var filterObj = GPUImageFilterTools.initFilterListObj()
        var arr = arrayListOf<FilterVo>()
        arr.add(FilterVo())
        for ((index, value) in filterObj.filters.withIndex()) {
            arr.add(FilterVo(value, filterObj.names.get(index), GPUImageFilterTools.createFilterForType(mContext!!, value)))
        }
        myAdapter.setNewData(arr)
    }

    private fun initRecyclerView() {
        recyclerview.layoutManager = GridLayoutManager(mContext, 2)
        myAdapter = MyAdapter<FilterVo>(R.layout.app_item_gpuimage)
        recyclerview.adapter = myAdapter
    }


    inner class MyAdapter<T>(layoutId: Int, mDataList: List<T>? = null) : BaseQuickAdapter<T, BaseViewHolder>(layoutId, mDataList) {

        override fun convert(helper: BaseViewHolder, item: T?) {
            var gpuImageView = helper.getView<GPUImageView>(R.id.image_style)
            if (item is FilterVo) {
                helper.setText(R.id.txt_style_name, item.filterName ?: "origin")
                if (helper.adapterPosition == 0) {
                    gpuImageView.setImage(BitmapFactory.decodeResource(mContext.resources, R.drawable.gpuimage_origin))
                } else {
                    //滤镜;
                    var createFilter = item.filter
                    var filterAdjuster = adjusterMaps.get(item.filterType)
                    if (filterAdjuster == null) {
                        filterAdjuster = GPUImageFilterTools.FilterAdjuster(createFilter)
                        adjusterMaps.put(item.filterType, filterAdjuster)
                    }

                    gpuImageView.filter = createFilter
                    gpuImageView.requestRender()
                }
            }
        }
    }

    data class FilterVo(
            var filterType: FilterType = FilterType.CONTRAST,
            var filterName: String = "",
            var filter: GPUImageFilter = GPUImageContrastFilter(2.0f)
    )

}