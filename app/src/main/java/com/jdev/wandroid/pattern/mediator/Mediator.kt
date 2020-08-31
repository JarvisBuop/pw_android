package com.jdev.wandroid.pattern.mediator

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * 设计模式16--中介者模式
 *
 * 松散耦合,多对多->一对多,星型结构;
 *
 * [抽象中介者]
 *
 * >>将需要耦合的对象集中在中介者对象上,通过中介达到各对象间的相互作用;
 * >>协调多个交互的对象;
 */
abstract class Mediator{
    /**
     * 通过中介者,同事1通知其他同事s
     */
    abstract fun changed(colleague: Colleague)
}