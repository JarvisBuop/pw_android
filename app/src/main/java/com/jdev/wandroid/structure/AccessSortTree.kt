package com.jdev.wandroid.structure

import java.util.*

/**
 * info: create by jd in 2020/9/17
 * @see:
 * @description: 二叉树 递归与非递归
 *
 */

data class TreeNode(
        var data: Int,
        var left: TreeNode? = null,
        var right: TreeNode? = null
)

class AccessSortTree {
    fun createBTree1(inputList: LinkedList<Int?>): TreeNode? {
        if (inputList.size == 0) {
            return null
        }

        var removeFirst = inputList.removeFirst()
        var treeNode: TreeNode? = null
        if (removeFirst != null) {
            treeNode = TreeNode(removeFirst)
            treeNode.left = createBTree1(inputList)
            treeNode.right = createBTree1(inputList)
        }
        return treeNode
    }

    /**
     * 前序遍历
     */
    fun preOrderTraversal(node: TreeNode?) {
        if (node == null) return
        System.out.print("-${node.data}")
        preOrderTraversal(node.left)
        preOrderTraversal(node.right)
    }

    /**
     * 后序遍历
     */
    fun postOrderTraversal(node: TreeNode?) {
        if (node == null) return
        postOrderTraversal(node.left)
        postOrderTraversal(node.right)
        System.out.print("-${node.data}")
    }

    /**
     * 中序遍历
     */
    fun inOrderTraversal(node: TreeNode?) {
        if (node == null) return
        inOrderTraversal(node.left)
        System.out.print("-${node.data}")
        inOrderTraversal(node.right)
    }

    /**
     * 非递归 前序遍历方案
     */
    fun preOrderTraversalWithStack(node: TreeNode?) {
        var stack = Stack<TreeNode>()
        var treeNode = node

        while (treeNode != null || !stack.isEmpty()) {
            while (treeNode != null) {
                System.out.print("-${treeNode.data}")
                stack.push(treeNode)
                treeNode = treeNode.left
            }

            if (!stack.isEmpty()) {
                treeNode = stack.pop()
                treeNode = treeNode.right
            }
        }
    }

    /**
     * 广度优先遍历
     */
    fun levelOrderTraversal(node: TreeNode?) {
        var queue = LinkedList<TreeNode>()
        var treeNode = node

        while (treeNode != null || !queue.isEmpty()) {
            while (treeNode != null) {
                queue.push(treeNode)


            }
        }
    }
}