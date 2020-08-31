package com.jdev.wandroid.pattern.observable

import java.util.*

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * 具体的观察者类
 *
 * >>0个或多个观察者;
 */
class ConcreateObserver public constructor(var tagName: String) : Observer {

    override fun update(o: Observable?, arg: Any?) {
        System.out.println("update " + tagName + " :: " + arg)
    }
}