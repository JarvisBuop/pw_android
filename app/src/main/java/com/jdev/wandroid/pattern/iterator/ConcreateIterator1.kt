package com.jdev.wandroid.pattern.iterator

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * [具体的迭代器1]
 *
 * >>迭代是列表;
 */
class ConcreateIterator1 : CustomIterator<Employee> {
    var list: ArrayList<Employee>? = null
    var position: Int = 0

    constructor(list: ArrayList<Employee>) {
        this.list = list
    }


    override fun hasNext(): Boolean {
        return !(list == null || position > list!!.size - 1 || list!!.get(position) == null)
    }

    override fun next(): Employee {
        var obj = list!!.get(position)
        position++
        return obj
    }

}