package com.jdev.wandroid.pattern.decorator

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * 设计模式20--装饰者模式
 *
 * 与代理的区别在于,装饰者关注于扩展,增强功能,代理关注于控制功能;
 * 装饰者继承关系的一个替代;代理是给一个对象提供一个代理对象,控制原有对象的引用;
 *
 * [抽象装饰者]
 *
 * >>动态扩展对象的功能,比继承子类更加灵活;
 * >>与代理一样`被修饰者`和`修饰者`都必须继承同一个类;
 */
abstract class Decorator public constructor(var component : Component): Component(){

    override fun operate() {
        component?.operate()
    }
}