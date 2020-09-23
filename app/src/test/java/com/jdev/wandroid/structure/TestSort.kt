package com.jdev.wandroid.structure

import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * info: create by jd in 2020/9/23
 * @see:
 * @description:
 *
 */
class TestSort {
    lateinit var array: IntArray

    fun IntArray.output(prefix: String = "") {
        System.out.println(prefix + " : " + this.contentToString())
    }

    @Before
    fun prepare() {
        array = intArrayOf(
                3, 1, 2, 8, 5, 7, 9, 10, 6, 4, 1
        )
        array.output("start")
    }

    @After
    fun stop() {
        array.output("end")
    }

    fun changeOrder(start: Int, end: Int) {
        //xor 交换数据;
        array[start] = array[start] xor array[end]
        array[end] = array[start] xor array[end]
        array[start] = array[start] xor array[end]

//        var temp = array[start]
//        array[start] = array[end]
//        array[end] = temp
    }

    @Test
    fun bubbleSort() {
        var size = array.size
        for (i in 0 until size) {
            for (j in 0 until size - i - 1) {
                if (array[j] > array[j + 1]) {
                    changeOrder(j, j + 1)
                }
            }
        }
    }

    @Test
    fun selectSort() {
        var size = array.size
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (array[i] < array[j]) {
                    changeOrder(i, j)
                }
            }
        }
    }

    @Test
    fun insertSort() {
        var size = array.size
        for (i in 1 until size) {
            for (j in 0 until i) {
                if (array[i] < array[j]) {
                    changeOrder(i, j)
                }
            }
        }
    }


}