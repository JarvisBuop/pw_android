package com.jdev.wandroid.pattern.facade

/**
 * Created by JarvisDong on 2018/12/27.
 * OverView:
 *
 * [抽象拍照类]
 *
 * >>功能点
 */
interface Camera {
    fun takeSnap()
}

class CameraImpl : Camera {
    override fun takeSnap() {
        System.out.println("snapshot running...")
    }
}