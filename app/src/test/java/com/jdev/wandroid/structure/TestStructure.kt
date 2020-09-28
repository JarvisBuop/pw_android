package com.jdev.wandroid.structure

import org.junit.Test
import java.util.*
import kotlin.Comparator

/**
 * info: create by jd in 2020/9/11
 * @see:
 * @description:
 *
 */
class TestStructure {

    /**
     * 数组
     */
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

    /**
     * 链表
     */
    @Test
    fun testLinkList() {
        var accessLinkList = AccessLinkList()

        accessLinkList.insert(0, -1)
        accessLinkList.insert(1, 1)
        accessLinkList.insert(2, 2)
        accessLinkList.insert(3, 3)
        accessLinkList.insert(1, 11)
        accessLinkList.delete(0)

        System.out.println(accessLinkList.toString())
    }

    /**
     * 循环队列;
     */
    @Test
    fun testCycleQueue() {
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

    /**
     * 二叉排序树
     */
    @Test
    fun testSortTree1() {
        var linkedList = LinkedList<Int?>(listOf(
//                1, 2, 4, null, null, 5, null, null, 3, null, 6
                1, 2, 4, null, null, 5, null, null, 3, 6, null, null, 7
        ))

        System.out.println("origin data: " + linkedList.joinToString())
        var accessSortTree = AccessSortTree()
        var createBTree1 = accessSortTree.createBTree1(linkedList)

        System.out.print("前序遍历")
        accessSortTree.preOrderTraversal(createBTree1)
        System.out.println("")
        System.out.print("中序遍历")
        accessSortTree.inOrderTraversal(createBTree1)
        System.out.println("")
        System.out.print("后序遍历")
        accessSortTree.postOrderTraversal(createBTree1)
        System.out.println("")
        System.out.print("前序遍历-非递归")
        accessSortTree.preOrderTraversalWithStack(createBTree1)
        System.out.println("")
        System.out.print("层级遍历")
        accessSortTree.levelOrderTraversal(createBTree1)
    }

    /**
     * 二叉堆 相关
     */
    @Test
    fun testHeap() {
        var array = intArrayOf(
                1, 3, 2, 6, 5, 7, 8, 9, 10, 0
        )
        System.out.println("start:  " + array.contentToString())
        AccessBinaryHeap.upAdjust(array, array.size)
        System.out.println("end:  " + array.contentToString())

        array = intArrayOf(
                7, 1, 3, 10, 5, 2, 8, 9, 6
        )
        System.out.println("start:  " + array.contentToString())
        AccessBinaryHeap.buildHeap(array)
        System.out.println("end:  " + array.contentToString())
    }

    @Test
    fun testPriorityQueue() {
        var accessBinaryHeap = AccessBinaryHeap()
        accessBinaryHeap.enQueue(3)
        accessBinaryHeap.enQueue(5)
        accessBinaryHeap.enQueue(10)
        accessBinaryHeap.enQueue(2)
        accessBinaryHeap.enQueue(7)

        System.out.println(" 出队元素：" + accessBinaryHeap.deQueue())
        System.out.println(" 出队元素：" + accessBinaryHeap.deQueue())
    }

    @Test
    fun testLinkListMethod() {
        var linkedList = LinkedList<Int>()
        linkedList.add(1)
        linkedList.add(2)
        linkedList.print()

        linkedList.addFirst(11)
        linkedList.addLast(22)
        linkedList.print()

        linkedList.removeFirst().toStr()
        linkedList.removeLast().toStr()
        linkedList.print()

        linkedList.push(5)
        linkedList.push(6)
        linkedList.print()

        linkedList.pop()
        linkedList.print()

        linkedList.offer(3)
        linkedList.offer(4)
        linkedList.print()

        linkedList.offerFirst(7)
        linkedList.offerLast(8)
        linkedList.print()

        linkedList.first.toStr()
        linkedList.last.toStr()

        linkedList.peek().toStr("peek")
        linkedList.print()
        linkedList.poll().toStr("poll")
        linkedList.print()

        linkedList.peekFirst().toStr("peekFirst")
        linkedList.print()
        linkedList.peekLast().toStr("peekLast")
        linkedList.print()

        linkedList.pollFirst().toStr("pollFirst")
        linkedList.print()
        linkedList.pollLast().toStr("pollLast")
        linkedList.print()

        linkedList.print()
    }

    fun LinkedList<Int>.print() {
        println(joinToString())
    }

    fun Any.toStr(name: String? = "") {
        println("$name  $this")
    }

    @Test
    fun testOperator() {
        123456()
        TestInvoker()()()()()()().output()
    }

    operator fun Int.invoke() {
        println(this)
    }

    class TestInvoker() {
        var data: Int = 0
        operator fun invoke(): TestInvoker {
            data++
            return this
        }

        fun output() {
            System.out.println("$data")
        }
    }
}