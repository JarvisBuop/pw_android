package com.jdev.wandroid.pattern.decorator

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * 测试装饰者模式;
 */
class TestDecorator {

    @Test
    fun testDecorator() {
        var person = ConcreateComponent()
        person.operate()
        System.out.println("-----------")
        //修饰
        var expensiveCloth = ExpensiveClothDecorator(person)
        expensiveCloth.operate()

        System.out.println("-----------")
        var cheapCloth = CheapClothDecorator(person)
        cheapCloth.operate()
    }
}