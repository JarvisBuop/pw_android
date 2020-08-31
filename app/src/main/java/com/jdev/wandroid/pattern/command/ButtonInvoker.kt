package com.jdev.wandroid.pattern.command

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * [请求者类]
 *
 * >>调用命令的地方,最后命令调用的是接受者的方法
 */
class ButtonInvoker {
    var leftCommand: LeftCommand? = null
    var rightCommand: RightCommand? = null
    var topCommand: TopCommand? = null
    var bottomCommand: BottomCommand? = null

    fun toLeft() {
        leftCommand?.execute()
    }

    fun toRight() {
        rightCommand?.execute()
    }

    fun toTop() {
        topCommand?.execute()
    }

    fun toBottom() {
        bottomCommand?.execute()
    }
}