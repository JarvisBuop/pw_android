package com.jdev.wandroid.pattern.visitor

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [具体访问者]
 *
 * >>给出对每一个元素类访问时所产生的具体行为;
 * >>扩展Visitor方便, 每个Visitor 想要关注点不同;
 */
class ConcreateCEOVisitor : Visitor {

    override fun visitElementA(elementA: EngineerElement) {
        System.out.println("engineer: " + elementA.name + " KPI: " + elementA.kpi)
    }

    override fun visitElementB(elementB: ManagerElement) {
        System.out.println("manager: " + elementB.name + " KPI: " + elementB.kpi)
    }

}