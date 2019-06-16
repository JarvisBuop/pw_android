package com.jdev.wandroid.noviceAnim;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;

import com.jdev.wandroid.R;

/**
 * Created by cjh on 17-2-16
 */

public abstract class BasePopWindow extends PopupWindow {

    protected static long SHOWDURATION = 300l;
    protected static long DURATION = 300l;

    protected static AlphaAnimation showAlphaAnimation = AnimationCreator.getAlphaAnimation(SHOWDURATION, 0f, 1f);
    protected static AlphaAnimation dismissAlphaAnimation = AnimationCreator.getAlphaAnimation(DURATION, 1f, 0f);
    protected static ScaleAnimation showScaleAnimation = AnimationCreator.getScaleAnimation(SHOWDURATION, 0.3f, 1f);
    protected static ScaleAnimation dismissScaleAnimation = AnimationCreator.getScaleAnimation(DURATION, 1f, 0.3f);

    protected static TranslateAnimation trasnLateInAnimation = AnimationCreator.getTranslateAnimationY(SHOWDURATION, 1f, 0f, true);
    protected static TranslateAnimation trasnLateOutAnimation = AnimationCreator.getTranslateAnimationY(BasePopWindow.DURATION, 0f, 1f, true);

    protected static TranslateAnimation trasnLateInAnimationX = AnimationCreator.getTranslateAnimationX(SHOWDURATION, 1f, 0f, true);
    protected static TranslateAnimation trasnLateOutAnimationX = AnimationCreator.getTranslateAnimationX(BasePopWindow.DURATION, 0f, 1f, true);

    protected View mRootView;
    protected Context mContext;
    protected View animateView;

    protected View parent;

    protected boolean supportShowAnimation = true;

    public BasePopWindow(Context context) {
        initPop(context, getLayoutId());
    }

    protected boolean isSupportBGDismiss() {
        return true;
    }

    public BasePopWindow(Context context, int layoutId) {
        initPop(context, layoutId);
    }

    protected void initPop(Context context, int layoutId) {
        mContext = context;
        setAnimationStyle(R.style.adv_popwin_anim_style);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(layoutId, null);
        initView();
        setContentView(mRootView);
        setWH();
        if (isSupportBGDismiss()) {
            ColorDrawable dw = new ColorDrawable(getBackDrawableColor());
            this.setBackgroundDrawable(dw);
            setOutsideTouchable(true);
        }
        try {
            animateView = getAnimateView();
        } catch (Exception e) {
            supportShowAnimation = false;
        }
    }

    protected void initView() {
        parent = mRootView.findViewById(R.id.parent);
    }

    protected void setWH() {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public abstract int getLayoutId();

    public abstract View getAnimateView();

    public int getBackDrawableColor() {
        return 0xb0000000;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        try {
            if (mContext == null) {
                return;
            }
            if (mContext instanceof Activity && ((Activity) mContext).isFinishing()) {
                return;
            }
            super.showAtLocation(parent, gravity, x, y);
            if (isSupportShowAnimation()) {
                show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public View getView(int viewId) {
        return mRootView.findViewById(viewId);
    }

    protected void show() {
        AnimationSet animationSet = createShowAnimation();
        if (animateView != null) {
            animateView.clearAnimation();
            animateView.startAnimation(animationSet);
        }
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                doShowEnd();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    protected void doShowEnd() {
    }

    @NonNull
    protected AnimationSet createShowAnimation() {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(showAlphaAnimation);
        animationSet.addAnimation(showScaleAnimation);
        return animationSet;
    }

    protected AnimationSet createHideAnimation() {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(dismissAlphaAnimation);
        animationSet.addAnimation(dismissScaleAnimation);
        return animationSet;
    }

    public void dismissAfterAnimation() {
        AnimationSet animSet = createHideAnimation();
        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRootView.post(() -> dismiss());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (animateView != null) {
            animateView.clearAnimation();
            animateView.startAnimation(animSet);
        }else
            dismiss();
    }

    protected boolean isSupportShowAnimation() {
        return supportShowAnimation;
    }


    protected View mFindViewById(int id) {
        if (mRootView != null) {
            View view = mRootView.findViewById(id);
            return view;
        } else
            return null;
    }

    @Override
    public void showAsDropDown(View anchor) {
        resetHeight(anchor);
        super.showAsDropDown(anchor);
    }

    private void resetHeight(View anchor) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            setHeight(h);
        }
    }

}
