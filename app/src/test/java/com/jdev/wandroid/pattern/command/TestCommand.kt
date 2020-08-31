package com.jdev.wandroid.pattern.command

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * 测试命令模式;
 */

class TestCommand {

    @Test
    fun testCommand() {
        var mReceiver = TetrisMachineReceiver()

        val leftCommand = LeftCommand(mReceiver)
        var rightCommand = RightCommand(mReceiver)
        var topCommand = TopCommand(mReceiver)
        var bottomCommand = BottomCommand(mReceiver)

        var buttonInvoker = ButtonInvoker()
        buttonInvoker.also {
            it.leftCommand = leftCommand
            it.rightCommand = rightCommand
            it.topCommand = topCommand
            it.bottomCommand = bottomCommand
        }

        buttonInvoker.toLeft()
        buttonInvoker.toRight()
        buttonInvoker.toTop()
        buttonInvoker.toBottom()
    }
}
