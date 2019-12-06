package com.jdev.wandroid

import android.graphics.drawable.Drawable
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.ResourceDecoder
import com.jarvisdong.kit.baseui.BaseApp
import com.jdev.kit.support.webp.WebpBytebufferDecoder
import com.jdev.kit.support.webp.WebpResourceDecoder
import java.io.InputStream
import java.nio.ByteBuffer


/**
 * 壳application
 */
class MyApplication : BaseApp(){
    override fun onCreate() {
        super.onCreate()

        initGlideWebp()
    }
}
