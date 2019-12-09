package com.jdev.wandroid.ui.frg

import android.gesture.GestureLibraries
import android.graphics.Color
import android.os.Bundle
import com.blankj.utilcode.util.LogUtils
import com.jdev.kit.baseui.BaseFragment
import com.jdev.wandroid.R
import kotlinx.android.synthetic.main.app_frag_gesture.*

/**
 * info: create by jd in 2019/12/9
 * @see:
 * @description: 从手势库中保存当前手势;
 *
 */
class GestureTestFrag : BaseFragment() {
    val path: String = "/mnt/sdcard/mygestures"
    val gestureName: String = "mygestures_"
    var count: Int = 0

    override fun getViewStubId(): Int {
        return R.layout.app_frag_gesture
    }

    override fun initIntentData(): Boolean = true

    override fun customOperate(savedInstanceState: Bundle?) {
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
                gesturelib.save()
            }

        }
    }

}