package com.jdev.wandroid.pattern.visitor

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * 测试访问者模式
 */
class TestVisitor {
    @Test
    fun testVisitor() {
        //构建报表
        var report = ObjectStructure()
        //设置不同的访问者;
        System.out.println("=====给CEO看的报表======")
        report.showReport(ConcreateCEOVisitor())

        System.out.println("=====给CTO看的报表======")
        report.showReport(ConcreateCTOVisitor())
    }
}