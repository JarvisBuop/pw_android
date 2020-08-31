package com.jdev.wandroid.pattern.visitor

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [具体访问者]
 *
 * 访问需要的每个元素属性;
 */
class ConcreateCTOVisitor : Visitor {

    override fun visitElementA(elementA: EngineerElement) {
        System.out.println("engineer: " + elementA.name + " code num: " + elementA.getCodeLines())
    }

    override fun visitElementB(elementB: ManagerElement) {
        System.out.println("manager: " + elementB.name + " product num: " + elementB.products)
    }

}