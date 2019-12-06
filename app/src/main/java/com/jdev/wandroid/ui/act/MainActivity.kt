package com.jdev.wandroid.ui.act

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jarvisdong.kotlindemo.ui.BaseActivity
import com.jdev.wandroid.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


/**
 * home page
 */
class MainActivity : BaseActivity() {


    lateinit var mAdapterTop: MyAdapter
    lateinit var mDataTop: ArrayList<OrientVo>

    lateinit var mAdapterBottom: MyAdapter
    lateinit var mDataBottom: ArrayList<OrientVo>

    override fun getViewStubId(): Int {
        return R.layout.activity_main
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
        initRecyclerViews()
        fetchDatas()
    }

    private fun initRecyclerViews() {
        main_container.setDisableDoubleScroll(false)

        mDataTop = ArrayList()
        mDataBottom = ArrayList()
        mAdapterTop = MyAdapter(R.layout.test_centerdrag, mDataTop)
        mAdapterBottom = MyAdapter(R.layout.test_centerdrag, mDataBottom)

        first_recyclerview.layoutManager = LinearLayoutManager(mContext)
        first_recyclerview.adapter = mAdapterTop
        second_recyclerview.layoutManager = LinearLayoutManager(mContext)
        second_recyclerview.adapter = mAdapterBottom
    }

    private fun fetchDatas() {
        mDataTop.also {

        }

        mDataBottom.also {

        }
    }

    class MyAdapter(layoutId: Int, mDataList: List<OrientVo>) : BaseQuickAdapter<OrientVo, BaseViewHolder>(layoutId, mDataList) {
        override fun convert(helper: BaseViewHolder?, item: OrientVo?) {

        }
    }

    data class OrientVo(
            var title: String? = "",
            var desc: String? = "",
            var level: LEVEL = LEVEL.LEVEL_NOPE,
            var clazz: Class<*>? = null
    )

    enum class LEVEL {
        LEVEL_CRITICAL, LEVEL_HIGH, LEVEL_MIDDLE, LEVEL_LOW, LEVEL_NOPE
    }


}
