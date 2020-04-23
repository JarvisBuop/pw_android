package com.jdev.wandroid.mvvm

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.databinding.BaseObservable
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jarvisdong.kit.baseui.BaseApp
import com.jdev.wandroid.R
import com.jdev.wandroid.databinding.AppFragMvvmTestBinding

/**
 * info: create by jd in 2020/4/1
 * @see:
 * @description: 绑定的vm;
 *
 */
class TestViewModel(val fragment: Fragment) : BaseObservable() {
    var mAdapter: BaseQuickAdapter<String, BaseViewHolder>? = null
    var mDataLists = arrayListOf<String>()
    var mutableLiveData: MutableLiveData<ArrayList<String>> = MutableLiveData()

    var subTitle: ObservableField<String> = ObservableField()

    var imgRes: ObservableField<Drawable> = ObservableField()

    init {
        mAdapter = object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.app_item_centerdrag) {
            override fun convert(helper: BaseViewHolder?, item: String?) {
                helper?.apply {
                    setText(R.id.scroll_tip, "=====> $item")
                }
            }
        }

        mutableLiveData?.observe(fragment, Observer<ArrayList<String>> {
            refreshList(it)
        })
    }


    fun start() {

    }

    fun loadDatas() {
        subTitle.set("hello mvvm")
        imgRes.set(BaseApp.getApp().getDrawable(R.drawable.icon1))

        mutableLiveData.value = arrayListOf("1","2","3")
    }

    fun imageClick() {
        subTitle.set("imageclick ~")
        mutableLiveData.value = arrayListOf("5","6","7","8")
    }

    fun refreshList(it: ArrayList<String>? = null) {
        if (it?.isEmpty() == false) {
            mAdapter?.setNewData(it)
        }
    }
}