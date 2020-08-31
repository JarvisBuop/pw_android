package com.jdev.wandroid.pattern.proxy

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [真实主题类]
 *
 */
class RealSubject : LawSubject{
    override fun submit() {
        System.out.println("仲裁")
    }

    override fun burden(file: String) {
        System.out.println("举证 "+ file)
    }

    override fun defend() {
        System.out.println("辩护")
    }

    override fun finish() {
        System.out.println("成功")
    }

}