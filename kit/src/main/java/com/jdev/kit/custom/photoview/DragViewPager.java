package com.jdev.kit.custom.photoview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * info: create by jd in 2019/6/17
 *
 * @see:
 * @description:  仿微信 下拉消失;
 */
public class DragViewPager extends ViewPager {
    public DragViewPager(@NonNull Context context) {
        super(context);
    }

    public DragViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
}
