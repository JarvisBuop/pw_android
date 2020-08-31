package com.jdev.wandroid.pattern.factory

/**
 * Created by JarvisDong on 2018/12/15.
 * @Description:
 * @see:
 *
 * 具体的产品类A
 */
class ConcreateProductA : Product() {
    override fun method() {
        System.out.println("创建具体的产品A")
    }
}