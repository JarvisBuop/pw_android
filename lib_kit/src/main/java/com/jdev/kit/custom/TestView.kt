package com.jdev.kit.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Created by JarvisDong on 2019/4/7.
 *
 * @Description:
 * @see:
 */
class TestView(context:Context, attrs: AttributeSet, defstyleAttr:Int) : View(context,attrs,defstyleAttr) {


    init {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }


}


