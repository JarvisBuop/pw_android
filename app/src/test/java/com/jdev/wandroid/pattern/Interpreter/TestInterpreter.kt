package com.jdev.wandroid.pattern.Interpreter

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/17.
 * @Description:
 * @see:
 *
 * 测试解释器模式
 *
 */
class TestInterpreter {

    @Test
    fun testInterpreter() {
        var calc = Calculator("1 + 2 + 3 + 4")
        System.out.println("result: " + calc.calculate())
    }
}