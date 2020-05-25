package com.jdev.kit.baseui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

/**
 * info: create by jd in 2020/4/1
 * @see:
 * @description: Non-UI Fragment used to retain ViewModels.
 *
 */
class ViewModelHolder<VM> : Fragment() {

    var mViewModel: VM? = null

    companion object {

        //使用指定vm生成无uifragment用于绑定至生命周期;
        fun <M> createContainer(viewModel: M): ViewModelHolder<M> {
            var viewModelHolder = ViewModelHolder<M>()
            viewModelHolder.mViewModel = viewModel
            return viewModelHolder
        }

        //绑定指定tag至fm,如果没有绑定则新建一个新的无ui fragment(内部有vm)加入fm监听生命周期;
        fun <VM> bindVMToActivity(fragmentManager: FragmentManager, tag: String, createAction: (() -> VM)? = null): VM? {
            var retainedVmHolder = fragmentManager.findFragmentByTag(tag) as ViewModelHolder<VM>?
            if (retainedVmHolder?.mViewModel != null) {
                return retainedVmHolder?.mViewModel
            } else {
                var newVm = createAction?.invoke()
                if (newVm != null) {
                    fragmentManager.beginTransaction()
                            .add(createContainer(newVm), tag)
                            .commit()
                }
                return newVm
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }
}