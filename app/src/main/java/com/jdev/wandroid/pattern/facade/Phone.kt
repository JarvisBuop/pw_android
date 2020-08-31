package com.jdev.wandroid.pattern.facade

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * [抽象电话类]
 *
 * >>一个功能点
 */
interface Phone{
    fun dial()
}

class PhoneImpl :Phone{
    override fun dial() {
        System.out.println("dial running...")
    }
}