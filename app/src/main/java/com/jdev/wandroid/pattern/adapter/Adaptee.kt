package com.jdev.wandroid.pattern.adapter

/**
 * Created by JarvisDong on 2018/12/26.
 * @Description:
 * @see:
 *
 *
 * [需要适配的接口]
 *
 * >>经典列表中,就是需要被处理的ItemView;
 */
open class Adaptee{
    fun getVolt220() : Int{
        return 220
    }
}