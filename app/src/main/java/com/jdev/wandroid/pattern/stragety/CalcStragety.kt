package com.jdev.wandroid.pattern.stragety

/**
 * Created by JarvisDong on 2018/12/16.
 * @Description:
 * @see:
 *
 * 设计模式6--策略模式
 *
 * 抽象策略类
 *
 * >>强调可替换性,状态强调的是平行的一种状态;
 */
interface CalcStragety{
    fun calcPcice( km : Int) : Int
}