package com.jdev.wandroid.pattern.iterator

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * 设计模式13--迭代器模式
 *
 * [迭代器类]
 *
 * >>统一多个数据的遍历接口,不需暴露该对象的内部显示
 */
interface CustomIterator<out E> : Iterator<E>