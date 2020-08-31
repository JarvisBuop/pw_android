package com.jdev.wandroid.pattern.decorator

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * [具体修饰者1]
 *
 *
 */
class ExpensiveClothDecorator constructor(component: Component) : Decorator(component) {

    fun dressShirt() {
        System.out.println("dress shirt")
    }

    fun dressJean() {
        System.out.println("dress jean")
    }

    /**
     * 扩展的功能
     */
    override fun operate() {
        super.operate()

        dressShirt()
        dressJean()
    }
}