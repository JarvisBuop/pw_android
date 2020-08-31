package com.jdev.wandroid.pattern.state

/**
 * Created by JarvisDong on 2018/12/16.
 * @Description:
 * @see:
 *
 * 设计模式7--状态模式
 *
 * 抽象状态类
 *
 * >>在每种状态下,具体的操作接口;和策略不同的是强调的是处于一种状态,各种状态间平行;
 */
interface State{
    fun nextChannel()
    fun preChannel()
    fun turnUp()
    fun turnDown()
}