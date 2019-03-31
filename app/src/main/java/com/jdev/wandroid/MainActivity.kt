package com.jdev.wandroid

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.jarvisdong.kotlindemo.ui.BaseActivity
import kotlinx.android.synthetic.main.fragment_main.*

class MainActivity : BaseActivity() {
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

        sample_text.text = stringFromJNI()
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

    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
