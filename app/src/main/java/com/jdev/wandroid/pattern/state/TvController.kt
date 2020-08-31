package com.jdev.wandroid.pattern.state

/**
 * Created by JarvisDong on 2018/12/16.
 * @Description:
 * @see:
 *
 * 具体的状态模式中的context--电视机
 *
 * >>也相当于一个代理,可以直接设置state,更改所有的行为;
 */
class TvController : PowerController {
    private lateinit var state : State

    fun setState(state: State){
        this.state = state
    }

    override fun poweron() {
        setState(PowerOnState())
        System.out.println("开机啦")
    }

    override fun poweroff() {
        setState(PowerOffState())
        System.out.println("关机啦")
    }

    fun nextChannel(){
        state.nextChannel()
    }
    fun preChannel(){
        state.preChannel()
    }
    fun turnUp(){
        state.turnUp()
    }
    fun turnDown(){
        state.turnDown()
    }

}