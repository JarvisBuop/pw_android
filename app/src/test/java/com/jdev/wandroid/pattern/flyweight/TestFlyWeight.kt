package com.jdev.wandroid.pattern.flyweight

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * 测试享元模式
 */
class TestFlyWeight {

    @Test
    fun testFlyWeight() {
        var ticket = TicketFlyWeightFactory.getTicket("beijing", "shanghai")
        ticket.showTicketInfo("上铺")

        var ticket1 = TicketFlyWeightFactory.getTicket("beijing", "shanghai")
        ticket1.showTicketInfo("下铺")

        var ticket2 = TicketFlyWeightFactory.getTicket("beijing", "shanghai")
        ticket2.showTicketInfo("坐票")
    }
}