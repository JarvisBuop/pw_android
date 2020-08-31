package com.jdev.wandroid.pattern.bridge

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * 测试桥接模式
 */
class TestBridge{

    @Test
    fun testBridge(){
        //实现部分的具体实现;
        var implementor = SugarImplementor()
        var implementor2 = OrdinaryImplementor()

        //抽象部分的具体实现及桥接
        //将多维度的变化组合到一起;

        //加糖 + 大份
        var abstraction = LargeCoffee(implementor)
        abstraction.operateAbs()

        //加糖 + 小份
        var abstraction2 = SmallCoffee(implementor)
        abstraction2.operateAbs()

        //原味 + 大份
        var abstraction3 = LargeCoffee(implementor2)
        abstraction3.operateAbs()

        //原味 + 小份
        var abstraction4 = SmallCoffee(implementor2)
        abstraction4.operateAbs()
    }
}