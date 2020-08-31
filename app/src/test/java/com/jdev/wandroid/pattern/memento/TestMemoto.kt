package com.jdev.wandroid.pattern.memento

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/25.
 * OverView:
 */
class TestMemoto {

    @Test
    fun testMemoto() {
        var originator = Originator()
        System.out.println("init "+originator.content +" "+ originator.index)

        originator.otherChange()
        System.out.println("current "+originator.content +" "+ originator.index)

        var memoto = originator.createMemoto()
        var caretaker = Caretaker()
        caretaker.saveMemoto(memoto)
        System.out.println("memoto save"+memoto.text +" "+ memoto.cursor)

        System.out.println("quit !")

        originator = Originator()
        originator.restoreMemoto(caretaker.getNextMemoto())
        System.out.println("current "+originator.content +" "+ originator.index)
    }


}