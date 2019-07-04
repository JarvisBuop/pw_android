package com.jdev.module_welcome.ui.frag

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jdev.module_welcome.R

/**
 * Created by JarvisDong on 2019/07/03.
 * @Description:
 * @see:
 */
class KtChildBaseFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.item_page, container, false)
        return view
    }
}