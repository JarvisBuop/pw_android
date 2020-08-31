package com.jdev.wandroid.pattern.facade

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * 测试外观模式
 */
class TestFacade{

    @Test
    fun testFacade(){
        var mobile = MobileFacade()

        mobile.videoChat()
    }
}