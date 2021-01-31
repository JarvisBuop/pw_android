package com.jdev.wandroid.utils

import android.annotation.SuppressLint
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Magnifier
import android.widget.TextView
import com.chad.library.adapter.base.BaseViewHolder
import com.jarvisdong.kit.utils.ResourceIdUtils
import com.jdev.wandroid.R
import com.jdev.wandroid.mockdata.ItemVo
import com.jdev.wandroid.mockdata.LEVEL

object ViewUtils {
    @JvmStatic
    fun setMsgIntoView(msg: String?, textView: TextView?) {
        if (msg != null && textView != null) {
            textView.text = msg
        }
    }

    @JvmStatic
    fun setImageRes(imageView: ImageView?, resId: Int) {
        if (imageView != null && resId != -1) {
            imageView.setImageResource(resId)
        }
    }

    interface OnCallback<T> {
        fun callback(ts: T?)
    }

    fun bind(helper: BaseViewHolder?, item: Any?) {
        if (item is ItemVo) {
            helper?.apply {
                setText(R.id.txt_title, item.title)
                setText(R.id.txt_desc, item.desc)
                setText(R.id.txt_content, item.level.toString())
                when (item.level) {
                    LEVEL.CRITICAL -> {
                        setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.red))
                    }
                    LEVEL.HIGH -> {
                        setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.color_orange))
                    }
                    LEVEL.MIDDLE -> {
                        setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.color_violet))
                    }
                    LEVEL.LOW -> {
                        setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.color_blue))
                    }
                    LEVEL.NOPE -> {
                        setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.color_green))
                    }
                    else -> {
                        setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.text_second_color))
                    }
                }
                //magnifier
                displayMagifier(helper.itemView)
            }
        }
    }

    private var longPressFlag: Boolean = false
    @SuppressLint("ClickableViewAccessibility")
    private fun displayMagifier(itemView: View?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            longPressFlag = false
            var magnifier: Magnifier? = null
            val viewPosition = IntArray(2)
            itemView?.setOnTouchListener { v, event ->
                if (longPressFlag) {
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            if (magnifier == null) {
                                magnifier = Magnifier(itemView)
                            }
                            v.getLocationOnScreen(viewPosition)
                            magnifier?.show(event.rawX - viewPosition[0], event.rawY - viewPosition[1])
                        }
                        MotionEvent.ACTION_UP -> {
                            magnifier?.dismiss()
                        }
                    }
                }
                return@setOnTouchListener false
            }
            itemView?.setOnLongClickListener {
                longPressFlag = true
                return@setOnLongClickListener true
            }
        }
    }
}