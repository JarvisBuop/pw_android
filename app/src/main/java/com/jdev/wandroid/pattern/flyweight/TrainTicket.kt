package com.jdev.wandroid.pattern.flyweight

import java.util.*

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * [具体的享元对象]
 */
class TrainTicket : TicketFlyWeight {
    var from: String
    var to: String
    lateinit var bunk: String //铺位
    var price: Int = 0

    constructor(from: String, to: String) {
        this.from = from
        this.to = to
    }

    override fun showTicketInfo(bunk: String) {
        price = Random().nextInt(100)
        System.out.println("购买车票: 从 " + from + " 到 " + to + " 价格: " + price + " 铺位: " + bunk)
    }

}
