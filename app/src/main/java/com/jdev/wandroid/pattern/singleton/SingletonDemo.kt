package com.jdev.wandroid.pattern.singleton

/**
 * Created by JarvisDong on 2018/12/10.
 * OverView:
 * 设计模式1--单例设计模式
 *
 * 1.DCL 单例
 *
 */

class SingletonDemo private constructor() {

    companion object {

        @Volatile private var instance: SingletonDemo? = null

        public fun getInstance()  =  {
            instance ?: synchronized(SingletonDemo::class.java) {
                instance ?: SingletonDemo().also {
                    instance = it
                }
            }
        }
    }
}


