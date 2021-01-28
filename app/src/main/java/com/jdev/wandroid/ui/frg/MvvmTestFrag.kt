package com.jdev.wandroid.ui.frg

import androidx.lifecycle.Observer
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.kit.baseui.ViewModelHolder
import com.jdev.wandroid.R
import com.jdev.wandroid.databinding.AppFragMvvmTestBinding
import com.jdev.wandroid.mvvm.TestViewModel

/**
 * info: create by jd in 2020/4/1
 * @see:
 * @description:
 *
 */
class MvvmTestFrag : BaseViewStubFragment() {
    companion object {
        var TASKS_VIEWMODEL_TAG = "TASKS_VIEWMODEL_TAG"

        fun newInstance(): MvvmTestFrag {
            var mvvmTestFrag = MvvmTestFrag()
            return mvvmTestFrag
        }
    }

    var mTestVM: TestViewModel? = null
    lateinit var mvvmTestBinding: AppFragMvvmTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDefaultView()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mvvmTestBinding = AppFragMvvmTestBinding.inflate(inflater, container, false)
        mvvmTestBinding.view = this
        mvvmTestBinding.viewmodel = mTestVM
        return mvvmTestBinding.root
    }

    override fun initDefaultView() {
        mTestVM = ViewModelHolder.bindVMToActivity<TestViewModel>(activity!!.supportFragmentManager,
                TASKS_VIEWMODEL_TAG) {
            TestViewModel(this)
        }
    }

    override fun onResume() {
        super.onResume()
        mTestVM?.start()
    }

    override fun customOperate(savedInstanceState: Bundle?) {
        setUpList()

        mTestVM?.loadDatas()
    }

    private fun setUpList() {
        var list = mvvmTestBinding.list
        list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(mContext)
        list.adapter = mTestVM?.mAdapter

        mTestVM?.mutableLiveData?.observe(this, object : Observer<ArrayList<String>> {
            override fun onChanged(t: ArrayList<String>?) {

            }
        })
    }

}