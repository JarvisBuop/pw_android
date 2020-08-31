package com.jdev.wandroid.pattern.factory

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/15.
 * @Description:
 * @see:
 *
 * 测试工厂模式
 * 造数据
 */
class TestFactory{

    @Test
    fun testFactory(){
        var factory = ConcreateFactory()
        var createProduct = factory.createProduct(ConcreateProductA::class.java)
        createProduct.method()

        //简单工厂
        var simplePro = Factory.createSimpleObj(ConcreateProductA::class.java)
        simplePro.method()
    }
}