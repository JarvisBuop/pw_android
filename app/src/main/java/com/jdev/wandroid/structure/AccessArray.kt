package com.jdev.wandroid.structure

import java.lang.IndexOutOfBoundsException

/**
 * info: create by jd in 2020/9/11
 * @see:
 * @description: 数据结构 - 数组
 *
 * 数组的插入操作
 */
class AccessArray {
    private var size = 0
    private var array: IntArray

    constructor(capacity: Int) {
        array = IntArray(capacity)
        size = array.size
    }

    fun size(): Int {
        return if (size > 0) size else array.size
    }

    fun insert(index: Int, value: Int) {
        if (index < 0 || index > size) {
            throw IndexOutOfBoundsException("超出数组范围")
        }
        System.out.println("\n插入原状态 ${array.contentToString()} index $index")
        if (size >= array.size) {
            resize()
        }

        System.out.println("开始插入数据 old ${array.contentToString()}")
        for (i in size - 1 downTo 0) {
            if (i >= index) {
                System.out.print(" $i ")
                array[i + 1] = array[i]
            }
        }
        array[index] = value
        System.out.println("\n插入数据结束 new ${array.contentToString()}")

        size++
    }

    private fun resize() {
        val newSize = if (array.size >= 3) array.size * 2 else (array.size + (array.size.shr(1)))
        System.out.println("开始扩容 oldArray ${array.contentToString()} originSize ${array.size} newSize $newSize")
        var newArray = IntArray(newSize)
        System.arraycopy(array, 0, newArray, 0, array.size)
        array = newArray

        System.out.println("扩容完毕 newArray ${array.contentToString()} newSize ${array.size}")
    }

    override fun toString(): String {
        return "$size array=${array.contentToString()})"
    }


}