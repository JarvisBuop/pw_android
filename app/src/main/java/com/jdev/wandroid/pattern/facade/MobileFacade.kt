package com.jdev.wandroid.pattern.facade

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * 设计模式22--外观模式
 *
 * [系统对外的统一接口]
 *
 * >>将一些功能组合,对外提供统一接口,一般用于封装api;
 */
class MobileFacade {
    private var phoneImpl = PhoneImpl()
    private var cameraImpl = CameraImpl()

    /**
     * 对外提供的一系列方法;
     */
    fun videoChat(){
        System.out.println("-> video chat start")
        cameraImpl.takeSnap()
        phoneImpl.dial()
    }

}
