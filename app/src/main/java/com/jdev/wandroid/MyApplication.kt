package com.jdev.wandroid

import android.app.Application

import com.jarvisdong.kit.baseui.BaseApp
import android.graphics.drawable.Drawable

import com.bumptech.glide.Glide
import com.bumptech.glide.load.ResourceDecoder
import com.jdev.wandroid.webp.WebpBytebufferDecoder
import com.jdev.wandroid.webp.WebpDrawable
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
