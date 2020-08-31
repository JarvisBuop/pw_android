package com.jdev.wandroid.pattern.composite

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [具体枝干节点]
 */
class TransparentComposite public constructor(name: String) : TransparentComponent(name) {
    var components = arrayListOf<TransparentComponent>()

    override fun doSomething() {
        System.out.println(name + " composite ")
        for(composite in components){
            composite.doSomething()
        }
    }

    override fun addChild(component: TransparentComponent) {
        components.add(component)
    }

    override fun removeChild(component: TransparentComponent) {
        components.remove(component)
    }

    override fun getChildren(index: Int): TransparentComponent {
        return components.get(index)
    }

}