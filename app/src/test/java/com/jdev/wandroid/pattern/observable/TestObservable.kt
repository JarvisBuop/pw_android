package com.jdev.wandroid.pattern.observable

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 *
 * 测试观察者模式;
 */
class TestObservable {

    @Test
    fun testObservable() {
        var subject = Subject()

        var observer1 = ConcreateObserver(" observer-1 ")
        var observer2 = ConcreateObserver(" observer-2 ")
        var observer3 = ConcreateObserver(" observer-3 ")

        subject.addObserver(observer1)
        subject.addObserver(observer2)
        subject.addObserver(observer3)

        subject.postNewMessage(" new book is arrived!! ")
    }
}