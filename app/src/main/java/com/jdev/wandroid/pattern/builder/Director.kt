package com.jdev.wandroid.pattern.builder

/**
 * Created by JarvisDong on 2018/12/13.
 * OverView:
 *
 * 统一组装过程类,负责构造产品
 *
 * >>一般的构造过程可忽略此类,与具体的Builder合并成一个即可;
 */
class Director public constructor(val builder: Builder) {

    fun construct(board: String) {
        builder.buildBoard(board)
        builder.buildOs()
    }
}