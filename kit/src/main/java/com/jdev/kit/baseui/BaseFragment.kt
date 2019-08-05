package com.jdev.kit.baseui

import android.content.Context
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import com.jdev.kit.R

/**
 * info: create by jd in 2019/8/5
 * @see:
 * @description:
 *
 */
abstract class BaseFragment : Fragment() {
    protected var mContext: Context? = null
    private val isDebug = true
    protected val TAG: String = this::class.java.name

    protected lateinit var mRootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.component_layout_coor_design_frag, container, false)
        var viewStubId = getViewStubId()
        if (viewStubId != 0) {
            var stubView = findView(R.id.content_layout) as ViewStub
            stubView.layoutResource = viewStubId
            stubView.inflate()
            initDefaultView()
        }

        if (initIntentData()) {
            customOperate(savedInstanceState)
        }
        return  mRootView
    }

    protected fun initDefaultView() {

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
    protected abstract fun getViewStubId(): Int

    protected abstract fun initIntentData(): Boolean

    protected abstract fun customOperate(savedInstanceState: Bundle?)
}