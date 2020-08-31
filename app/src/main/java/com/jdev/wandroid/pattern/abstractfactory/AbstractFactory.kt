package com.jdev.wandroid.pattern.abstractfactory

/**
 * Created by JarvisDong on 2018/12/15.
 * @Description:
 * @see:
 *
 * 设计模式5-抽象工厂模式
 *
 * 抽象工厂类:
 *
 * >>定义抽象产品的生成方法;
 * >>多个业务可选的工厂
 */
abstract class AbstractFactory{
    abstract fun createProductA() : AbstractProductA
    abstract fun createProductB() : AbstractProductB
}