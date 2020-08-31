package com.jdev.wandroid.pattern.state

/**
 * Created by JarvisDong on 2018/12/16.
 * @Description:
 * @see:
 *
 * 再开机状态下,相应方法的实现;
 */
class PowerOnState : State {
    override fun nextChannel() {
        System.out.println("下一频道")
    }

    override fun preChannel() {
        System.out.println("上一频道")
    }

    override fun turnUp() {
        System.out.println("调到音量")
    }

    override fun turnDown() {
        System.out.println("调低音量")
    }

}