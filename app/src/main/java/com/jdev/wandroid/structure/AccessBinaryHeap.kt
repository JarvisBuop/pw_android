package com.jdev.wandroid.structure

import java.lang.IndexOutOfBoundsException
import java.util.*

/**
 * info: create by jd in 2020/9/23
 * @see:
 * @description: 二叉堆 - 自我调整
 *
 */
class AccessBinaryHeap {

    companion object {
        /**
         * 上浮调整
         *
         * 父parentIndex;
         * leftIndex = 2*parentIndex+1;
         * rightIndex = 2*parentIndex+2;
         */
        fun upAdjust(array: IntArray, length: Int) {
            var childIndex = length - 1
            var parentIndex = (childIndex - 1) / 2  // parentIndex = (leftIndex-1)/2
            var temp = array[childIndex] //保存插入的叶子节点值,用于最后的赋值;

            while (childIndex > 0 && temp < array[parentIndex]) {
                System.out.println("父节点[${array[parentIndex]} in ${parentIndex}] ==> 孩子节点[${array[childIndex]} in ${childIndex}]")
                array[childIndex] = array[parentIndex]
                childIndex = parentIndex
                parentIndex = (childIndex - 1) / 2
            }
            //上浮,一直等所有的值都已上浮后,然后赋值temp;
            array[childIndex] = temp
            System.out.println("上浮结束 , 结束位置 $temp in $childIndex")
        }

        /**
         * 下沉调整
         *
         *
         */
        fun downAdjust(array: IntArray, parentIndex: Int, length: Int) {
            var temp = array[parentIndex]
            var leftIndex = 2 * parentIndex + 1 //左节点
            var tempParentIndex = parentIndex
            System.out.println(" \n 下沉开始 , 开始位置 ${array[tempParentIndex]} in $tempParentIndex")
            while (leftIndex < length) {
                //右节点;
                if (leftIndex + 1 < length && array[leftIndex + 1] < array[leftIndex]) {
                    System.out.println("下沉使用右节点")
                    leftIndex += 1
                }
                if (temp <= array[leftIndex]) {
                    break
                }
                System.out.println("孩子节点[${array[leftIndex]} in ${leftIndex}]  ==>  父节点[${array[tempParentIndex]} in ${tempParentIndex}]")
                array[tempParentIndex] = array[leftIndex]
                tempParentIndex = leftIndex
                leftIndex = 2 * tempParentIndex + 1
            }
            array[tempParentIndex] = temp
            System.out.println("下沉结束 , 结束位置 $temp in $tempParentIndex")
        }

        /**
         * 构建堆 ,从最后一个非叶子节点做下沉调整
         */
        fun buildHeap(array: IntArray) {
            var parentIndex = (array.size - 2) / 2 //rightIndex = 2*parentIndex+2  -> parentIndex = (rightIndex -2)/2
            for (i in parentIndex downTo 0 step 1) {
                downAdjust(array, i, array.size)
            }
        }
    }

    var array: IntArray
    var size: Int = 0

    init {
        array = IntArray(10)
    }

    fun enQueue(key: Int) {
        if (size >= array.size) {
            resize()
        }
        array[size++] = key
        upAdjust(array,size)
        System.out.println("enQueue: " + array.contentToString())
    }

    fun deQueue(): Int {
        if (size <= 0) throw IndexOutOfBoundsException("empty")

        var heapTop = array[0]
        array[0] = array[--size]
        downAdjust(array, 0, size)
        System.out.println("deQueue: " + array.contentToString())
        return heapTop
    }

    private fun resize() {
        var newSize = size * 2
        array = Arrays.copyOf(array, newSize)
    }


}