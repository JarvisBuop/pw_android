package com.jdev.module_welcome.ui.act

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jdev.module_welcome.R

/**
 * info: create by jd in 2019/7/3
 * @see:
 * @description: 首页;
 *
 * 搜索框采用 隐藏fakeview 浮顶实现;
 *
 *
 * tab 采用 处理滑动事件处理;
 *
 *
 * --------------------
 */
class MainPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_container)

    }

}