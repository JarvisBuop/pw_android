package com.jdev.wandroid.pattern.stragety

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/16.
 * @Description:
 * @see:
 *
 * 测试策略模式
 */
class TestStragety {

    @Test
    fun testStragety(){
        var contextCalc = ContextCalc()
        contextCalc.setStrategy(BusStragety())
        System.out.println("策略一计算结果: "+contextCalc.calcPrice(0))
        contextCalc.setStrategy(SubwayStragety())
        System.out.println("策略二计算结果: "+contextCalc.calcPrice(0))

    }
}