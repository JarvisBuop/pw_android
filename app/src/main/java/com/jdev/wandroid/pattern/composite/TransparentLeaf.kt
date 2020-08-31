package com.jdev.wandroid.pattern.composite

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [具体的叶子节点]
 */
class TransparentLeaf public constructor(name: String) : TransparentComponent(name) {
    override fun doSomething() {
        System.out.println(name + " leaf ")
    }

    override fun addChild(component: TransparentComponent) {
        throw UnsupportedOperationException("can not operate to leaf")
    }

    override fun removeChild(component: TransparentComponent) {
        throw UnsupportedOperationException("can not operate to leaf")
    }

    override fun getChildren(index: Int): TransparentComponent {
        throw UnsupportedOperationException("can not operate to leaf")
    }

}