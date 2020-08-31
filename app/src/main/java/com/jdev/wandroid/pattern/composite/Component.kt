package com.jdev.wandroid.pattern.composite

/**
 * Created by JarvisDong on 2018/12/26.
 * OverView:
 *
 * 设计模式18--组合模式
 *
 * 表示对象的部分-整体层次结构;
 *
 * 分类为`透明组合模式`和`安全的组合模式`,
 * 区别在于透明组合模式不管是`叶子节点`和是`枝干节点`都有着相同的结构;
 *
 * [抽象根节点类]
 *
 * >>安全组合模式,使用了实现类违背依赖倒置原则;
 */
abstract class Component public constructor(var name: String) {

    abstract fun doSomething()
}