package com.jdev.wandroid.utils

import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.jdev.wandroid.mockdata.ItemVo
import com.jdev.wandroid.ui.act.ContainerActivity

class OpenUtils {
    companion object {
        fun open(context: Context,itemVo: ItemVo?) {
            itemVo ?: return
            if (!StringUtils.isEmpty(itemVo.actName)) {
                val intent = Intent().apply {
                    setClassName(context, itemVo.actName!!)
                }
                context.packageManager.queryIntentActivities(intent,0).apply {
                    if(this.isNotEmpty()){
                        context.startActivity(intent)
                    }else{
                        ToastUtils.showShort("not found")
                    }
                }
            } else if (!StringUtils.isEmpty(itemVo.fragName)) {
                ContainerActivity.launch(context,itemVo.fragName)
            }
        }
    }
}