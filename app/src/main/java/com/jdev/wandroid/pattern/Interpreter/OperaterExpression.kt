package com.jdev.wandroid.pattern.Interpreter

/**
 * Created by JarvisDong on 2018/12/17.
 * @Description:
 * @see:
 *
 * 抽象的操作符解释器--+ -
 *
 * >>比如为了解释算数表达式(文法)中的操作符;
 */
abstract class OperaterExpression public constructor(val exp1: ArithmeticExpression,
                                                     val exp2: ArithmeticExpression) : ArithmeticExpression() {

}