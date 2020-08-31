package com.jdev.wandroid.pattern.iterator

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * [具体的元素类]
 *
 */
class Employee public constructor(){
    var age: Int = 0
    lateinit var name: String
    lateinit var sex: String

    constructor(age: Int, name: String, sex: String) : this() {
        this.age = age
        this.name = name
        this.sex = sex
    }

    override fun toString(): String {
        return " Employee(age=$age, name='$name', sex='$sex') \n"
    }
}
