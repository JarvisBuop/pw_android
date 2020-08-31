package com.jdev.wandroid.pattern.Interpreter

/**
 * Created by JarvisDong on 2018/12/17.
 * @Description:
 * @see:
 *
 * 设计模式9--解释器模式
 *
 * 文法
 *
 * 抽象解释器
 */
abstract class ArithmeticExpression{
    abstract fun interpreter() : Int
}