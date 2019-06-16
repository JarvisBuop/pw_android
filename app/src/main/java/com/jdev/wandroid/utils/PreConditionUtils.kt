package com.jdev.wandroid.utils

import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.utils.StringUtils
import com.jarvisdong.kit.baseui.BaseApp
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * info: create by jd in 2019/4/18
 * @see:
 * @description: 判断 or 简写
 *
 */
class PreConditionUtils {
    companion object {
        fun getSafePositiveDoubleFromStr(number: String?): Double {
            if (StringUtils.isSpace(number)) {
                return -1.0
            } else {
                if (number == "." || number == "-") {
                    return -1.0
                } else {
                    try {
                        val bigDecimal = BigDecimal(number)
                        if (bigDecimal != null) {
                            return bigDecimal.toDouble()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                return -1.0
            }
        }


        /**
         * 设置数字或者隐藏的方法;
         *
         * input : 0.1;5;"3.4k",null,abc,"0"
         */
        fun setFormatStrForText(textView: TextView, string: String?) {
            if (!StringUtils.isSpace(string)) {
                var safeDouble = PreConditionUtils.getSafePositiveDoubleFromStr(string)
                if (safeDouble > 0) {
                    textView.visibility = View.VISIBLE
                    textView.setText(string)
                } else if (!StringUtils.isSpace(string)) {
                    textView.visibility = View.VISIBLE
                    textView.setText(string)
                } else {
                    textView.visibility = View.GONE
                }
            } else {
                textView.visibility = View.GONE
            }
        }


        fun formatDouble(number: Double): String {
            var decimalFormat = DecimalFormat("##.##")
            return decimalFormat.format(number)
        }

        fun getStringByRes(@StringRes res: Int): String {
            return BaseApp.getApp().resources.getString(res)
        }

        fun getColorByRes(@ColorRes res: Int): Int {
            return BaseApp.getApp().resources.getColor(res)
        }

        fun getSafeFormatStr(resFormat: String, vararg strValue: Any): String {
            if (StringUtils.isSpace(resFormat) || strValue == null || strValue.size == 0) return ""
            try {
                return String.format(resFormat, *strValue)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ""
        }

        fun checkNotEmpty(collects: Collection<Any>?): Boolean {
            if (collects != null && !collects.isEmpty()) {
                return true
            }
            return false
        }

//        fun <T> checkNotEmpty(collects: Collection<T>): Boolean {
//            if (collects != null && !collects.isEmpty()) {
//                return true
//            }
//            return false
//        }

        fun checkNotNull(value: Any?): Boolean {
            return value != null
        }

        fun checkNotNulls(vararg values: Any?): Boolean {
            if (values != null && values.size > 0) {
                for (i in 0..values.size - 1) {
                    if (values[i] == null) {
                        return false
                    }
                }
                return true
            }
            return false
        }

        fun checkStringNotNulls(vararg values: String?): Boolean {
            if (values != null && values.size > 0) {
                for (i in 0..values.size - 1) {
                    if (StringUtils.isSpace(values[i])) {
                        return false
                    }
                }
                return true
            }
            return false
        }

        fun checkPositionLegal(collects: Collection<Any>?, position: Int): Boolean {
            if (checkNotEmpty(collects) && collects!!.size > position) {
                return true
            }
            return false
        }

        fun getFormatNumber(num: Long): String {
            if (num < 1000) {
                return num.toString()
            } else if (num < 100000) {
                var thousand = BigDecimal(num / 1000.0).toDouble()
                return String.format("%.1fk", thousand)
            } else if (num < 10000000) {
                var thousand = BigDecimal(num / 10000.0).toDouble()
                return String.format("%dw", thousand)
            } else {
                var thousand = BigDecimal(num / 10000000.0).toDouble()
                return String.format("%dkw", thousand)
            }
        }
    }
}