package com.jdev.wandroid.pattern.mediator

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [具体中介者]
 *
 * >>主板: 将其他需要相互联系的类通过一个中介者联系;
 */
class ConcreateMainBoardMediator : Mediator() {

    /**
     * 各需要相互交互类
     */
    lateinit var cdDevice: ConcreateCDDeviceColleague
    lateinit var cpu: ConcreateCPUColleague
    lateinit var graphic: ConcreateGraphicsColleague


    override fun changed(colleague: Colleague) {
        when (colleague) {
            cdDevice -> {
                handleCD(cdDevice)
            }
            cpu -> {
                handleCPU(cpu)
            }
        }
    }

    fun handleCD(colleague: ConcreateCDDeviceColleague){
        cpu.decodeData(colleague.read())
    }

    fun handleCPU(colleague: ConcreateCPUColleague){
        graphic.videoPlay(colleague.getDecodeData())
    }

}