package com.jdev.wandroid.pattern.state

/**
 * Created by JarvisDong on 2018/12/16.
 * @Description:
 * @see:
 *
 * context环境-维护一个state的类,用于设置不同状态的处理
 *
 * >>电源操作接口,指定不同的两种状态切换方法;
 */
interface PowerController {
    fun poweron()
    fun poweroff()
}