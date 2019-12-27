package com.jdev.kit.baseui

import android.content.Context
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.ToastUtils

/**
 * info: create by jd in 2019/8/5
 * @see:
 * @description:
 *
 */
abstract class BaseFragment : Fragment() {
    private val isDebug = true
    protected var mContext: Context? = null
    protected val TAG: String = this::class.java.name

    protected lateinit var mRootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var layoutId = getLayoutId()
        if (layoutId != 0) {
            mRootView = inflater.inflate(layoutId, container, false)
        }

        initDefaultView()
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (initIntentData()) {
            customOperate(savedInstanceState)
        } else {
            noDataOperate()
        }
    }

    /**
     * open func
     */
    open fun noDataOperate() {
        if (isDebug) {
            ToastUtils.showShort("data initial error in $TAG")
        }
    }

    open fun initDefaultView() {

    }

    protected fun <T : View?> findView(@IdRes id: Int): T {
        return mRootView.findViewById<T>(id)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
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

    override fun onDetach() {
        super.onDetach()
    }

    /**
     * abstract class;
     */
    protected abstract fun getLayoutId(): Int

    protected open fun initIntentData(): Boolean = true

    protected abstract fun customOperate(savedInstanceState: Bundle?)
}