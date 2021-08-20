package com.jdev.wandroid.test;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class MyView extends View {

    private static final String NAME = "MyView ";

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyView(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        String str = NAME + "dispatchTouchEvent" + ev.getAction();
        Log.e(MyViewGroup.TAG, str);
        append(str);
        boolean a = super.dispatchTouchEvent(ev);
        return a;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        String str = NAME + "onTouchEvent" + ev.getAction();
        Log.e(MyViewGroup.TAG, str);
        append(str);
        boolean a = super.onTouchEvent(ev);
        return a;
    }

    private TextView text;

    public void setText(TextView text) {
        this.text = text;
    }

    private void append(String str) {
        if (text == null) return;
        text.append(str);
        text.append("\n");
    }
}
