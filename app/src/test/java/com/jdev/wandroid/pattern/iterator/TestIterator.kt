package com.jdev.wandroid.pattern.iterator

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * 测试迭代器设计模式;
 */
class TestIterator {

    @Test
    fun testIterator() {
        //两个容器;
        var concreateAggrate1 = ConcreateAggrate1()
        var concreateAggrate2 = ConcreateAggrate2()

        //一次遍历;
        check(concreateAggrate1.iterator())
        System.out.println("---------------------")
        check(concreateAggrate2.iterator())
    }

    companion object {
        fun check(iterator: CustomIterator<Employee>) {
            while (iterator.hasNext()) {
                System.out.println(" :: " + iterator.next().toString())
            }
        }
    }
}