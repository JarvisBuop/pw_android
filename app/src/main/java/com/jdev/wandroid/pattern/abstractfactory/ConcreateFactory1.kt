package com.jdev.wandroid.pattern.abstractfactory

/**
 * Created by JarvisDong on 2018/12/15.
 * @Description:
 * @see:
 *
 * 具体工厂类1
 */
class ConcreateFactory1 : AbstractFactory() {
    override fun createProductB(): AbstractProductB {
        return ConcreateProductB1()
    }

    override fun createProductA(): AbstractProductA {
        return ConcreateProductA1()
    }

}