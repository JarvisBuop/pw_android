package com.jdev.wandroid.pattern.bridge

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * 设计模式23--桥接模式
 *
 * 将`抽象部分`和`实现部分`分离,可以独立的变化;处理多个维度的变化;
 * 个人感觉就是将某个单独维度的变化抽取出来,避免直接继承;
 *
 * [抽象部分]
 *
 * >>抽象部分的抽象(抽象类)
 */
abstract class Abstraction public constructor(var implementor: Implementor) {

    /**
     * 实现部分的具体实现;
     */
    fun operateImpl() {
        implementor.operateImpl()
    }

    /**
     * 抽象部分的实现
     */
    abstract fun operateAbs()
}
