package com.jdev.kit.utils

import com.blankj.utilcode.util.StringUtils

/**
 * info: create by jd in 2019/4/18
 * @see:
 * @description: 判断 or 简写
 *
 */
class PreConditionUtils {
    companion object {

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
    }
}