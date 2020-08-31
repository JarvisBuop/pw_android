package com.jdev.kit.baseui

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.TextView
import com.blankj.utilcode.util.ConvertUtils
import com.jdev.kit.R
import java.lang.Exception

/**
 * Created by JarvisDong on 2018/12/6.
 * OverView:
 */
abstract class BaseActivity : AppCompatActivity() {
    private val isDebug = true
    protected lateinit var mContext: Context
    protected val TAG: String = this::class.java.name

    //init default view;
    protected var toolbar: android.support.v7.widget.Toolbar? = null
    protected var appbarLayout: View? = null
    protected var barLeftView: TextView? = null
    protected var barTitleView: TextView? = null
    protected var barRightView: TextView? = null
    protected var fabView: FloatingActionButton? = null

    protected var textMarkTipView: TextView? = null

    private var isFirstFocus = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component_layout_coor_design)
        mContext = this
        var viewStubId = getViewStubId()
        if (viewStubId != 0) {
            var stubView = findViewById<ViewStub>(R.id.content_layout)
            stubView.layoutResource = viewStubId
            stubView.inflate()

            initDefaultView()
        }

        addTextMarkTips()

        if (initIntentData()) {
            customOperate(savedInstanceState)
        } else {
            noDataOperate()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            addTextMarkTips()
        }
    }

    private fun addTextMarkTips() {
        if (isFirstFocus) {
            isFirstFocus = false
            try {
                val decorView = window.decorView
                if (isDebug && decorView is ViewGroup) {
                    val container = decorView.findViewById<ViewGroup>(android.R.id.content)
                    textMarkTipView = TextView(mContext)
                    textMarkTipView?.setPadding(ConvertUtils.dp2px(15f), ConvertUtils.dp2px(15f), 0, 0)
                    textMarkTipView?.setTextColor(mContext.resources.getColor(R.color.red))
                    val sb = StringBuilder()
                    sb.append("\ninfo: ${javaClass.simpleName}")
                    textMarkTipView?.text = sb.toString()
                    container.addView(textMarkTipView)
                }
            } catch (e: Exception) {

            }
        }
    }

    protected fun setTextMarkTips(content: String) {
        val sb = StringBuilder()
        sb.append("\ninfo: ${javaClass.simpleName}")
                .append(" - $content")
        textMarkTipView?.text = sb.toString()
    }

    private fun initDefaultView() {
        toolbar = findViewById(R.id.toolbar)
        appbarLayout = findViewById(R.id.app_bar_design)
        barLeftView = findViewById(R.id.bar_left)
        barTitleView = findViewById(R.id.bar_title)
        barRightView = findViewById(R.id.bar_right)
        fabView = findViewById(R.id.fab)

        if (isDebug) {
            barTitleView?.text = this.javaClass.name
        }
    }

    fun getRootView(): View {
        return window.decorView.findViewById(android.R.id.content)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onRestart() {
        super.onRestart()
    }

    /**
     * open func
     */
    open fun noDataOperate() {
        finish()
    }

    /**
     * abstract class;
     */
    protected abstract fun getViewStubId(): Int

    protected abstract fun initIntentData(): Boolean

    protected abstract fun customOperate(savedInstanceState: Bundle?)

}