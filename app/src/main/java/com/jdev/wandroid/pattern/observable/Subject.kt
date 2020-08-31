package com.jdev.wandroid.pattern.observable

import java.util.*

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * 设计模式11--观察者模式;
 *
 * [被观察者类]
 *
 * >>使用系统提供的接口,也可以自定义一个容器,保存观察者的强引用,每次获取新数据,遍历容器,给每个观察者发送消息;
 */
class Subject : Observable() {

    fun postNewMessage(message: String) {
        setChanged()
        notifyObservers(message)
    }
}