package com.jdev.wandroid.pattern.Interpreter

import java.util.*

/**
 * Created by JarvisDong on 2018/12/17.
 * @Description:
 * @see:
 *
 * 与解释器相关业务--计算;
 *
 * >>分为终结运算符(不能再次推导的式子)和非终结运算符;
 */
class Calculator public constructor(expression: String) {
    private var mExpStack = Stack<ArithmeticExpression>()

    init {
        var exp1: ArithmeticExpression? = null
        var exp2: ArithmeticExpression? = null
        var elements = expression.split(" ")

        /**
         * compile error
         */
//        for (index: Int in 0..elements.size - 1) {
//            System.out.println("Calculator: " + " " + index + " ")
//            //取分离开的字符串第一个字符;
//            when (elements[index].trim().get(0)) {
//                '+' -> {
//                    exp1 = mExpStack.pop()
//                    exp2 = NumExpression(Integer.valueOf(elements[++index]))
//                    mExpStack.push(AddtionExpression(exp1, exp2))
//                }
//                else -> {
//                    mExpStack.push(NumExpression(Integer.valueOf(elements[index])))
//                }
//            }
//        }

//        for((index,value) in elements.withIndex()){
//
//        }

        /**
         * for循环中不允许var ,所以如果在循环体中使用index++,就不能使用for循环了;
         */
        var index: Int = 0
        while (index < elements.size) {
            System.out.println("Calculator: " + " " + index + " " + elements[index])
            //取分离开的字符串第一个字符;
            when (elements[index].trim().get(0)) {
                '+' -> {
                    exp1 = mExpStack.pop()
                    exp2 = NumExpression(Integer.valueOf(elements[++index]))
                    mExpStack.push(AddtionExpression(exp1, exp2))
                }
                else -> {
                    mExpStack.push(NumExpression(Integer.valueOf(elements[index])))
                }
            }
            index++
        }
    }

    fun calculate(): Int {
        return mExpStack.pop().interpreter()
    }
}