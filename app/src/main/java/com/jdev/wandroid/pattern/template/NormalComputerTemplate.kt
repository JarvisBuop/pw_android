package com.jdev.wandroid.pattern.template

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [具体实现类]
 */

class NormalComputerTemplate : AbstractTemplate(){

    override fun log() {
        System.out.println("用户和密码验证登录")
    }
}
