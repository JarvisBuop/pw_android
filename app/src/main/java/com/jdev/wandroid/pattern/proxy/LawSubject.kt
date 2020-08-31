package com.jdev.wandroid.pattern.proxy

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * 设计模式17--代理模式
 *
 * [抽象主题类]
 *
 * >>具体的业务方法,为真实主题和代理主题共同的接口方法;
 */
interface LawSubject {
    fun submit()

    fun burden(file: String)

    fun defend()

    fun finish()
}