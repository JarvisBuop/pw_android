package com.jdev.wandroid.pattern.mediator

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [具体同事类]
 *
 * >>需要交互类
 */
class ConcreateCPUColleague public constructor(mediator: Mediator) : Colleague(mediator) {

    lateinit var dataTemp: String

    fun decodeData(data: String) {
        var plus = data.plus(" cpu decoded ")
        this.dataTemp = plus
        mediator.changed(this)
    }

    fun getDecodeData(): String {
        return dataTemp
    }

}