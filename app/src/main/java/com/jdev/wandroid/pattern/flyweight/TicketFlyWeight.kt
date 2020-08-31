package com.jdev.wandroid.pattern.flyweight

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * 设计模式21--享元模式
 *
 * 即对象池,细粒度的对象;
 *
 * [抽象类]
 *
 * >>用于大量重复对象,缓冲池;
 */
interface TicketFlyWeight {
    fun showTicketInfo(bunk: String)
}