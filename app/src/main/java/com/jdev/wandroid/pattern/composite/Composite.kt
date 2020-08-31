package com.jdev.wandroid.pattern.composite

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [具体枝干节点]
 */
class Composite public constructor(name: String) : Component(name) {

    var components = arrayListOf<Component>()

    override fun doSomething() {
        System.out.println(name+ " composite ")
        for(component in components){
            component.doSomething()
        }
    }

    fun addChild(child : Component){
        components.add(child)
    }

    fun removeChild(child : Component){
        components.remove(child)
    }

    fun getChildren(index : Int) : Component{
        return components.get(index)
    }

}