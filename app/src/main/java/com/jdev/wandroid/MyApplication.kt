package com.jdev.wandroid

import com.jarvisdong.kit.baseui.BaseApp


/**
 * 壳application
 */
class MyApplication : BaseApp(){
    override fun onCreate() {
        super.onCreate()

        initGlideWebp()
    }
}
