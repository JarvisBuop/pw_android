package com.jdev.wandroid.ui.act

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.snackbar.Snackbar
import com.jdev.kit.baseui.BaseActivity
import com.jdev.wandroid.R
import com.jdev.wandroid.mockdata.ItemVo
import com.jdev.wandroid.utils.OpenUtils
import com.jdev.wandroid.utils.ParseUtils
import com.jdev.wandroid.utils.ViewUtils
import kotlinx.android.synthetic.main.app_activity_main.*
import kotlinx.android.synthetic.main.app_include_main_top.*
import kotlin.properties.ReadWriteProperty
import kotlin.random.Random
import kotlin.reflect.KProperty


/**
 * home page
 */
class MainActivity : BaseActivity() {
    var mSecretCodes: List<ItemVo>? by CodeDelegate()
    lateinit var mAdapterTop: MyAdapter<ItemVo>
    lateinit var mAdapterBottom: MyAdapter<ItemVo>

    override fun getViewStubId(): Int {
        return R.layout.app_activity_main
    }

    override fun initIntentData(): Boolean {
        setSupportActionBar(toolbar)
        return true
    }

    override fun customOperate(savedInstanceState: Bundle?) {
        initToolBar()
        initTopView()
        initFootView()
        initRecyclerViews()

        fetchDatas()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.app_menu_main, menu)
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

    private fun initToolBar() {
        fabView?.show()
        fabView?.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        //sample_text.text = stringFromJNI()
    }


    private fun initTopView() {
        edt_input.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                searchItem(edt_input.text.toString())
            }
            return@setOnEditorActionListener true
        }
        img_go.setOnClickListener {
            searchItem(edt_input.text.toString())
        }
    }

    private fun initFootView() {
        txt_foot.setOnClickListener {
            val codeDatas = mSecretCodes
            if (codeDatas?.isEmpty() != false) return@setOnClickListener
            AlertDialog.Builder(mContext)
                    .setAdapter(object : BaseAdapter() {
                        @SuppressLint("ViewHolder")
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                            val view = LayoutInflater.from(mContext).inflate(R.layout.app_item_centerdrag, parent, false)
                            val txtTips = view.findViewById<TextView>(R.id.scroll_tip)
                            val itemVo = codeDatas.get(position)
                            txtTips.text = itemVo.title ?: ""
                            view.setOnClickListener {
                                OpenUtils.open(mContext, itemVo)
                            }
                            return view
                        }

                        override fun getItem(position: Int): Any {
                            return codeDatas[position]
                        }

                        override fun getItemId(position: Int): Long {
                            return position.toLong()
                        }

                        override fun getCount(): Int {
                            return codeDatas.size
                        }

                    }) { dialog, which ->

                    }.setIcon(R.drawable.icon1)
                    .setTitle("secret code table")
                    .show()
        }
    }

    private fun initRecyclerViews() {
        main_container.setDisableDoubleScroll(false)
        first_recyclerview.apply {
            layoutManager = LinearLayoutManager(mContext)
            mAdapterTop = MyAdapter<ItemVo>(R.layout.app_item_normalitem).apply {
                openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT)
                isFirstOnly(false)
                setOnItemClickListener { adapter, view, position ->
                    OpenUtils.open(mContext, mAdapterTop.getItem(position))
                }
            }
            adapter = mAdapterTop
        }
        second_recyclerview.apply {
            layoutManager = LinearLayoutManager(mContext)
            mAdapterBottom = MyAdapter<ItemVo>(R.layout.app_item_normalitem_reverse).apply {
                openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT)
                isFirstOnly(false)
                setOnItemClickListener { adapter, view, position ->
                    OpenUtils.open(mContext, mAdapterTop.getItem(position))
                }
            }
            adapter = mAdapterBottom
        }
    }

    private fun searchItem(codeStr: String) {
        if (!StringUtils.isEmpty(codeStr)) {
            try {
                val codes = mSecretCodes
                val random = Random.nextInt(codes!!.size)
                OpenUtils.open(mContext, mSecretCodes!![random])
            } catch (e: Exception) {
            }
        }
    }

    private fun fetchDatas() {
        ParseUtils.getMainConfigList<ItemVo>("topList")?.apply {
            mAdapterTop.setNewData(this)
        }
        ParseUtils.getMainConfigList<ItemVo>("bottomList").apply {
            mAdapterBottom.setNewData(this)
        }
    }

    //----------------EXTRA CLASS------------------
    class MyAdapter<T>(layoutId: Int, mDataList: List<T>? = null) : BaseQuickAdapter<T, BaseViewHolder>(layoutId, mDataList) {
        override fun convert(helper: BaseViewHolder?, item: T?) {
            ViewUtils.bind(helper, item)
        }
    }

    //lazy load
    class CodeDelegate() : ReadWriteProperty<Context, List<ItemVo>?> {
        override fun getValue(thisRef: Context, property: KProperty<*>): List<ItemVo>? {
            return ParseUtils.getMainConfigList("secretList")
        }

        override fun setValue(thisRef: Context, property: KProperty<*>, value: List<ItemVo>?) {}
    }
}
