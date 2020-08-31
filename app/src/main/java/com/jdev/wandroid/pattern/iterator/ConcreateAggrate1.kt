package com.jdev.wandroid.pattern.iterator

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * [具体的容器类1]
 */
class ConcreateAggrate1 : Aggregate<Employee> {
    var list: ArrayList<Employee> = arrayListOf()

    init {
        for (i in 0..9) {
            list.add(Employee(i, "name-list" + i, "sex-list" + i))
        }
    }

    override fun add(obj: Employee) {
        list.add(obj)
    }

    override fun remove(obj: Employee) {
        list.remove(obj)
    }

    override fun iterator(): CustomIterator<Employee> {
        return ConcreateIterator1(list)
    }

}