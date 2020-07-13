package com.jdev.wandroid.ui.frg

import android.gesture.GestureLibraries
import android.gesture.GestureOverlayView
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.LogUtils
import com.jdev.kit.baseui.BaseFragment
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import kotlinx.android.synthetic.main.app_frag_gesture.*

/**
 * info: create by jd in 2019/12/9
 * @see:
 * @description: 从手势库中保存当前手势;
 *
 */
class GestureTestFrag : BaseViewStubFragment() {
    val path: String = "/mnt/sdcard/mygestures"
    val gestureName: String = "mygestures_"
    var count: Int = 0

    lateinit var btn_save : TextView
    lateinit var gesture_view : GestureOverlayView
    lateinit var image_show : ImageView

    override fun getViewStubId(): Int {
        return R.layout.app_frag_gesture
    }

    override fun initIntentData(): Boolean = true


    override fun customOperate(savedInstanceState: Bundle?) {
        btn_save = findView(R.id.btn_save)
        gesture_view = findView(R.id.gesture_view)
        image_show = findView(R.id.image_show)


        btn_save.isEnabled = false

        gesture_view.gestureColor = Color.RED
        gesture_view.gestureStrokeWidth = 5f
        gesture_view.addOnGesturePerformedListener { overlay, gesture ->
            LogUtils.e(TAG, "addOnGesturePerformedListener")

            image_show.setImageBitmap(gesture.toBitmap(128, 128, 10, Color.GREEN))

            btn_save.isEnabled = true
            btn_save.setOnClickListener {
                var gesturelib = GestureLibraries.fromFile(path)
                gesturelib.addGesture(gestureName + count, gesture)
                count++
                var save = gesturelib.save()
                LogUtils.e("保存状态: $save in $path name: ${gestureName + count}")
            }

        }
    }

}