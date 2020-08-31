package com.jdev.wandroid.pattern.template

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [具体实现类]
 */
class MilitaryComputerTemplate : AbstractTemplate() {
    override fun log() {
        System.out.println("指纹识别等复杂验证登录")
    }
}