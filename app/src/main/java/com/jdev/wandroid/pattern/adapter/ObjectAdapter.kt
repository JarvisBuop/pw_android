package com.jdev.wandroid.pattern.adapter

/**
 * Created by JarvisDong on 2018/12/26.
 * @Description:
 * @see:
 *
 * [对象适配器]
 *
 * >>经典list模型
 * >>代理被适配的对象,输出为期待的接口;
 */
class ObjectAdapter public constructor(var adaptee: Adaptee) : Target {

    override fun getVolt5(): Int {
        return 5
    }

    fun getVolt220(): Int {
        return adaptee.getVolt220()
    }

}