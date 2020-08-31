package com.jdev.wandroid.pattern.Interpreter

/**
 * Created by JarvisDong on 2018/12/17.
 * @Description:
 * @see:
 *
 * 加法操作符解释器
 */
class AddtionExpression public constructor(exp1: ArithmeticExpression,
                                           exp2: ArithmeticExpression) :
        OperaterExpression(exp1, exp2) {

    override fun interpreter(): Int {
        return exp1.interpreter() + exp2.interpreter()
    }

}