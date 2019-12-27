package com.jarvisdong.kit.utils

import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.jarvisdong.kit.baseui.BaseApp


/**
 * Created by JarvisDong on 2018/9/26.
 * OverView:
 * 常用工具类;
 *
 * 静态方法;
 *
 */
class ResourceIdUtils {
    //伴生对象,常用来作为工具类 static方法;

    companion object {
        fun getStringById(@StringRes intRes: Int ): String {
            return BaseApp.getApp().resources.getString(intRes)
        }

        fun getDrawableById(@DrawableRes intRes: Int): Drawable {
            return BaseApp.getApp().resources.getDrawable(intRes)
        }

        fun getColorById(@ColorRes res: Int): Int {
            return BaseApp.getApp().resources.getColor(res)
        }
    }
}