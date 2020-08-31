package com.jdev.wandroid.pattern.bridge

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * [抽象部分子类扩展变化]
 */
class LargeCoffee constructor(implementor: Implementor) : Abstraction(implementor) {

    override fun operateAbs() {
        System.out.println(" 大杯 " + implementor.operateImpl() + " 咖啡 ")
    }

}