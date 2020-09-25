package com.jdev.wandroid.structure

import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * info: create by jd in 2020/9/23
 * @see:
 * @description: 常见排序算法
 *
 * - 冒泡排序
 * - 选择排序
 * - 插入排序
 * - 归并排序
 * - 希尔排序
 *
 * - 快速排序
 * - 归并排序
 * - 堆排序
 *
 * - 计数排序
 * - 桶排序
 * - 基数排序
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
        //xor 交换数据; 异或法
        array[start] = array[start] xor array[end]
        array[end] = array[start] xor array[end]
        array[start] = array[start] xor array[end]

        //临时数据法;
//        var temp = array[start]
//        array[start] = array[end]
//        array[end] = temp
    }

    @Test
    fun bubbleSort() {
        /**
         * 冒泡排序
         *
         * 时间复杂度: O(n^2)
         */
        var size = array.size
        for (i in 0 until size - 1) {
            for (j in 0 until size - i - 1) {
                if (array[j] > array[j + 1]) {
                    changeOrder(j, j + 1)
                }
            }
        }
    }

    @Test
    fun bubbleSortOptimize1() {
        /**
         * 冒泡排序优化1
         *
         * 最后一次循环,如果有序就跳出循环;
         *
         */
        var size = array.size
        for (i in 0 until size - 1) {
            var isSorted = true //表示没有元素交换是有序状态,跳出循环;
            for (j in 0 until size - i - 1) {
                if (array[j] > array[j + 1]) {
                    changeOrder(j, j + 1)
                    isSorted = false
                }
            }
            if (isSorted) {
                break
            }
        }
    }

    @Test
    fun bubbleSortOptimize2() {
        /**
         * 冒泡排序优化2
         *
         * 对数列`有序区`的界定,避免重复对有序区的比较;
         *
         */
        var size = array.size
        var lastExchangeIndexd = 0
        var sortBorder = size - 1

        for (i in 0 until size - 1) {
            var isSorted = true
            for (j in 0 until sortBorder) {
                if (array[j] > array[j + 1]) {
                    changeOrder(j, j + 1)
                    isSorted = false
                    lastExchangeIndexd = j //更新最后一次交换的位置
                }
            }
            sortBorder = lastExchangeIndexd
            if (isSorted) {
                break
            }
        }
    }

    @Test
    fun bubbleSortCockTail() {
        /**
         * 鸡尾酒排序 (也属于冒泡排序的一种)
         *
         * 对元素的比较和交换过程是双向的;
         *
         */
        var size = array.size
        for (i in 0 until size / 2) {
            //一次循环为先从左到右再从右到左; 即先确定两边的有序数组;
            for (j in i until size - 1 - i) {
                //正向遍历,大的放后面;
                if (array[j] > array[j + 1]) {
                    changeOrder(j, j + 1)
                }
            }
            for (j in size - 1 - i downTo i + 1) {
                //反向遍历,小的放前面;
                if (array[j] < array[j - 1]) {
                    changeOrder(j, j - 1)
                }
            }
        }
    }

    @Test
    fun selectSort() {
        /**
         * 选择排序:
         *
         * 时间复杂度: O(n^2)
         */
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
        /**
         * 插入排序:
         *
         * 时间复杂度: O(n^2)
         */
        var size = array.size
        for (i in 1 until size) {
            for (j in 0 until i) {
                if (array[i] < array[j]) {
                    changeOrder(i, j)
                }
            }
        }
    }

    @Test
    fun quickSort() {
        /**
         * 快速排序
         *
         */
        var size = array.size
        var baseIndex = 0
//        while ()



    }


    @Test
    fun testTraversal() {
        for (i in 0..5) {
            System.out.print("$i ")
        }
        System.out.println("")
        for (i in 0 until 5) {
            System.out.print("$i ")
        }
        System.out.println("")
        for (i in 0 until 5 step 2) {
            System.out.print("$i ")
        }
        System.out.println("")
        for (i in 5 downTo 0) {
            System.out.print("$i ")
        }
        System.out.println("")
        for (i in 5 downTo 0 step 2) {
            System.out.print("$i ")
        }
        System.out.println("")
    }

}