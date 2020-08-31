package com.jdev.wandroid.pattern.visitor

import java.util.*


/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [具体元素]
 */
class ManagerElement(aName: String) : Element(aName) {
    var products: Int = 0

    init {
        products = Random().nextInt(10)
    }

    override fun accept(visitor: Visitor) {
        visitor.visitElementB(this)
    }
}