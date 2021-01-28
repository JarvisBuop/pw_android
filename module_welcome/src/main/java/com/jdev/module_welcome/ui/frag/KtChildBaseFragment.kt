package com.jdev.module_welcome.ui.frag

import android.os.Bundle
import android.os.ProxyFileDescriptorCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jdev.kit.helper.HeaderScrollHelper
import com.jdev.module_welcome.R

/**
 * Created by JarvisDong on 2019/07/03.
 * @Description:
 * @see:
 *
 * 基类 fragment;
 */
class KtChildBaseFragment : androidx.fragment.app.Fragment(), HeaderScrollHelper.ScrollableContainer {

    private var position: Int = 0
    var callback: ((position: Int) -> Unit)? = null

    companion object {
        val KEY_TAB_POSITION = "position"
        fun newInstance(position: Int, callback: ((position: Int) -> Unit)?): KtChildBaseFragment {
            var childFragment = KtChildBaseFragment()
            var bundle = Bundle()
            bundle.putInt(KEY_TAB_POSITION, position)
            childFragment.arguments = bundle
            childFragment.callback = callback
            return childFragment
        }
    }

    var mDataList: ArrayList<String> = ArrayList()
    var mChildAdapter: ChildBaseAdapter<String>? = null
    protected var isInitView: Boolean = false
    protected var recyclerview: androidx.recyclerview.widget.RecyclerView? = null

    override fun getScrollableView(): View? {
//        LogUtils.e(TAG, "getScrollableView $recyclerview")
        return recyclerview
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.mw_item_page, container, false)
        bindView(view)
        isInitView = true
        initArgsData()
        callback?.invoke(position)
        return view
    }

    fun bindView(view: View) {
        recyclerview = view.findViewById(R.id.recyclerview)
    }

    fun initRecyclerView() {
        mDataList = ArrayList()
        recyclerview?.layoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(2, androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL)
        mChildAdapter = ChildBaseAdapter(R.layout.mw_item_sample_view, mDataList)
        mChildAdapter?.setOnLoadMoreListener({
            recyclerview?.postDelayed({
                fetchListDataByTabName(false)
            }, 10)
        }, recyclerview)
        recyclerview?.adapter = mChildAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        fetchListDataByTabName(true)
    }

    open fun initArgsData() {
        position = arguments?.getInt(KEY_TAB_POSITION, 0)?:0
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        LogUtils.e(" fragment: " + this::class.java.name + " show: " + isVisibleToUser)
    }

    class ChildBaseAdapter<T>(layoutResId: Int, data: List<T>) : BaseQuickAdapter<T, BaseViewHolder>(layoutResId, data) {

        override fun convert(helper: BaseViewHolder?, item: T) {
            if (helper != null) {
                fillItem(helper, item, helper.layoutPosition)
            }
        }

        fun fillItem(holder: BaseViewHolder, item: T, position: Int) {
            holder.setText(R.id.item_text, item.toString())
        }

    }
    //--------------------------fetch data--------------------------------
    /**
     * 刷新当前list;
     */
    open fun fetchListDataByTabName(refreshFlag: Boolean) {
        for (i in 0..10) {
            mDataList.add("test $i")
        }
        mChildAdapter?.notifyDataSetChanged()
    }

}