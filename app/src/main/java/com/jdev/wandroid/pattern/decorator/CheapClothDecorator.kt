package com.jdev.wandroid.pattern.decorator

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * [具体修饰者2]
 *
 *
 */
class CheapClothDecorator constructor(component: Component):Decorator(component){

    fun dressShorts(){
        System.out.println("dress shorts")
    }

    override fun operate() {
        super.operate()

        dressShorts()
    }
}