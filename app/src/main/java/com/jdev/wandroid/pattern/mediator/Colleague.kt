package com.jdev.wandroid.pattern.mediator

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * [抽象同事类]
 *
 * >>每一个同事都知道中介者
 * >>通过中介者类互相通知,彼此之间不通知;
 */
abstract class Colleague public constructor(var mediator: Mediator)