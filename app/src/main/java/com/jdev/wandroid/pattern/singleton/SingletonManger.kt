package com.jdev.wandroid.pattern.singleton

/**
 * Created by JarvisDong on 2018/12/12.
 * OverView:
 * 3.容器单例
 *
 * 还有一个枚举单例就不举了(java中注意反序列化问题-->readResolve()方法);
 */
class SingletonManger private constructor() {

    companion object {
        private var objMap = HashMap<String, Any>()

        fun registerService(key: String, instance: Any) = {
            if (!objMap.containsKey(key)) {
                objMap.put(key, instance)
            }
        }

        fun getService(key: String): Any? {
            return objMap.get(key)
        }
    }
}