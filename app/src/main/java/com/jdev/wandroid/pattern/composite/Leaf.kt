package com.jdev.wandroid.pattern.composite

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [具体叶子节点]
 */
class Leaf public constructor(name: String) : Component(name) {

    override fun doSomething() {
        System.out.println(name + " leaf ")
    }

}