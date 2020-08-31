package com.jdev.wandroid.pattern.singleton

/**
 * Created by JarvisDong on 2018/12/11.
 * OverView:
 * 3.kotlin lazy 单例
 * TODO 相当于DCL单例
 */

class SingletonDemo3 private constructor(){

    companion object {
        val instance: SingletonDemo3 by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SingletonDemo3()
        }
    }
}
