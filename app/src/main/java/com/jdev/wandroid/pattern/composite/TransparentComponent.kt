package com.jdev.wandroid.pattern.composite

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 * 透明组合模式
 *
 * [抽象根节点]
 *
 * >> 都是相同的结构;
 * >>跟那个区别不大,只是将该类作为中心了;
 */
abstract class TransparentComponent public constructor(var name: String) {

    abstract fun doSomething()

    abstract fun addChild(component: TransparentComponent)

    abstract fun removeChild(component: TransparentComponent)

    abstract fun getChildren(index: Int): TransparentComponent
}