package com.jdev.wandroid.pattern.abstractfactory

import com.jdev.wandroid.pattern.abstractfactory.AbstractProductA

/**
 * Created by JarvisDong on 2018/12/15.
 * @Description:
 * @see:
 *
 * 具体工厂构造的具体产品类
 */
class ConcreateProductA1 : AbstractProductA() {
    override fun method() {
        System.out.println("具体产品类A1--普通轮胎")
    }

}