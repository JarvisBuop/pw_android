package com.jdev.wandroid.structure

import java.lang.IndexOutOfBoundsException
import java.lang.StringBuilder

/**
 * info: create by jd in 2020/9/11
 * @see:
 * @description: 链表
 *
 */
data class Node(
        var data: Int,
        var next: Node? = null
)

class AccessLinkList {
    private var head: Node? = null
    private var last: Node? = null
    private var size = 0

    fun insert(index: Int, data: Int) {
        if (index < 0 || index > size) throw IndexOutOfBoundsException("超出范围")

        System.out.println("\n插入数据开始 index:$index originList: ${toString()}")
        var node = Node(data)
        if (size == 0) {
            head = node
            last = node
        } else if (index == 0) {
            node.next = head
            head = node
        } else if (size == index) {
            last?.next = node
            last = node
        } else {
            var forwardNode = get(index - 1)
            node.next = forwardNode?.next
            forwardNode?.next = node
        }

        size++
        System.out.println("\n插入数据结束 插入node:$node resultList: ${toString()}")
    }

    //获取当前位置的node
    fun get(index: Int): Node? {
        if (index < 0 || index > size) throw IndexOutOfBoundsException("超出范围")

        var currentNode: Node? = head
        for (i in 0 until index) {
            currentNode = head?.next
        }
        return currentNode
    }

    fun delete(index: Int): Node? {
        if (index < 0 || index > size) throw IndexOutOfBoundsException("超出范围")

        System.out.println("\n删除数据开始 result: ${toString()}")
        var removeNode: Node? = null
        if (index == size - 1) {
            var lastNode = get(index - 1)
            removeNode = lastNode?.next
            lastNode?.next = null
            last = lastNode
        } else if (index == 0) {
            removeNode = head
            head = head?.next
        } else {
            var upNode = get(index - 1)
            var downNode = upNode?.next?.next
            removeNode = upNode?.next
            upNode?.next = downNode
        }

        System.out.println("\n删除数据结束 $removeNode result: ${toString()}")

        size--
        return removeNode
    }

    override fun toString(): String {
        var next = head
        var sb = StringBuilder()
        do {
            sb.append("${next?.data}")
            next = next?.next
            if (next != null) {
                sb.append("->")
            }
        } while (next != null)

        return "linkList: ${sb.toString()} size=$size"
    }


}