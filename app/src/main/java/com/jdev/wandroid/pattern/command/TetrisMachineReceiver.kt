package com.jdev.wandroid.pattern.command

/**
 * Created by JarvisDong on 2018/12/17.
 * OverView:
 *
 * 设计模式10--命令模式
 *
 * [接受者类]
 *
 * >>真正处理具体逻辑的地方,其实可以直接调用逻辑实现,
 * >>命令模式抽象出待执行的动作,以参数的形式提供出来,类似于回调;
 */
class TetrisMachineReceiver {

    fun toLeft(){
        System.out.println("方块--向左")
    }

    fun toRight(){
        System.out.println("方块--向右")
    }

    fun fastToBottom(){
        System.out.println("方块--快速向下")
    }

    fun transformToTop(){
        System.out.println("方块--变换方向")
    }
}