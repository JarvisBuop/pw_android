package com.jdev.wandroid.pattern.adapter

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/26.
 * @Description:
 * @see:
 */
class TestAdapter{

    @Test
    fun testAdapter(){
        /**
         * 类适配器
         */
        var adapter = Adapter()
        System.out.println("out: "+ adapter.getVolt5())

        /**
         * 对象适配器
         */
        var objAdapter = ObjectAdapter(adaptee = Adaptee())
        System.out.println("out: "+objAdapter.getVolt5())

    }
}