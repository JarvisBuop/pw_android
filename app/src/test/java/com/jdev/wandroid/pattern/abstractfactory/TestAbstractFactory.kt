package com.jdev.wandroid.pattern.abstractfactory

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/15.
 * @Description:
 * @see:
 */
class TestAbstractFactory{

    @Test
    fun testAbstractFactory(){
        var abstractFactory1 = ConcreateFactory1()
        abstractFactory1.createProductA().method()
        abstractFactory1.createProductB().method()

        System.out.println("========================")

        var abstractFactory2 = ConcreateFactory2()
        abstractFactory2.createProductA().method()
        abstractFactory2.createProductB().method()
    }
}