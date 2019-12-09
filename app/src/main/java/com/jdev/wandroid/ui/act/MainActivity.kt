package com.jdev.wandroid.ui.act

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.util.SparseArray
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.BaseAdapter
import android.widget.TextView
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jarvisdong.kit.utils.ResourceIdUtils
import com.jarvisdong.kotlindemo.ui.BaseActivity
import com.jdev.wandroid.R
import kotlinx.android.synthetic.main.app_activity_main.*
import kotlinx.android.synthetic.main.app_include_main_top.*
import java.lang.Exception
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * home page
 */
class MainActivity : BaseActivity() {
    //top recyclerview datas
    var mTopDatas = arrayListOf<OrientVo>(
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc")
    )

    //bottom recyclerview datas
    var mBottomDatas = arrayListOf<OrientVo>(
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc")
    )

    //secretcode other datas (not important)
    var mSecretString = mapOf<Int, OrientVo>(
            Pair(ContainerActivity.KEY_GESTURE, OrientVo("gesture_test")),
            Pair(ContainerActivity.KEY_PHOTOVIEW, OrientVo("photoview_test")),
            Pair(ContainerActivity.KEY_SHADOW, OrientVo("shadow_test")),
            Pair(ContainerActivity.KEY_WEBP, OrientVo("webp_test"))
    )

    var mSecretCodes: SparseArray<OrientVo> by CodeDelegate(mSecretString)


    lateinit var mAdapterTop: MyAdapter<OrientVo>
    lateinit var mAdapterBottom: MyAdapter<OrientVo>

    override fun getViewStubId(): Int {
        return R.layout.app_activity_main
    }

    override fun initIntentData(): Boolean {
        setSupportActionBar(toolbar)
        return true
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

    override fun customOperate(savedInstanceState: Bundle?) {
        initToolBar()

        initRecyclerViews()
        initTopView()
        initFootView()
        fetchDatas()
    }

    private fun initToolBar() {
        fabView?.visibility = View.VISIBLE
        fabView?.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        //sample_text.text = stringFromJNI()
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
            var codeDatas = mSecretCodes
            if (codeDatas?.size() == 0) return@setOnClickListener
            AlertDialog.Builder(mContext)
                    .setAdapter(object : BaseAdapter() {
                        @SuppressLint("ViewHolder")
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                            var view = LayoutInflater.from(mContext).inflate(R.layout.app_item_centerdrag, parent, false)
                            var txtTips = view.findViewById<TextView>(R.id.scroll_tip)
                            var item = codeDatas.get(position)
                            txtTips.text = item.title
                            return view
                        }

                        override fun getItem(position: Int): Any {
                            return codeDatas[position]
                        }

                        override fun getItemId(position: Int): Long {
                            return position.toLong()
                        }

                        override fun getCount(): Int {
                            return codeDatas.size()
                        }

                    }) { dialog, which ->

                    }.setIcon(R.drawable.icon1)
                    .setTitle("secret code table")
                    .show()
        }
    }

    private fun initRecyclerViews() {
        main_container.setDisableDoubleScroll(false)

        mAdapterTop = MyAdapter(R.layout.app_item_normalitem)
        mAdapterBottom = MyAdapter(R.layout.app_item_normalitem_reverse)
        mAdapterTop.setOnItemClickListener { adapter, view, position ->
            clickItemByClazz(mAdapterTop.getItem(position), position)
        }

        mAdapterBottom.setOnItemClickListener { adapter, view, position ->
            clickItemByClazz(mAdapterBottom.getItem(position), position)
        }

        first_recyclerview.layoutManager = LinearLayoutManager(mContext)
        first_recyclerview.adapter = mAdapterTop
        second_recyclerview.layoutManager = LinearLayoutManager(mContext)
        second_recyclerview.adapter = mAdapterBottom
    }

    private fun clickItemByClazz(item: OrientVo?, position: Int = -1) {
        item?.also {
            if (it.clazz != null) {
                mContext.startActivity(Intent(mContext, it.clazz))
            }
        }
    }

    private fun clickItemByCode(indexOfKey: Int) {
        ContainerActivity.launch(mContext, indexOfKey)
    }

    private fun searchItem(codeStr: String) {
        if (!StringUtils.isEmpty(codeStr)) {
            try {
                var code = java.lang.Integer.parseInt(codeStr)
                var indexOfKey = mSecretCodes.indexOfKey(code)
                if (indexOfKey >= 0) {
                    clickItemByCode(indexOfKey)
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun fetchDatas() {
        mAdapterTop?.also {
            it.setNewData(mTopDatas)
        }

        mAdapterBottom.also {
            it.setNewData(mBottomDatas)
        }
    }

    class MyAdapter<T>(layoutId: Int, mDataList: List<T>? = null) : BaseQuickAdapter<T, BaseViewHolder>(layoutId, mDataList) {
        override fun convert(helper: BaseViewHolder?, item: T?) {
            if (item is OrientVo) {
                helper?.apply {
                    setText(R.id.txt_title, item.title)
                    setText(R.id.txt_desc, item.desc)
                    setText(R.id.txt_content, item.level.toString())
                    when (item.level) {
                        LEVEL.LEVEL_CRITICAL -> {
                            setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.red))
                        }
                        LEVEL.LEVEL_HIGH -> {
                            setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.color_violet))
                        }
                        LEVEL.LEVEL_MIDDLE -> {
                            setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.color_orange))
                        }
                        LEVEL.LEVEL_LOW -> {
                            setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.color_blue))
                        }
                        LEVEL.LEVEL_NOPE -> {
                            setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.color_green))
                        }
                        else -> {
                            setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.text_second_color))
                        }
                    }
                }
            }
        }
    }

    data class OrientVo(
            var title: String,
            var desc: String = "",
            var level: LEVEL = LEVEL.LEVEL_NOPE,
            var clazz: Class<*>? = null
    )

    enum class LEVEL {
        LEVEL_CRITICAL,
        LEVEL_HIGH,
        LEVEL_MIDDLE,
        LEVEL_LOW,
        LEVEL_NOPE
    }

    //
    class CodeDelegate(val al: Map<Int, OrientVo>) : ReadWriteProperty<Context, SparseArray<OrientVo>> {
        override fun getValue(thisRef: Context, property: KProperty<*>): SparseArray<OrientVo> {
            var sa = SparseArray<OrientVo>()
            for ((key, value) in al.entries) {
                sa.put(key, value)
            }
            return sa
        }

        override fun setValue(thisRef: Context, property: KProperty<*>, value: SparseArray<OrientVo>) {

        }

    }
}
