package com.jdev.wandroid.noviceAnim;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by cjh on 17-2-16
 */

public class AnimationCreator {

    /**
     * 制定 AlphaAnimation
     *
     * @return
     */
    public static AlphaAnimation getAlphaAnimation(long durationMillis,
                                                   float fromAlpha, float toAlpha) {
        AlphaAnimation aa = new AlphaAnimation(fromAlpha, toAlpha);
        aa.setDuration(durationMillis);
        aa.setFillAfter(true);
        aa.setFillEnabled(true);
        return aa;
    }

    /**
     * 制定 ScaleAnimation
     *
     * @param durationMillis
     * @param from
     * @param to
     * @return
     */
    public static ScaleAnimation getScaleAnimation(long durationMillis, float from, float to) {
        ScaleAnimation sa = new ScaleAnimation(from, to, from, to,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(durationMillis);
        sa.setFillAfter(true);
        sa.setFillEnabled(true);
        return sa;
    }

    public static ScaleAnimation getScaleYAnimation(long durationMillis, float fromY, float toY) {
        ScaleAnimation sa = new ScaleAnimation(1f, 1f, fromY, toY,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(durationMillis);
        sa.setFillAfter(true);
        sa.setFillEnabled(true);
        return sa;
    }

    /**
     * 制定 TranslateAnimation
     *
     * @return
     */
    public static TranslateAnimation getTranslateAnimationY(long durationMillis,
                                                            float fromYValue, float toYValue, boolean isKeepEnd) {
        TranslateAnimation ta = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, fromYValue,
                Animation.RELATIVE_TO_SELF, toYValue);
        ta.setDuration(durationMillis);
        if (isKeepEnd) {
            ta.setFillAfter(true);
            ta.setFillEnabled(true);
        }
        return ta;
    }

    /**
     * 制定 TranslateAnimation
     *
     * @return
     */
    public static TranslateAnimation getTranslateAnimationY(long durationMillis,
                                                            float fromYValue, float toYValue, boolean isKeepEnd, Interpolator interpolator) {
        TranslateAnimation ta = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, fromYValue,
                Animation.RELATIVE_TO_SELF, toYValue);
        ta.setDuration(durationMillis);
        ta.setInterpolator(interpolator);
        if (isKeepEnd) {
            ta.setFillAfter(true);
            ta.setFillEnabled(true);
        }
        return ta;
    }

    /**
     * 制定 TranslateAnimation
     *
     * @return
     */
    public static TranslateAnimation getTranslateAnimationX(long durationMillis,
                                                            float fromXValue, float toXValue, boolean isKeepEnd) {
        TranslateAnimation ta = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, fromXValue, Animation.RELATIVE_TO_SELF, toXValue,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        ta.setDuration(durationMillis);
        if (isKeepEnd) {
            ta.setFillAfter(true);
            ta.setFillEnabled(true);
        }
        return ta;
    }

    /**
     * 制定 RotateAnimation
     *
     * @param v
     * @param v1
     * @param durationMillis
     * @return
     */
    public static RotateAnimation getRotationAnimation(float v, float v1, long durationMillis) {
        RotateAnimation animation = new RotateAnimation(v, v1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(durationMillis);
        animation.setFillAfter(true);
        return animation;
    }

    /**
     * 制定 RotateAnimation
     *
     * @param v
     * @param v1
     * @param durationMillis
     * @return
     */
    public static RotateAnimation getRotationAnimation(float v, float v1, long durationMillis, boolean fillAfter) {
        RotateAnimation animation = new RotateAnimation(v, v1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(durationMillis);
        animation.setFillAfter(fillAfter);
        return animation;
    }

    public static AnimationSet getAnimationSet(Animation... animations) {
        AnimationSet animationSet = new AnimationSet(true);
        for (Animation animation : animations) {
            animationSet.addAnimation(animation);
        }
        animationSet.setFillAfter(true);
        animationSet.setFillBefore(false);
        return animationSet;
    }

    public static ObjectAnimator getObjectAnimator(View view,float x,float y){
        ObjectAnimator anim = ObjectAnimator.ofFloat(view,
                "rotation",
                0f, -9f, 9f, -6.5f, 6.5f, -4.5f, 4.5f, -3f, 3f, -2f, 2f, -1.25f, 1.25f, -0.75f, 0.75f, -0.5f,0.5f, -0.35f, 0.35f, -0.1f, 0.1f,
                0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,0f);
        view.setPivotX(x);
        view.setPivotY(y);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setDuration(2500);
        return anim;
    }

}
