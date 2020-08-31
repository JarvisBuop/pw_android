package com.jdev.wandroid.pattern.proxy

import org.junit.Test
import java.lang.reflect.Proxy

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * 测试代理模式
 */
class TestProxy {

    /**
     * 静态代理;
     */
    @Test
    fun testProxy() {
        var lawSubject = ProxySubject(realSubject = RealSubject())

        lawSubject.submit()
        lawSubject.burden(" file ")
        lawSubject.defend()
        lawSubject.finish()
    }

    /**
     * 动态代理
     */
    @Test
    fun testDynamicProxy() {
        var realSubject = RealSubject()

        //动态代理设置类/匿名内部类代理
        var handler = DynamicProxy(realSubject)

        //返回为动态代理类;
        var dynamicSubject = Proxy.newProxyInstance(
                realSubject::class.java.classLoader,
                arrayOf(LawSubject::class.java),
                handler) as LawSubject

        dynamicSubject.submit()
        dynamicSubject.burden(" file ")
        dynamicSubject.defend()
        dynamicSubject.finish()
    }

    @Test
    fun testDynamicProxy2() {
        var realSubject = RealSubject()

        //返回为动态代理类;
        var dynamicSubject = Proxy.newProxyInstance(realSubject::class.java.classLoader,
                arrayOf(LawSubject::class.java), { proxy, method, args ->
            method.invoke(realSubject, *(args ?: emptyArray()))
        }) as LawSubject

        dynamicSubject.submit()
        dynamicSubject.burden(" file ")
        dynamicSubject.defend()
        dynamicSubject.finish()
    }
}