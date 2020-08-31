package com.jdev.wandroid.pattern.factory

/**
 * Created by JarvisDong on 2018/12/15.
 * @Description:
 * @see:
 *
 * 设计模式4-工厂模式
 *
 * [简单工厂模式]--直接使用静态方法造对象;
 *
 * 抽象工厂类
 */
abstract class Factory {

    abstract fun <T : Product> createProduct(clazz: Class<T>): T

    /**
     * 简单工厂模式,静态方法直接构建具体的产品类;
     */
    companion object {
        fun <T : Product> createSimpleObj(clazz:Class<T>): T{
            var p : Product? = null
            try {
                p = Class.forName(clazz.name).newInstance() as Product
            }catch (e:Exception){
                e.printStackTrace()
            }
            return p as T
        }
    }
}