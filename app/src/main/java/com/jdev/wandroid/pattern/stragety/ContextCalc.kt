package com.jdev.wandroid.pattern.stragety

/**
 * Created by JarvisDong on 2018/12/16.
 * @Description:
 * @see:
 *
 * 操作策略的上下文环境
 */
class ContextCalc {
    private lateinit var stragety : CalcStragety


    fun setStrategy(calcStragety: CalcStragety){
        this.stragety = calcStragety
    }

    fun calcPrice(km : Int): Int{
        return stragety?.calcPcice(km)
    }
}