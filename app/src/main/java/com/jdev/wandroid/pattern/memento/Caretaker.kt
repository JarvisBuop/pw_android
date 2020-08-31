package com.jdev.wandroid.pattern.memento

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * [存储备忘录类]
 *
 * >>负责管理备忘录类,只能传递,不能更改;
 */
const val MAXSIZE = 30

class Caretaker {
    var index: Int = 0
    var memotos = arrayListOf<Memoto>()

    fun saveMemoto(memento: Memoto) {
        if (memotos.size > MAXSIZE) {
            memotos.removeAt(0)
        }

        memotos.add(memento)

        index = memotos.size - 1
    }

    fun getPreMemoto(): Memoto {
        index = if (index > 0) --index else index
        return memotos.get(index)
    }

    fun getNextMemoto(): Memoto {
        index = if (index < memotos.size - 1) ++index else index
        return memotos.get(index)
    }
}