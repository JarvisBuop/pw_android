package com.jdev.wandroid.pattern.Interpreter

/**
 * Created by JarvisDong on 2018/12/17.
 * @Description:
 * @see:
 *
 * 具体的数字解释器--0-9
 *
 * >>比如为了解释算数表达式(文法)中的数字;
 */
class NumExpression public constructor(var num: Int): ArithmeticExpression(){

    override fun interpreter(): Int {
        return num
    }

}