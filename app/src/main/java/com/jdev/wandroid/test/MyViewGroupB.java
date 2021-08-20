package com.jdev.wandroid.test;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyViewGroupB extends ViewGroup {

    private static final String NAME = "MyViewGroupB ";

    public MyViewGroupB(Context context) {
        super(context);
    }

    public MyViewGroupB(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewGroupB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyViewGroupB(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        String str = NAME + "onInterceptTouchEvent" + ev.getAction();
        Log.e(MyViewGroup.TAG, str);
        append(str);
        boolean a = super.onInterceptTouchEvent(ev);
        return a;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        String str = NAME + "onTouchEvent" + ev.getAction();
        Log.e(MyViewGroup.TAG, str);
        append(str);
        boolean a = super.onTouchEvent(ev);
        return true ;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View childAt = getChildAt(0);
        if (childAt != null) {
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View childAt = getChildAt(0);
        if (childAt != null) {
            childAt.layout(0, 0, childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
        }
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
