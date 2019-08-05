package module

import android.content.Context
import com.blankj.utilcode.util.Utils
import com.danikula.videocache.HttpProxyCacheServer
import com.jarvisdong.kit.baseui.BaseApp

//import com.alibaba.android.arouter.launcher.ARouter

/**
 * 组件化application;
 */
class MainApplication : BaseApp() {

    private var proxy: HttpProxyCacheServer? = null

    fun getProxy(context: Context): HttpProxyCacheServer {
        val app = context.applicationContext as MainApplication
        if (app.proxy == null)
            app.proxy = app.newProxy()
        else
            app.proxy
        return app.proxy!!
    }

    private fun newProxy(): HttpProxyCacheServer {
        return HttpProxyCacheServer(this)
    }

    override fun onCreate() {
        super.onCreate()

        Utils.init(this)
        //ARouter配置
        //        if (Utils.isDebug()) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
        //            ARouter.openLog();     // 打印日志
        //            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        //        }
        //        ARouter.init(this); // 尽可能早，推荐在Application中初始化

    }
}
