package com.jdev.wandroid

import android.graphics.drawable.Drawable
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.ResourceDecoder
import com.jarvisdong.kit.baseui.BaseApp
import com.jdev.wandroid.webp.WebpBytebufferDecoder
import com.jdev.wandroid.webp.WebpResourceDecoder
import java.io.InputStream
import java.nio.ByteBuffer


/**
 * 壳application
 */
class MyApplication : BaseApp(){
    override fun onCreate() {
        super.onCreate()

        initGlide()
        Utils.init(this)
    }

    private fun initGlide() {
        // webp support
        val decoder = WebpResourceDecoder(this)
        val byteDecoder = WebpBytebufferDecoder(this)

        // use prepend() avoid intercept by default decoder
        Glide.get(this).registry
                .prepend<InputStream, Drawable>(InputStream::class.java!!, Drawable::class.java,
                        decoder as ResourceDecoder<InputStream, Drawable>)
                .prepend<ByteBuffer, Drawable>(ByteBuffer::class.java!!, Drawable::class.java,
                        byteDecoder as ResourceDecoder<ByteBuffer, Drawable>)
    }
}
