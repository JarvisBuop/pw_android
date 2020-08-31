package com.jdev.wandroid.pattern.mediator

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [具体同事类]
 *
 * >>需要交互类
 */
class ConcreateGraphicsColleague public constructor(mediator: Mediator): Colleague(mediator){

    fun videoPlay(data:String){
        System.out.println("视频: "+ data)
    }
}