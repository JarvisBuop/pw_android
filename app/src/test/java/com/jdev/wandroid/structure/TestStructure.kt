package com.jdev.wandroid.structure

import org.junit.Test

/**
 * info: create by jd in 2020/9/11
 * @see:
 * @description:
 *
 */
class TestStructure {


    @Test
    fun testArray() {
        var accessArray = AccessArray(3)
        accessArray.insert(2, 2)
        accessArray.insert(2, 22)
        accessArray.insert(1, 1)
        accessArray.insert(0, -1)
        accessArray.insert(3, 3)

        System.out.println(accessArray.toString())
    }

    @Test
    fun textLinkList() {
        var accessLinkList = AccessLinkList()

        accessLinkList.insert(0, -1)
        accessLinkList.insert(1, 1)
        accessLinkList.insert(2, 2)
        accessLinkList.insert(3, 3)
        accessLinkList.insert(1, 11)
        accessLinkList.delete(0)

        System.out.println(accessLinkList.toString())
    }

    @Test
    fun textCycleQueue() {
        var accessCycleQueue = AccessCycleQueue(6)

        accessCycleQueue.enQueue(3)
        accessCycleQueue.enQueue(5)
        accessCycleQueue.enQueue(6)
        accessCycleQueue.enQueue(8)
        accessCycleQueue.enQueue(1)
        accessCycleQueue.deQueue()
        accessCycleQueue.deQueue()
        accessCycleQueue.deQueue()
        accessCycleQueue.enQueue(2)
        accessCycleQueue.enQueue(4)
        accessCycleQueue.enQueue(9)
//        accessCycleQueue.enQueue(7)


        System.out.println(accessCycleQueue.toString())

    }
}