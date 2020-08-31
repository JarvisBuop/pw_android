package com.jdev.wandroid.pattern.composite

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * 测试组合模式;
 */

class TestComponent {

    /**
     * 安全组合模式;
     */
    @Test
    fun testComponent() {
        var leaf1 = Leaf("leaf1")
        var leaf2 = Leaf("leaf2")
        var branch1 = Composite("branch1").apply {
            addChild(leaf1)
            addChild(leaf2)
        }
        var branch2 = Composite("branch2").apply {
            addChild(leaf1)
            addChild(leaf2)
        }
        var root = Composite("root").apply {
            addChild(branch1)
            addChild(branch2)
        }

        root.doSomething()
    }

    /**
     * 透名组合模式;
     */
    @Test
    fun testComponent2() {
        var leaf1 = TransparentLeaf("leaf1")
        var leaf2 = TransparentLeaf("leaf2")
        var branch1 = TransparentComposite("branch1").apply {
            addChild(leaf1)
            addChild(leaf2)
        }
        var branch2 = TransparentComposite("branch2").apply {
            addChild(leaf1)
            addChild(leaf2)
        }
        var root = TransparentComposite("root").apply {
            addChild(branch1)
            addChild(branch2)
        }

        root.doSomething()
    }
}
