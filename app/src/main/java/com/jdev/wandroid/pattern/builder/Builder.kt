package com.jdev.wandroid.pattern.builder

/**
 * Created by JarvisDong on 2018/12/13.
 * OverView:
 *
 * 抽象构造者类--Builder
 *
 * >>可快速构建对象
 */
abstract class Builder{
    abstract fun buildBoard(board : String)
    abstract fun buildOs()
    abstract fun create():Computer
}