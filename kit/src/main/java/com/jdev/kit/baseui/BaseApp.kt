package com.jarvisdong.kit.baseui

import android.app.Activity
import android.app.Application
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.multidex.MultiDexApplication
import com.blankj.utilcode.util.LogUtils
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
open class BaseApp : MultiDexApplication() {

    companion object {
        public var instance: Application? = null

        fun getApp(): Application {
            return instance ?: Utils.getApp()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        Runnable {
            postInit()
        }.run()
    }

    private fun postInit() {
        Utils.init(this)
        //监听act
        registerActivityLifecycleCallbacks(ActivityLifecycleCallbacksImpl())
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

    /**
     * 监听所有act的生命周期,判断是否处于后台,回到前台时需要更新用户信息;
     */
    private inner class ActivityLifecycleCallbacksImpl : ActivityLifecycleCallbacks {
        //判断在前台的act数量;
        private var actCount = 0

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            if (activity != null) {
                LogUtils.e("\n Act-lifecle: " + activity.componentName + " onActivityCreated; cur in " + Thread.currentThread().name)
            }
        }

        override fun onActivityStarted(activity: Activity) {
            actCount++
        }

        override fun onActivityResumed(activity: Activity) {}

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {
            actCount--
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

        }

        override fun onActivityDestroyed(activity: Activity?) {
            if (activity != null) {
                LogUtils.e("\n Act-lifecle: " + activity.componentName + " onActivityDestroyed; cur in " + Thread.currentThread().name)
            }
        }
    }
}