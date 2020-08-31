package com.jdev.wandroid.pattern.stragety

/**
 * Created by JarvisDong on 2018/12/16.
 * @Description:
 * @see:
 *
 * 具体的策略之一--subway
 */
class SubwayStragety : CalcStragety{
    override fun calcPcice(km: Int): Int {
        System.out.println("subway calc result")
        return 3
    }

}