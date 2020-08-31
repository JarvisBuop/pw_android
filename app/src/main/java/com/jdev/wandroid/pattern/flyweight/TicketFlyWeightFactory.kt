package com.jdev.wandroid.pattern.flyweight

import java.util.concurrent.ConcurrentHashMap

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * [享元工厂]
 *
 * >>通过池或者容器减少重复对象的创建;
 */
class TicketFlyWeightFactory {

    companion object {
        private var sTicketPool = ConcurrentHashMap<String, TicketFlyWeight>()
        fun getTicket(from: String, to: String): TicketFlyWeight {
            var key = from + "-" + to
            return if (sTicketPool.containsKey(key)) {
                System.out.println(">>缓存 "+ key)
                sTicketPool.get(key) ?: newTicket(from, to).also {
                    sTicketPool.put(key, it)
                }
            } else {
                System.out.println(">>创建 "+ key)
                newTicket(from, to).also {
                    sTicketPool.put(key, it)
                }
            }
        }

        private fun newTicket(from: String, to: String): TicketFlyWeight {
            return TrainTicket(from, to)
        }
    }

}