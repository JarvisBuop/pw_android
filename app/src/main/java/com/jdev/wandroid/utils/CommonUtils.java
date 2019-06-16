package com.jdev.wandroid.utils;

import android.text.TextUtils;
import android.widget.TextView;

/**
 * Created by JarvisDong on 2019/06/15.
 *
 * @Description:
 * @see:
 */
public class CommonUtils {
    public static void setMsgIntoView(String msg, TextView textView) {
        if (!TextUtils.isEmpty(msg) && textView != null) {
            textView.setText(msg);
        }
    }
}
