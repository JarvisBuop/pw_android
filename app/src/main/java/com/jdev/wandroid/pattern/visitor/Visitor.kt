package com.jdev.wandroid.pattern.visitor

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * 设计模式15--访问者模式
 *
 * 数据操作和数据结构分离,数据结构稳定;
 *
 * [抽象访问者类 抽象类或接口]
 *
 * >>定义对每一个元素访问的行为,
 * >>参数是可以访问的元素,方法个数是可访问的元素个数;
 */
interface Visitor {

    fun visitElementA(elementA: EngineerElement)

    fun visitElementB(elementB: ManagerElement)

}