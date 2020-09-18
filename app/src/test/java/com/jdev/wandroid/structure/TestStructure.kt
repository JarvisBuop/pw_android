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

    @Test
    fun testSortTree1() {
        var linkedList = LinkedList<Int?>(listOf(
                1, 2, 4, null, null, 5, null, null, 3, null, 6
        ))

        System.out.println("origin data: " + linkedList.joinToString())
        var accessSortTree = AccessSortTree()
        var createBTree1 = accessSortTree.createBTree1(linkedList)

        accessSortTree.preOrderTraversal(createBTree1)
        System.out.println("")
        accessSortTree.inOrderTraversal(createBTree1)
        System.out.println("")
        accessSortTree.postOrderTraversal(createBTree1)
        System.out.println("")

        accessSortTree.preOrderTraversalWithStack(createBTree1)
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