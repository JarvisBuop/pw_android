package com.jdev.wandroid.pattern.mediator

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * 测试中介者模式;
 */
class TestMediator{

    @Test
    fun testMediator(){
        var mainBoard = ConcreateMainBoardMediator()

        mainBoard.cdDevice = ConcreateCDDeviceColleague(mainBoard)
        mainBoard.cpu = ConcreateCPUColleague(mainBoard)
        mainBoard.graphic = ConcreateGraphicsColleague(mainBoard)

        mainBoard.cdDevice.load()
    }
}