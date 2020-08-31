package com.jdev.wandroid.pattern.proxy

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [动态代理]
 *
 */
class DynamicProxy public constructor(val obj: LawSubject) : InvocationHandler {

    /**
     * proxy: 被代理对象;
     *
     * method: 被代理对象方法;
     *
     * args: 被代理对象参数;
     */
    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        System.out.println(method?.name + "  " + args?.size)
        when (method?.name) {
            "burden" -> {
                var argsNew: Array<out Any>? = args
                if (args != null && args.size != 0) {
                    argsNew = arrayOf(args[0].toString() + " dynamic ")
                } else {
                    argsNew = emptyArray()
                }
                return method.invoke(obj, *argsNew)
            }
            else -> {
                return method!!.invoke(obj, *(args ?: emptyArray()))
            }
        }
    }

}