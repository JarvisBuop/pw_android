package com.jarvisdong.kit.utils

import android.graphics.drawable.Drawable
import com.jarvisdong.kit.baseui.BaseApp


/**
 * Created by JarvisDong on 2018/9/26.
 * OverView:
 * 常用工具类;
 *
 * 静态方法;
 *
 */
class CommonUtils {
    //伴生对象,常用来作为工具类 static方法;

    companion object {
        fun getStringId(intRes: Int): String {
            return BaseApp.getApp().getString(intRes)
        }

        fun getDrawableId(intRes: Int): Drawable {
            return BaseApp.getApp().resources.getDrawable(intRes)
        }
    }
}