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
import com.jdev.module_video.ui.act.GithubJiaoziVideoAct
import com.jdev.module_welcome.ui.act.FullscreenActivity
import com.jdev.module_welcome.ui.act.WelcomeActivity
import com.jdev.wandroid.R
import kotlinx.android.synthetic.main.app_activity_main.*
import kotlinx.android.synthetic.main.app_include_main_top.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * home page
 */
class MainActivity : BaseActivity() {

    //----------------DATA CODE---------------------
    //top recyclerview datas
    var mTopDatas = arrayListOf<OrientVo>(
            OrientVo("kotlin",
                    "kotlin 协程",
                    clazz = ContainerActivity::class.java,
                    clazzCode = KEY_KOTLIN_TEST, level = LEVEL.LEVEL_HIGH),
            OrientVo("mvvm",
                    "mvvm test",
                    clazz = ContainerActivity::class.java,
                    clazzCode = KEY_MVVM_TEST, level = LEVEL.LEVEL_HIGH),
            OrientVo("gpuimage 滤镜列表测试 (有问题)",
                    "Android filters based on OpenGL (idea from GPUImage for iOS)",
                    clazz = ContainerActivity::class.java,
                    clazzCode = KEY_ANDROID_GPUIMAGE),
            OrientVo("gpuimage 滤镜单张图片测试",
                    level = LEVEL.LEVEL_HIGH,
                    clazz = ContainerActivity::class.java,
                    clazzCode = KEY_ANDROID_GPUIMAGE_SIMPLE),
            OrientVo("gpuimage 美颜相机",
                    "gpuimage camera demo",
                    clazz = ContainerActivity::class.java,
                    clazzCode = KEY_ANDROID_GPUIMAGE_CAMERA),
            OrientVo("magicCamera 美颜相机 MagicCameraDemo",
                    "https://github.com/jameswanliu/MagicCamera_master",
                    level = LEVEL.LEVEL_HIGH,
                    clazz = ContainerActivity::class.java,
                    clazzCode = KEY_ANDROID_MAGIC_CAMERA),
            OrientVo("magicCamera MagicImageView",
                    "magic 图片处理demo",
                    clazz = ContainerActivity::class.java,
                    clazzCode = KEY_ANDROID_GPU_TEST),
            OrientVo("opengl test",
                    "opengl demo",
                    clazz = ContainerActivity::class.java,
                    clazzCode = KEY_ANDROID_OPENGL_SIMGLE_DEMO),

            OrientVo("自定义悬浮框 仿微信",
                    "window manager",
                    clazz = ContainerActivity::class.java,
                    clazzCode = KEY_ANDROID_FLOAT_WINDOW
            ),
            OrientVo("pip test",
                    "PictureInPicture Mode",
                    clazz = ContainerActivity::class.java,
                    clazzCode = KEY_ANDROID_PIP
            ),
            OrientVo("mediaMuxer test",
                    "MediaMuxer",
                    clazz = ContainerActivity::class.java,
                    clazzCode = KEY_ANDROID_MEDIA_MUXER
            ),
            OrientVo("test",
                    "desc")
    )


    //bottom recyclerview datas
    var mBottomDatas = arrayListOf<OrientVo>(
            OrientVo("组件module_welcome",
                    "显示welcome页面",
                    clazz = WelcomeActivity::class.java),
            OrientVo("组件module_video",
                    "测试视频及三方",
                    clazz = GithubJiaoziVideoAct::class.java),
            OrientVo("处理滑动冲突首页效果",
                    "两个三方处理滑动冲突",
                    clazz = FullscreenActivity::class.java),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc"),
            OrientVo("test", "desc")
    )

    //secretcode other datas (not important)
    var mSecretString = mutableMapOf<Int, OrientVo>(
            Pair(KEY_GESTURE, OrientVo("${KEY_GESTURE} : gesture_test")),
            Pair(KEY_PHOTOVIEW, OrientVo("${KEY_PHOTOVIEW} : photoview_test")),
            Pair(KEY_SHADOW, OrientVo("${KEY_SHADOW} : shadow_test")),
            Pair(KEY_WEBP, OrientVo("${KEY_WEBP} : webp_test"))
    )

    var mSecretCodes: SparseArray<OrientVo> by CodeDelegate(mSecretString)

    //----------------LOGIC CODE--------------------
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
        fabView?.visibility = View.VISIBLE
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
            var codeDatas = mSecretCodes
            if (codeDatas?.size() == 0) return@setOnClickListener
            AlertDialog.Builder(mContext)
                    .setAdapter(object : BaseAdapter() {
                        @SuppressLint("ViewHolder")
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                            var view = LayoutInflater.from(mContext).inflate(R.layout.app_item_centerdrag, parent, false)
                            var txtTips = view.findViewById<TextView>(R.id.scroll_tip)
                            var id = codeDatas.keyAt(position)
                            var orientVo = codeDatas[id]
                            txtTips.text = orientVo?.title ?: ""
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
        mAdapterTop.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT)
        mAdapterTop.isFirstOnly(false)
        mAdapterBottom = MyAdapter(R.layout.app_item_normalitem_reverse)
        mAdapterBottom.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT)
        mAdapterBottom.isFirstOnly(false)
        mAdapterTop.setOnItemClickListener { adapter, view, position ->
            clickItemByClazz(mAdapterTop.getItem(position), mAdapterTop.getItem(position)?.clazzCode)
        }

        mAdapterBottom.setOnItemClickListener { adapter, view, position ->
            clickItemByClazz(mAdapterBottom.getItem(position), mAdapterTop.getItem(position)?.clazzCode)
        }

        first_recyclerview.layoutManager = LinearLayoutManager(mContext)
        first_recyclerview.adapter = mAdapterTop
        second_recyclerview.layoutManager = LinearLayoutManager(mContext)
        second_recyclerview.adapter = mAdapterBottom
    }

    private fun clickItemByClazz(item: OrientVo?, clazzCode: Int? = -1) {
        item?.also {
            if (it.clazz != null) {
                if (it.clazz == ContainerActivity::class.java && clazzCode != null && clazzCode != -1) {
                    mSecretString.put(clazzCode, item)
                    clickItemByCode(clazzCode)
                } else {
                    mContext.startActivity(Intent(mContext, it.clazz))
                }
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


    //----------------EXTRA CLASS------------------
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
                            setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.color_orange))
                        }
                        LEVEL.LEVEL_MIDDLE -> {
                            setTextColor(R.id.txt_content, ResourceIdUtils.getColorById(R.color.color_violet))
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
            var clazz: Class<*>? = null,
            var clazzCode: Int = -1
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
