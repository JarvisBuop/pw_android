package com.jdev.wandroid.ui.act

import android.gesture.GestureLibraries
import android.graphics.Color
import android.os.Bundle
import com.blankj.utilcode.util.LogUtils
import com.jarvisdong.kotlindemo.ui.BaseActivity
import com.jdev.wandroid.R
import kotlinx.android.synthetic.main.act_gesture.*

/**
 * Created by JarvisDong on 2019/07/20.
 *
 * @Description:  从手势库中保存当前手势;
 * @see:
 *
 */
class GestureTestAct : BaseActivity() {
    val path: String = "/mnt/sdcard/mygestures"
    val gestureName: String = "mygestures_"
    var count: Int = 0
    override fun getViewStubId(): Int {
        return R.layout.act_gesture
    }

    override fun initIntentData(): Boolean {
        btn_save.isEnabled = false
        return true
    }

    override fun customOperate(savedInstanceState: Bundle?) {
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
