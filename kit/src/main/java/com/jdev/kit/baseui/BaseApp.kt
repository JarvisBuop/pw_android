package com.jarvisdong.kit.baseui

import android.app.Application
import android.graphics.drawable.Drawable
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.ResourceDecoder
import com.jdev.kit.support.webp.WebpBytebufferDecoder
import com.jdev.kit.support.webp.WebpResourceDecoder
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * Created by JarvisDong on 2018/12/5.
 * OverView:
 */
open class BaseApp : Application() {

    companion object {
        public var instance: Application? = null

        fun getApp(): Application {
            return instance ?: Utils.getApp()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Utils.init(this)
    }

    protected fun initGlideWebp() {
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