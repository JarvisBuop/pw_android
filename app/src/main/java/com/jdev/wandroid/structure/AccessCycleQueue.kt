package com.jdev.wandroid.structure

import java.lang.IndexOutOfBoundsException
import java.lang.StringBuilder

/**
 * info: create by jd in 2020/9/16
 * @see:
 * @description: 循环队列
 *
 */
class AccessCycleQueue {
    private var array: IntArray
    private val capacity: Int

    private var front: Int = 0
    private var rear: Int = 0

    constructor(capacity: Int) {
        this.capacity = capacity
        array = IntArray(capacity)
    }


    fun enQueue(data: Int) {
        if ((rear + 1) % capacity == front) {
            throw IndexOutOfBoundsException("队列已满")
        }
        System.out.println("入栈之前: ${toString()} 入栈元素: $data")
        array[rear] = data
        rear = (rear + 1) % array.size
        System.out.println("入栈之后: ${toString()}")
    }

    fun deQueue(): Int {
        if (rear == front) {
            throw IndexOutOfBoundsException("队列已空")
        }
        System.out.println("出栈之前: ${toString()}")
        var deElement = array[front]
        front = (front + 1) % array.size
        System.out.println("出栈之后: ${toString()} 出栈元素: $deElement")
        return deElement
    }

    override fun toString(): String {
        return "AccessCycleQueue: ${array.contentToString()} size=$capacity --- ${output()}"
    }

    fun output(): String {
        var sb = StringBuilder()
        var i = front
        while (i != rear) {
            sb.append(array[i]).append(",")
            i = (i + 1) % array.size
        }
        return sb.toString()
    }
}