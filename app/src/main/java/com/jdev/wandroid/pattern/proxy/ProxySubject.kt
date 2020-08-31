package com.jdev.wandroid.pattern.proxy

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [代理主题类]
 *
 */
class ProxySubject public constructor(var realSubject: RealSubject): LawSubject{

    override fun submit() {
        realSubject.submit()
    }

    override fun burden(file: String) {
        realSubject.burden(file)
    }

    override fun defend() {
        realSubject.defend()
    }

    override fun finish() {
        realSubject.finish()
    }

}