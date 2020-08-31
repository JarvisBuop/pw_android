package com.jdev.wandroid.pattern.iterator

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * [具体的迭代器2]
 *
 *
 * >> 迭代是数组;
 */
class ConcreateIterator2 : CustomIterator<Employee> {
    var array : Array<Employee>? = null
    var position: Int = 0

    constructor(array: Array<Employee>?) {
        this.array = array
    }


    override fun hasNext(): Boolean {
        return !(array == null || position > array!!.size - 1 || array!![position] == null)
    }

    override fun next(): Employee {
        var obj = array!!.get(position)
        position++
        return obj
    }

}