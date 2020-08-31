package com.jdev.wandroid.pattern.visitor

import java.util.*


/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [对象结构]
 *
 *  >>管理元素集合,并可以迭代这些元素供访问者访问;
 *
 * businesseport 报表
 */
class ObjectStructure {
    /**
     * 所有员工的列表;
     */
    var mStaffs = LinkedList<Element>()

    init {
        mStaffs.add(ManagerElement("manager wang"))
        mStaffs.add(ManagerElement("manager mao"))
        mStaffs.add(EngineerElement("engineer dong"))
        mStaffs.add(EngineerElement("engineer wu"))
        mStaffs.add(EngineerElement("engineer gao"))
        mStaffs.add(EngineerElement("engineer zhao"))
    }

    /**
     * 每个角色想关注的点不同,可传入不同的访问者,查看想知道的内容;
     */
    fun showReport(visitor: Visitor){
        for(element in mStaffs){
            element.accept(visitor)
        }
    }
}