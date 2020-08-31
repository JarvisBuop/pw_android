package com.jdev.wandroid.pattern.template

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * 设计模式14--模板模式
 *
 * [抽象类模板]
 *
 * >>定义一套算法框架
 */
abstract class AbstractTemplate{

    open fun powerUp(){
        System.out.println("开启电源")
    }

    open fun checkHardWare(){
        System.out.println("硬件检查")
    }

    open  fun loadOS(){
        System.out.println("载入操作系统")
    }

    open fun log(){
        System.out.println("无验证登录")
    }

    fun startUp(){
        System.out.println("----开机start----")
        powerUp()
        checkHardWare()
        loadOS()
        log()
        System.out.println("----关机end----")
    }
}