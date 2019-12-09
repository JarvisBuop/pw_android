package com.jdev.wandroid.ui.act

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jarvisdong.kit.utils.ResourceIdUtils
import com.jarvisdong.kotlindemo.ui.BaseActivity
import com.jdev.wandroid.R
import kotlinx.android.synthetic.main.app_activity_main.*


/**
 * home page
 */
class MainActivity : BaseActivity() {
    //top recyclerview datas
    var mTopDatas = arrayListOf<OrientVo>(
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc")
    )

    //bottom recyclerview datas
    var mBottomDatas = arrayListOf<OrientVo>(
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc")
    )

    lateinit var mAdapterTop: MyAdapter<OrientVo>
    lateinit var mAdapterBottom: MyAdapter<OrientVo>

    override fun getViewStubId(): Int {
        return R.layout.app_activity_main
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        initRecyclerViews()
        fetchDatas()
    }

    private fun initRecyclerViews() {
        main_container.setDisableDoubleScroll(false)

        mAdapterTop = MyAdapter(R.layout.app_test_normalitem)
        mAdapterBottom = MyAdapter(R.layout.app_test_normalitem_reverse)
        mAdapterTop.setOnItemClickListener { adapter, view, position ->
            clickItem(mAdapterTop.getItem(position), position)
        }

        mAdapterBottom.setOnItemClickListener { adapter, view, position ->
            clickItem(mAdapterBottom.getItem(position), position)
        }

        first_recyclerview.layoutManager = LinearLayoutManager(mContext)
        first_recyclerview.adapter = mAdapterTop
        second_recyclerview.layoutManager = LinearLayoutManager(mContext)
        second_recyclerview.adapter = mAdapterBottom
    }

    private fun clickItem(item: OrientVo?, position: Int) {
        item?.also {
            if (it.clazz != null) {
                mContext.startActivity(Intent(mContext, it.clazz))
            }
        }
    }

    private fun fetchDatas() {
        mAdapterTop?.also {
            it.setNewData(mTopDatas)
        }

        mAdapterBottom.also {
            it.setNewData(mBottomDatas)
        }
    }

    class MyAdapter<T>(layoutId: Int, mDataList: List<T>? = null) : BaseQuickAdapter<T, BaseViewHolder>(layoutId, mDataList) {
        override fun convert(helper: BaseViewHolder?, item: T?) {
            if (item is OrientVo) {
                helper?.apply {
                    setText(R.id.txt_title, item.title)
                    setText(R.id.txt_desc, item.desc)
                    setText(R.id.txt_content, item.level.toString())
                    when (item.level) {
                        LEVEL.LEVEL_CRITICAL -> {
                            setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.red))
                        }
                        LEVEL.LEVEL_HIGH -> {
                            setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.color_violet))
                        }
                        LEVEL.LEVEL_MIDDLE -> {
                            setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.color_orange))
                        }
                        LEVEL.LEVEL_LOW -> {
                            setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.color_blue))
                        }
                        LEVEL.LEVEL_NOPE -> {
                            setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.color_green))
                        }
                        else -> {
                            setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.text_second_color))
                        }
                    }
                }
            }
        }
    }

    data class OrientVo(
            var title: String,
            var desc: String,
            var level: LEVEL = LEVEL.LEVEL_NOPE,
            var clazz: Class<*>? = null
    )

    enum class LEVEL {
        LEVEL_CRITICAL,
        LEVEL_HIGH,
        LEVEL_MIDDLE,
        LEVEL_LOW,
        LEVEL_NOPE
    }


}
