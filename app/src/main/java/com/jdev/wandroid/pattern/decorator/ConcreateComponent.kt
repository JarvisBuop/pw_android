package com.jdev.wandroid.pattern.decorator

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * [具体的组件实现类]
 *
 * >>作为具体被修饰者;
 */

class ConcreateComponent : Component(){
    override fun operate() {
        System.out.println("dress underwave")
    }

}