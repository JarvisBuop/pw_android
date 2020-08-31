package com.jdev.wandroid.pattern.iterator

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * [具体的容器类2]
 */
class ConcreateAggrate2 : Aggregate<Employee> {
    var array: Array<Employee> = Array<Employee>(10, { i -> Employee() })

    override fun add(obj: Employee) {

    }

    override fun remove(obj: Employee) {

    }

    init {
        for (i in 0..array.size - 1) {
            array[i] = Employee(i, "name" + i, "sex" + i)
        }
    }

    override fun iterator(): CustomIterator<Employee> {
        return ConcreateIterator2(array)
    }

}