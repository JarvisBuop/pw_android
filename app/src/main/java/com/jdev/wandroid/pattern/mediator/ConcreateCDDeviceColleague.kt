package com.jdev.wandroid.pattern.mediator

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [具体同事类]
 *
 * >>需要交互类;
 * >>光驱: 读取数据,通过主板通知cpu通知显卡处理;
 */
class ConcreateCDDeviceColleague public constructor(mediator: Mediator) : Colleague(mediator) {
    lateinit var data: String

    fun read(): String {
        return data
    }

    fun load(){
        data = "视频数据读取~"
        mediator.changed(this)
    }
}