package com.jdev.wandroid.pattern.abstractfactory

import com.jdev.wandroid.pattern.abstractfactory.AbstractProductB

/**
 * Created by JarvisDong on 2018/12/15.
 * @Description:
 * @see:
 */
class ConcreateProductB1 : AbstractProductB() {
    override fun method() {
        System.out.println("具体产品类B1--普通发动机")
    }

}