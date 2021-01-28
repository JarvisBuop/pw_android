package com.jdev.wandroid.ui.act

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.jdev.kit.baseui.BaseActivity
import com.jdev.kit.baseui.BaseFragment
import com.jdev.wandroid.R
import com.jdev.wandroid.ui.frg.*
import kotlinx.android.synthetic.main.app_activity_container.*
import java.lang.Exception

/**
 * info: create by jd in 2019/12/9
 * @see:
 * @description:
 *
 */
class ContainerActivity : BaseActivity() {
    var callback: (() -> Unit)? = null
    var permission = Manifest.permission.CAMERA
    var permissions = arrayOf(
            permission,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    companion object {
        const val EXTRA_KEY = "KEY"

        fun getFragmentByKey(name: String?): BaseFragment? {
            try {
                return Class.forName(name).newInstance() as BaseFragment?
            } catch (e: Exception) {
                LogUtils.e("构建fragment失败 ! ")
            }
            return null
        }

        fun launch(mContext: Context, name: String?) {
            mContext.startActivity(
                    Intent(mContext, ContainerActivity::class.java)
                            .putExtra(EXTRA_KEY, name)
            )
        }
    }

    var name: String? = null
    var currentFrag: BaseFragment? = null

    override fun getViewStubId(): Int {
        return R.layout.app_activity_container
    }

    override fun initIntentData(): Boolean {
        name = intent.getStringExtra(EXTRA_KEY)
        return !StringUtils.isEmpty(name)
    }

    override fun customOperate(savedInstanceState: Bundle?) {
        btn_retry.setOnClickListener {
            isPermission({ fillContainer() }, permission, permissions)
        }
        isPermission({ fillContainer() }, permission, permissions)
    }

    fun fillContainer() {
        currentFrag = getFragmentByKey(name)
        if (currentFrag != null) {
            layout_fragment_container.removeAllViews()
            setTextMarkTips(currentFrag!!.javaClass.simpleName)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.layout_fragment_container, currentFrag!!, currentFrag!!.javaClass.simpleName)
                    .commitAllowingStateLoss()
        } else {
            noDataOperate()
        }
    }

    override fun onResume() {
        super.onResume()
        currentFrag?.onResume()
    }

    fun isPermission(callback: (() -> Unit)? = null, permission: String, permissions: Array<out String>, isRequestPermission: Boolean = true): Boolean {
        if (!isRequestPermission) {
            return true
        }
        var checkSelfPermission = PermissionChecker.checkSelfPermission(this, permission)
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            callback?.invoke()
            return true
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1 && permissions.size == 3 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED
        ) {
            callback?.invoke()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}