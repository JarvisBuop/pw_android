package com.jdev.wandroid.utils

import com.blankj.utilcode.util.GsonUtils
import com.google.gson.reflect.TypeToken
import com.jarvisdong.kit.baseui.BaseApp
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


object ParseUtils {
    fun getConfigJson(): String? {
        val stringBuilder = StringBuilder()
        try {
            val open = BufferedReader(InputStreamReader(BaseApp.getApp().assets.open("app_config.json")))
            var line: String?
            while (open.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

    fun <T> getMainConfigList(childsKey: String): List<T>? {
        val configJson = getConfigJson()
        configJson ?: return null
        val main = JSONObject(configJson).getJSONObject("main")
        val childListStr = main.optString(childsKey)
        return GsonUtils.fromJson<List<T>>(childListStr, object : TypeToken<List<T>>() {}.type)
    }
}