package com.jdev.wandroid.pattern.bridge

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * [实现部分单独变化维度]
 */
class OrdinaryImplementor : Implementor{

    override fun operateImpl(): String {
        return "原味"
    }

}