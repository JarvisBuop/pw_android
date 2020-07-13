package com.jdev.kit.baseui

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewStub
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jdev.kit.R
import kotlinx.android.synthetic.main.component_layout_recyclerview.*
import org.reactivestreams.Subscription

/**
 * info: create by jd in 2019/11/1
 * @see:
 * @description: only recyclerview fragment;
 *
 */
abstract class BaseRecyclerViewFragment<T> : BaseViewStubFragment(), SwipeRefreshLayout.OnRefreshListener {
    protected var mAdapter: BaseQuickAdapter<T, BaseViewHolder>? = null
    protected var mDataList: ArrayList<T>? = null

    protected var page: Int = 0
    protected var mEmptyView: View? = null

    override fun getViewStubId(): Int {
        return R.layout.component_layout_recyclerview
    }

    override fun initIntentData(): Boolean {
        return true
    }

    override fun customOperate(savedInstanceState: Bundle?) {
        initSwipeView()
        initRecyclerView()
        initEmptyView()
        resetInitParams()
        if (needFirstLoad()) {
            loadDatas()
        }
    }

    open fun initEmptyView() {
        if (getEmptyLayout() != 0) {
            var mEmptyViewStub = findView<ViewStub>(R.id.mEmptyViewStub)
            mEmptyViewStub.layoutResource = getEmptyLayout()
            mEmptyView = mEmptyViewStub.inflate()
            mEmptyView?.visibility = View.GONE
        }
    }

    open fun initRecyclerView() {
        findView<RecyclerView>(R.id.recyclerView)?.layoutManager = getLayoutManager()
        if (getItemDecoration() != null) {
            findView<RecyclerView>(R.id.recyclerView)?.addItemDecoration(getItemDecoration()!!)
        }
        try {
            mDataList = ArrayList()
            mAdapter = initialAdapter()
            initExtraAdapter()
        } catch (e: Exception) {
            LogUtils.e("${javaClass.name} adapter initial encounter a exception ")
        }
        findView<RecyclerView>(R.id.recyclerView)?.adapter = mAdapter
    }

    private fun initSwipeView() {
        if (isSupportRefresh()) {
            findView<SwipeRefreshLayout>(R.id.swipeRefreshLayout)?.isEnabled = true
            findView<SwipeRefreshLayout>(R.id.swipeRefreshLayout)?.setColorSchemeResources(R.color.red, R.color.green, R.color.blue)
            findView<SwipeRefreshLayout>(R.id.swipeRefreshLayout)?.setOnRefreshListener(this)
        } else {
            findView<SwipeRefreshLayout>(R.id.swipeRefreshLayout)?.isEnabled = false
        }
    }

    override fun onRefresh() {
        findView<SwipeRefreshLayout>(R.id.swipeRefreshLayout)?.isRefreshing = false
        resetInitParams()
        loadDatas()
    }

    fun loadDatas() {
//        releaseSubscription()
//        registerSubscription(fetchDatas())
        fetchDatas()
    }


    open inner class MyAdapter(layoutResId: Int, data: List<T>?) : BaseQuickAdapter<T, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: T) {
            convertSingleItemView(helper, item)
        }
    }

    //----------------------------------------------------------
    //---------------------open method------------------------------
    //----------------------------------------------------------

    open fun getEmptyLayout(): Int {
        return 0
    }

    open fun needFirstLoad(): Boolean {
        return true
    }

    open fun resetInitParams() {
        page = 0
    }

    open fun isNeedClear(): Boolean {
        return page == 0
    }

    open fun isSupportRefresh(): Boolean {
        return true
    }

    open fun initialAdapter(): BaseQuickAdapter<T, BaseViewHolder>? {
        if (getItemLayoutId() != 0) {
            return MyAdapter(getItemLayoutId(), mDataList)
        } else {
            return null
        }
    }

    open fun initExtraAdapter() {

    }


    open fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(mContext)
    }

    open fun getItemDecoration(): RecyclerView.ItemDecoration? = null


    abstract fun fetchDatas(): Subscription?

    /**
     * single itemtype layoutId
     */
    open fun getItemLayoutId(): Int {
        return 0
    }

    /**
     * single itemtype
     */
    open fun convertSingleItemView(helper: BaseViewHolder, item: T) {

    }
}