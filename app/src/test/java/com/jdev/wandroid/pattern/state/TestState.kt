package com.jdev.wandroid.pattern.state

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/16.
 * @Description:
 * @see:
 *
 * 测试状态模式
 */
class TestState{

    @Test
    fun testState(){
        var tvController = TvController()
        tvController.poweron()
        tvController.nextChannel()
        tvController.turnUp()
        tvController.poweroff()
        tvController.turnDown()

    }
}