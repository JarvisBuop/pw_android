package com.jdev.wandroid.pattern.memento

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * [备忘录创建者]
 *
 * >>起源,记录和恢复内部状态,存储字段;
 */
class Originator {
    var index: Int = 0
    var content: String? = null

    fun createMemoto(): Memoto {
        var memoto = Memoto()
        memoto.cursor = index
        memoto.text = "test" + index
        return memoto
    }

    fun restoreMemoto(memoto: Memoto) {
        content = memoto.text
        index = memoto.cursor
    }


    fun otherChange() {
        index += 2
        content = "process" + index
    }
}