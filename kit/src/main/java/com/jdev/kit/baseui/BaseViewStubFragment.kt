package com.jdev.kit.baseui

import android.view.ViewStub
import com.jdev.kit.R

/**
 * info: create by jd in 2019/8/5
 * @see:
 * @description:
 *
 * kotlin的扩展在此类用不了,
 * 可能因为事先设置好了layout,
 * 导致不能从想要的layout中获取id;
 *
 */
abstract class BaseViewStubFragment : BaseFragment() {

    override fun initDefaultView() {
        var viewStubId = getViewStubId()
        if (viewStubId != 0) {
            var stubView = findView(R.id.content_layout) as ViewStub
            stubView.layoutResource = viewStubId
            stubView.inflate()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.component_layout_coor_design_frag
    }

    /**
     * abstract class;
     */
    abstract fun getViewStubId(): Int

}