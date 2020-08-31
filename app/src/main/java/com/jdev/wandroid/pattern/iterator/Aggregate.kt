package com.jdev.wandroid.pattern.iterator

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * [容器接口类]
 *
 * >>兼容多种数据的遍历
 */
interface Aggregate<T> {

    fun add(obj: T)

    fun remove(obj: T)

    fun iterator(): CustomIterator<T>
}