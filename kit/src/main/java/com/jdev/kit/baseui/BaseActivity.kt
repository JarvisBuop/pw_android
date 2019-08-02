package com.jarvisdong.kotlindemo.ui

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewStub
import android.widget.TextView
import com.jdev.kit.R

/**
 * Created by JarvisDong on 2018/12/6.
 * OverView:
 */
abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var mContext: Context
    private val isDebug = true
    protected val TAG :String = this::class.java.name

    //init default view;
    protected var toolbar: android.support.v7.widget.Toolbar? = null
    protected var appbarLayout: View? = null
    protected var barLeftView: TextView? = null
    protected var barTitleView: TextView? = null
    protected var barRightView: TextView? = null
    protected var fabView: FloatingActionButton? = null

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

        if (initIntentData()) {
            customOperate(savedInstanceState)
        }
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
     * abstract class;
     */
    protected abstract fun getViewStubId(): Int

    protected abstract fun initIntentData(): Boolean

    protected abstract fun customOperate(savedInstanceState: Bundle?)

}