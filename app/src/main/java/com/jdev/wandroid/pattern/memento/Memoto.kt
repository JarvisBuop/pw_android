package com.jdev.wandroid.pattern.memento

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * 设计模式12--备忘录模式(快照模式)
 *
 * [备忘录类]
 *
 * >>用于保存数据类,存储Originator的内部状态,防止其他无关类访问;
 */
class Memoto {
    var text: String? = null

    var cursor: Int = 0
}