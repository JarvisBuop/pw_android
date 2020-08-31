package com.jdev.wandroid.pattern.command

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * [具体命令]
 *
 * >>持有对接受者的引用,作为参数传递到请求者类;
 */
class RightCommand public constructor(var mReceiver:TetrisMachineReceiver): Command {

    override fun execute() {
        mReceiver?.toRight()
    }

}