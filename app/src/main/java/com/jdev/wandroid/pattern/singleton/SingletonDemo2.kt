package com.jdev.wandroid.pattern.singleton

/**
 * Created by JarvisDong on 2018/12/10.
 * @Description:
 * @see:
 * 2.静态内部类单例
 */

class SingletonDemo2 private constructor() {

    companion object {
        fun getInstance(): SingletonDemo2 {
            return Holder.instance
        }
    }

    private object Holder {
        val instance = SingletonDemo2()
    }
}
