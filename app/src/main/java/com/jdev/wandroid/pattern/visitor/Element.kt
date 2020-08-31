package com.jdev.wandroid.pattern.visitor

import java.util.*

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [抽象元素类]
 *
 * >>定义一个接受访问者方法的方法accept,每一个元素都可以被访问者访问;
 * >>属于对象结构,相对稳定;
 */
abstract class Element public constructor(aName : String){
    lateinit var name:String
    var kpi:Int = 0

    init {
        this.name = aName
        kpi = Random().nextInt(10)
    }

    /**
     * key: 每个元素提供一个accept方法供访问者访问;
     */
    abstract fun accept(visitor: Visitor)
}