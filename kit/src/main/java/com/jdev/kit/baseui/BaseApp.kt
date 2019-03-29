package com.jarvisdong.kit.baseui

import android.app.Application

/**
 * Created by JarvisDong on 2018/12/5.
 * OverView:
 */
open class BaseApp : Application() {

    companion object {
        private var instance: BaseApp?= null
        fun getApp(): BaseApp {
            return instance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}