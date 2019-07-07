package com.jdev.module_welcome.ui.frag

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.ConvertUtils
import com.jdev.module_welcome.R
import kotlinx.android.synthetic.main.item_page.*
import kotlinx.android.synthetic.main.item_sample_view.view.*

/**
 * Created by JarvisDong on 2019/07/03.
 * @Description:
 * @see:
 */
class KtChildBaseFragment : Fragment() {
    var mData = arrayListOf<Int>(
            R.drawable.icon1, R.drawable.icon2,
            R.drawable.icon3, R.drawable.icon4,
            R.drawable.icon1, R.drawable.icon2)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.item_page, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerview.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerview.adapter = MyAdapter()
    }

    inner class MyAdapter : RecyclerView.Adapter<MyHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            var inflate = LayoutInflater.from(activity).inflate(R.layout.item_sample_view, parent, false)
            return MyHolder(inflate)
        }

        override fun getItemCount(): Int {
            return mData.size
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            if (holder is MyHolder) {
                holder.itemView.item_image.setImageResource(mData[position])
                var layoutParams = holder.itemView.item_image.layoutParams
                if (position.rem(2) == 0) {
                    layoutParams.height = ConvertUtils.dp2px(300f)
                } else {
                    layoutParams.height = ConvertUtils.dp2px(150f)
                }
                holder.itemView.item_image.layoutParams = layoutParams
            }
        }

    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}