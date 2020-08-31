package com.jdev.wandroid.pattern.adapter

/**
 * Created by JarvisDong on 2018/12/26.
 * @Description:
 * @see:
 *
 * 设计模式19--适配器模式
 *
 * 将两个不兼容的类融合在一起;可以重复使用的类;生成一个统一的输入接口;
 *
 * 分为`类适配器`和`对象适配器`
 *
 * [类适配器角色]
 *
 * >>适配器,如经典列表中的adapter,作用为将ItemView输出为View抽象的角色;
 */
class Adapter : Adaptee(),Target{

    override fun getVolt5(): Int {
        return 5
    }

}