package com.jdev.wandroid.pattern.singleton

import org.junit.Before
import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/10.
 *
 * @Description:
 * @see:
 *
 * 测试单例设计模式
 */
class TestSingleTon {

    @Before
    fun setUp() {

    }

    @Test
    @Throws(Exception::class)
    fun TestSingleFun() {
        var instance1 = SingletonDemo2.getInstance()
        var instance2 = SingletonDemo2.getInstance()
        System.out.println("jarvis1" + instance1.toString() + " :: " + instance2.toString())
        var instance3 = SingletonDemo.getInstance()
        var instance4 = SingletonDemo.getInstance()
        System.out.println("jarvis2" + instance3.toString() + " :: " + instance4.toString())
        var instance5 = SingletonDemo3.instance
        var instance6 = SingletonDemo3.instance
        System.out.println("jarvis3" + instance5.toString() + " :: " + instance6.toString())
        var instance7 = SingletonDemo4
        var instance8 = SingletonDemo4
        System.out.println("jarvis4" + instance7.toString() + " :: " + instance8.toString())
    }
}