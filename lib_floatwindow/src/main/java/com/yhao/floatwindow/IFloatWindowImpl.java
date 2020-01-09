package com.yhao.floatwindow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by yhao on 2017/12/22.
 * https://github.com/yhaolpz
 */

public class IFloatWindowImpl extends IFloatWindow {

    private FloatWindow.B mB;
    private FloatView mFloatView;
    private FloatLifecycle mFloatLifecycle;
    private boolean isShow;
    private boolean once = true;
    private ValueAnimator mAnimator;
    private TimeInterpolator mDecelerateInterpolator;
    private int mSlop;


    private IFloatWindowImpl() {

    }

    IFloatWindowImpl(FloatWindow.B mB) {
        setConfig(mB);
    }

    private void initWindowImplByConfig() {
        createFloatView();
        initTouchEvent();
        mFloatView.setSize(mB.mWidth, mB.mHeight);
        mFloatView.setGravity(mB.gravity, mB.xOffset, mB.yOffset);
        mFloatView.setView(mB.mView);
        registerLifecycle();
    }

    private void registerLifecycle() {
        if (mFloatLifecycle == null) {
            mFloatLifecycle = new FloatLifecycle(mB.mApplicationContext, mB.mShow, mB.mActivities, new LifecycleListener() {
                @Override
                public void onShow() {
                    show();
                }

                @Override
                public void onHide() {
                    hide();
                }

                @Override
                public void onBackToDesktop() {
                    if (!mB.mDesktopShow) {
                        hide();
                    }
                    if (mB.mViewStateListener != null) {
                        mB.mViewStateListener.onBackToDesktop();
                    }
                }
            });
        } else {
            mFloatLifecycle.setShowParams(mB.mShow, mB.mActivities);
        }
    }

    private void createFloatView() {
        if (mFloatView != null) return;
        if (mB.mMoveType == MoveType.fixed) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                mFloatView = new FloatPhone(mB.mApplicationContext, mB.mPermissionListener);
            } else {
                mFloatView = new FloatToast(mB.mApplicationContext);
            }
        } else {
            mFloatView = new FloatPhone(mB.mApplicationContext, mB.mPermissionListener);
        }
    }

    @Override
    public void show() {
        if (once) {
            mFloatView.init();
            once = false;
            isShow = true;
        } else {
            if (isShow) {
                return;
            }
            getView().setVisibility(View.VISIBLE);
            isShow = true;
        }
        if (mB.mViewStateListener != null) {
            mB.mViewStateListener.onShow();
        }
    }

    @Override
    public void hide() {
        if (once || !isShow) {
            return;
        }
        getView().setVisibility(View.INVISIBLE);
        isShow = false;
        if (mB.mViewStateListener != null) {
            mB.mViewStateListener.onHide();
        }
    }

    @Override
    public boolean isShowing() {
        return isShow;
    }

    @Override
    void dismiss() {
        mFloatView.dismiss();
        isShow = false;
        if (mB.mViewStateListener != null) {
            mB.mViewStateListener.onDismiss();
        }
    }

    @Override
    public FloatWindow.B getConfig() {
        return mB;
    }

    @Override
    public void setConfig(FloatWindow.B mB) {
        this.mB = mB;
        initWindowImplByConfig();
    }

    @Override
    public void updateX(int x) {
        checkMoveType();
        mB.xOffset = x;
        mFloatView.updateX(x);
    }

    @Override
    public void updateY(int y) {
        checkMoveType();
        mB.yOffset = y;
        mFloatView.updateY(y);
    }

    @Override
    public void updateX(int screenType, float ratio) {
        checkMoveType();
        mB.xOffset = (int) ((screenType == Screen.width ?
                Util.getScreenWidth(mB.mApplicationContext) :
                Util.getScreenHeight(mB.mApplicationContext)) * ratio);
        mFloatView.updateX(mB.xOffset);

    }

    @Override
    public void updateY(int screenType, float ratio) {
        checkMoveType();
        mB.yOffset = (int) ((screenType == Screen.width ?
                Util.getScreenWidth(mB.mApplicationContext) :
                Util.getScreenHeight(mB.mApplicationContext)) * ratio);
        mFloatView.updateY(mB.yOffset);

    }

    @Override
    public int getX() {
        return mFloatView.getX();
    }

    @Override
    public int getY() {
        return mFloatView.getY();
    }


    @Override
    public View getView() {
        mSlop = ViewConfiguration.get(mB.mApplicationContext).getScaledTouchSlop();
        return mB.mView;
    }


    private void checkMoveType() {
        if (mB.mMoveType == MoveType.fixed) {
            throw new IllegalArgumentException("FloatWindow of this tag is not allowed to move!");
        }
    }


    private void initTouchEvent() {
        switch (mB.mMoveType) {
            case MoveType.inactive:
            case MoveType.fixed:
                getView().setOnTouchListener(null);
                break;
            default:
                getView().setOnTouchListener(new DefaultTouchListener());
        }
    }

    class DefaultTouchListener implements View.OnTouchListener {
        float lastX, lastY, changeX, changeY;
        int newX, newY;
        private float downX;
        private float downY;
        private float upX;
        private float upY;
        //防止微小位移不能点击;
        boolean isDrag = false;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            LogUtil.e("ontouch:  " + event.getAction());
            if (mB.mMoveType == MoveType.inactive) return false;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getRawX();
                    downY = event.getRawY();
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    cancelAnimator();
                    isDrag = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    changeX = event.getRawX() - lastX;
                    changeY = event.getRawY() - lastY;
                    if (!isDrag && Math.hypot(Math.abs(changeX), Math.abs(changeY)) < mSlop)
                        return isDrag;
                    isDrag = true;
                    newX = (int) (mFloatView.getX() + changeX);
                    newY = (int) (mFloatView.getY() + changeY);
                    retrictXY(v);
                    mFloatView.updateXY(newX, newY);
                    if (mB.mViewStateListener != null) {
                        mB.mViewStateListener.onPositionUpdate(newX, newY);
                    }
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isDrag) return false;
                    upX = event.getRawX();
                    upY = event.getRawY();
                    switch (mB.mMoveType) {
                        case MoveType.slide:
                            int startX = mFloatView.getX();
                            int endX = (startX * 2 + v.getWidth() > Util.getScreenWidth(mB.mApplicationContext)) ?
                                    Util.getScreenWidth(mB.mApplicationContext) - v.getWidth() - mB.mSlideRightMargin :
                                    mB.mSlideLeftMargin;
                            mAnimator = ObjectAnimator.ofInt(startX, endX);
                            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    int x = (int) animation.getAnimatedValue();
                                    mFloatView.updateX(x);
                                    if (mB.mViewStateListener != null) {
                                        mB.mViewStateListener.onPositionUpdate(x, (int) upY);
                                    }
                                }
                            });
                            startAnimator();
                            break;
                        case MoveType.back:
                            PropertyValuesHolder pvhX = PropertyValuesHolder.ofInt("x", mFloatView.getX(), mB.xOffset);
                            PropertyValuesHolder pvhY = PropertyValuesHolder.ofInt("y", mFloatView.getY(), mB.yOffset);
                            mAnimator = ObjectAnimator.ofPropertyValuesHolder(pvhX, pvhY);
                            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    int x = (int) animation.getAnimatedValue("x");
                                    int y = (int) animation.getAnimatedValue("y");
                                    mFloatView.updateXY(x, y);
                                    if (mB.mViewStateListener != null) {
                                        mB.mViewStateListener.onPositionUpdate(x, y);
                                    }
                                }
                            });
                            startAnimator();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            return isDrag;
        }

        private void retrictXY(View v) {
            //retrict
            if (newX + v.getWidth() > Util.getScreenWidth(mB.mApplicationContext)) {
                newX = Util.getScreenWidth(mB.mApplicationContext) - v.getWidth();
            } else if (newX < 0) {
                newX = 0;
            }

            if (newY + v.getHeight() > Util.getScreenHeight(mB.mApplicationContext)) {
                newY = Util.getScreenHeight(mB.mApplicationContext) - v.getHeight();
            } else if (newY < 0) {
                newY = 0;
            }
        }
    }


    private void startAnimator() {
        if (mB.mInterpolator == null) {
            if (mDecelerateInterpolator == null) {
                mDecelerateInterpolator = new DecelerateInterpolator();
            }
            mB.mInterpolator = mDecelerateInterpolator;
        }
        mAnimator.setInterpolator(mB.mInterpolator);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator.removeAllUpdateListeners();
                mAnimator.removeAllListeners();
                mAnimator = null;
                if (mB.mViewStateListener != null) {
                    mB.mViewStateListener.onMoveAnimEnd();
                }
            }
        });
        mAnimator.setDuration(mB.mDuration).start();
        if (mB.mViewStateListener != null) {
            mB.mViewStateListener.onMoveAnimStart();
        }
    }

    private void cancelAnimator() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

}
