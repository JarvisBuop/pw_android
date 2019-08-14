package com.jdev.wandroid.ui.act

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jarvisdong.kotlindemo.ui.BaseActivity
import com.jdev.wandroid.R
import kotlinx.android.synthetic.main.act_feed_container.*
import kotlinx.android.synthetic.main.activity_feed_detail_include_centerdrag.view.*
import java.util.*

/**
 * Created by JarvisDong on 2019/4/14.
 * @Description: 测试scrollercontainer 的recyclerview和nestscrollerview
 * @see:
 */
class FeedTestAct : BaseActivity() {
    lateinit var mAdapter: MyAdapter
    lateinit var mData: ArrayList<Any>

    lateinit var mAdapter2: MyAdapter
    lateinit var mData2: ArrayList<Any>

    override fun getViewStubId(): Int {
        return R.layout.act_feed_container
    }

    override fun initIntentData(): Boolean {
        return true
    }

    override fun customOperate(savedInstanceState: Bundle?) {
        initRecyclerList()
        initSecondRecyclerList()

        container.setDisableDoubleScroll(false)
        initData()
    }

    private fun initSecondRecyclerList() {
        if(second_list_view==null) return
        mData2 = ArrayList()
        mAdapter2 = MyAdapter(mContext, mData2)
        mAdapter2.b = true
        second_list_view.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        second_list_view.adapter = mAdapter2
    }

    private fun initData() {
        for (i in 0..30) {
            mData.add("test: "+i)
            mData2.add("test: "+i)
        }

        mAdapter.notifyDataSetChanged()
        mAdapter2.notifyDataSetChanged()
    }

    private fun initRecyclerList() {
        mData = ArrayList()
        mAdapter = MyAdapter(mContext, mData)
        first_scroll_view.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        first_scroll_view.adapter = mAdapter
    }


    class MyAdapter(var mContext: Context?, var mData: ArrayList<Any>) : RecyclerView.Adapter<MyHolder>() {
        var b:Boolean = false
        fun setBg(){
            b = true
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            var view = LayoutInflater.from(mContext).inflate(R.layout.activity_feed_detail_include_centerdrag, parent, false)
            return MyHolder(view)
        }

        override fun getItemCount(): Int {
            return mData.size
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            if(b){
                holder.itemView.setBackgroundColor(Color.GREEN)
            }else {
                holder.itemView.setBackgroundColor(Color.CYAN)
            }
            holder.itemView.scroll_tip.setText(mData.get(position).toString())
        }

    }

    class MyHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

}



