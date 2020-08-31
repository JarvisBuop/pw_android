package com.jdev.wandroid.pattern.builder

/**
 * Created by JarvisDong on 2018/12/13.
 * OverView:
 * 设计模式2--构造者设计模式;
 *
 * 抽象产品类--computer
 *
 * >>提供可自由定制抽象类;
 */
abstract class Computer constructor() {
    protected lateinit var mBoard: String
    protected lateinit var mOs: String

    abstract fun setOS()

    fun setBoard(board:String){
        this.mBoard = board
    }

    override fun toString(): String {
        return "this computer compose by $mBoard operation is: $mOs"
    }
}