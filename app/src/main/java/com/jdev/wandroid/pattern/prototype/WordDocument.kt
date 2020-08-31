package com.jdev.wandroid.pattern.prototype

/**
 * Created by JarvisDong on 2018/12/13.
 * OverView:
 *
 *设计模式3--原型模式
 *
 * [保护性拷贝概念]
 * [clone不执行构造函数]
 *
 * 具体原型  && 抽象原型 cloneable
 */
class WordDocument : Cloneable {
    lateinit var mText: String
    lateinit var mImages: ArrayList<String>


    init {
        System.out.println("------------ WordDocument 主构造函数执行 -------------")
    }

    public override fun clone(): Any {
        try {
            var clone = super.clone() as WordDocument
            mText = clone.mText
            mImages = mImages.clone() as ArrayList<String>
            return clone
        } catch (e: Exception) {
        }
        return super.clone()
    }

    fun showDocument(){
        System.out.println("-------- word content start ----------")

        System.out.println("---Content: "+mText +" mImages: "+mImages.toString())

        System.out.println("-------- word content end ----------")
    }
}