package com.jdev.wandroid.pattern.prototype

import org.junit.Test

/**
 * Created by JarvisDong on 2018/12/13.
 * OverView:
 *
 * 测试原型模式
 */
class TestProtoType {

    @Test
    fun testProtoType() {
        var wd: WordDocument = WordDocument()
        wd.mText = "this is a document"
        wd.mImages = arrayListOf("image1", "image2", "image3")
        wd.showDocument()

        var wdClone = wd.clone() as WordDocument
        wdClone.showDocument()
        wdClone.mText = "this is a shit"
        wdClone.showDocument()
        /**
         * 修改clone的文档,不会对源文档有改变
         */
        wd.showDocument()
    }
}