package com.example.module_filter.utils

import android.app.Activity
import android.os.Handler
import android.os.Message
import java.lang.Exception
import java.lang.ref.WeakReference

/**
 * info: create by jd in 2019/7/30
 * @see:
 * @description:
 *
 */
class KtHandlerSingleton {

    var handler: MyHandler? = null

    class INSTANCE {
        companion object {
            val handlerInstance = KtHandlerSingleton()
        }
    }

    companion object {
        fun getInstance(): KtHandlerSingleton {
            return INSTANCE.handlerInstance
        }
    }

    fun setCallBackByHandler(time: Long = 0, mActivty: Activity, func: (msg: Message?) -> Unit) {
        if (handler == null) {
            handler = MyHandler(WeakReference(mActivty))
        } else {
            handler?.mActivty = WeakReference(mActivty)
        }
        var obtain = Message.obtain()
        obtain.obj = func
        handler?.sendMessageDelayed(obtain, time)
    }

    class MyHandler(var mActivty: WeakReference<Activity>) : Handler() {

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            var act = mActivty.get()
            if (act != null && !act.isFinishing && msg != null) {
                try {
                    (msg.obj as (msg: Message?) -> Unit).invoke(msg)
                } catch (e: Exception) {

                }
            }
        }
    }
}