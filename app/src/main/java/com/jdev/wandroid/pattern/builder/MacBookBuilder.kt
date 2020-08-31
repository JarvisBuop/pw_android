package com.jdev.wandroid.pattern.builder

/**
 * Created by JarvisDong on 2018/12/13.
 * OverView:
 *
 * 具体构造者类--macbook的构造者
 */
class MacBookBuilder : Builder() {
    private var computer: Computer = MacBook()

    override fun create(): Computer {
        return computer
    }

    override fun buildBoard(board: String) {
        computer.setBoard(board)
    }

    override fun buildOs() {
        computer.setOS()
    }

}