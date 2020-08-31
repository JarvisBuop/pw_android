package com.jdev.wandroid.pattern.builder

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/13.
 * OverView:
 *
 * 测试构造者设计模式
 */
class TestBuilder{

    @Test
    fun testBuilder(){
        /**
         * 具体的构造器类
         */
        var builder : Builder = MacBookBuilder()
        /**
         * 负责构造类
         */
        var director : Director = Director(builder)
        /**
         * 具体产品构造过程
         */
        director.construct("主板")
        /**
         * 返回具体产品
         */
        var macbook = builder.create()

        System.out.println(macbook.toString())
    }
}