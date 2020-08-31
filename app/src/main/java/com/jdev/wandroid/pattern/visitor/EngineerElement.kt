package com.jdev.wandroid.pattern.visitor

import java.util.*

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [具体元素]
 */
class EngineerElement public constructor(aName :String): Element(aName){

    fun getCodeLines() : Int{
        return Random().nextInt(10*10000)
    }

    override fun accept(visitor: Visitor) {
        visitor.visitElementA(this)
    }
}