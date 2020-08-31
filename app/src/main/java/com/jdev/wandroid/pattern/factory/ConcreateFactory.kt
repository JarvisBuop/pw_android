package com.jdev.wandroid.pattern.factory

/**
 * Created by JarvisDong on 2018/12/15.
 * @Description:
 * @see:
 *
 * 具体的工厂类
 *
 * >>直接生成需要的对象即可;
 */
class ConcreateFactory : Factory() {
    override fun <T : Product> createProduct(clazz: Class<T>): T {
        var p : Product? = null
        try {
            p = Class.forName(clazz.name).newInstance() as Product
        }catch (e:Exception){
            e.printStackTrace()
        }
        return p as T
    }

}