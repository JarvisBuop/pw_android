package com.jdev.wandroid.pattern.template

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * 测试模板模式;
 */
class TestTemplate {
    @Test
    fun testTemplate() {
        var template = NormalComputerTemplate()
        template.startUp()

        var template2 = MilitaryComputerTemplate()
        template2.startUp()
    }
}