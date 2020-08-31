package com.jdev.wandroid.pattern.iterator_intercept

/**
 * Created by JarvisDong on 2018/12/16.
 * @Description:
 * @see:
 *
 * 抽象请求者
 *
 * >>对请求,判断条件等进行封装,如果没有封装只是一个判断则为简化版本;
 */
abstract class AbstractRequest public constructor(var obj:Any){

    //获取处理的内容对象
    fun getContent() : Any{
        return obj
    }

    //获取请求级别
    abstract fun getRequestLevel():Int
}