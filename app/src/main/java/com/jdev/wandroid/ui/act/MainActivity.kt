package com.jdev.wandroid.ui.act

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.jarvisdong.kotlindemo.ui.BaseActivity
import com.jdev.wandroid.R
import com.jdev.wandroid.R.id.*
import com.jdev.wandroid.mockdata.MockData
import com.jdev.wandroid.noviceAnim.KtVersionMainPop
import com.jdev.wandroid.utils.ViewUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {
    lateinit var arr:Array<String>
    var index:Int = 0

    override fun getViewStubId(): Int {
        return R.layout.activity_main
    }

    override fun initIntentData(): Boolean {
        setSupportActionBar(toolbar)
        return true
    }

    override fun customOperate(savedInstanceState: Bundle?) {
        fabView?.visibility = View.VISIBLE
        fabView?.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

//        sample_text.text = stringFromJNI()

        quickContactBadge.assignContactFromPhone("13817228124", false)
        txt_feed.setOnClickListener {
            startActivity(Intent(this, FeedTestAct::class.java))
        }

        initWebp2()

        btn_test.setOnClickListener{
            initPop()
        }
    }

    private fun initPop() {

        var pop = KtVersionMainPop(mContext, ViewUtils.OnCallback<Any> {

        })

        pop.showAtLocation(getRootView(),Gravity.CENTER,0,0)
    }


    private fun initWebp2() {
        setWebpInImage("file:///android_asset/small.webp")
        arr = MockData.ALPHA_WEBP

        img_btn.setOnClickListener {
            arr = MockData.ANIM_WEBP
            index = 0
            setWebpInImage(arr.get(index % arr.size))
        }

        img_webp.setOnClickListener {
            setWebpInImage(arr.get(index % arr.size))
            index++
        }

    }

    fun setWebpInImage(webpUrl: String): RequestOptions {
        val options = RequestOptions()
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        Glide.with(this)
                .load(webpUrl)
                .apply(options).transition(DrawableTransitionOptions().crossFade(200))
                .into(img_webp)
        return options
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

//    external fun stringFromJNI(): String
//
//    companion object {
//
//        // Used to load the 'native-lib' library on application startup.
//        init {
//            System.loadLibrary("native-lib")
//        }
//    }
}
