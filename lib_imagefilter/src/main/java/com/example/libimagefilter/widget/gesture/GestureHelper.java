package com.example.libimagefilter.widget.gesture;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;
import android.widget.Scroller;

import com.blankj.utilcode.util.LogUtils;
import com.example.libimagefilter.widget.JdGPUDisplayView;

/**
 * info: create by jd in 2020/1/16
 *
 * @see JdGPUDisplayView
 * @description:
 *
 * 使用于 [JdGPUDisplayView] 添加手势;
 */
@Deprecated
public class GestureHelper {
    public final String TAG = "JdGPUDisplayView";
    public final boolean isDebug = true;

//    private static class Holder {
//        private static GestureHelper mHelper = new GestureHelper();
//    }

//    public static GestureHelper getInstance(Context mContext) {
////        return Holder.mHelper;
//    }

    public GestureHelper(Context mContext) {
        this.mContext = mContext;
        initGesture();
    }

    private ControllListener mListener;

    public interface ControllListener {
        void setScaleXY(float mScaleX, float mScaleY);

        void setScrollXY(int mScrollX, int mScrollY);

        void postAction(Runnable runnable);

        void removeAction(Runnable runnable);

        void invalidateView();

//        Context getInputContext();
    }
    private Context mContext;
    //===================手势部分==================
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mDetector;
    private float mScale = 1.0f;
    private final static float MAX_SCALE = 2.5f;
    private final static float MIN_SCALE = 1f;
    // 当前是否处于放大状态
    private boolean isZoonUp = false;
    //todo test
    private boolean isEnable = true;
    //translate
    private int mTranslateX;
    private int mTranslateY;
    private int mLastTranslateX;
    private int mLastTranslateY;

    private Transform mTranslate = null;
    private boolean hasMultiTouch = false;

    public void setScaleToOrigin() {
        mTranslate.withScale(mScale, 1f);
        mTranslate.withTranslate(0, 0, -mTranslateX, -mTranslateY);
        mTranslate.start();
    }


    class Transform implements Runnable {
        boolean isRuning;
        Scroller mScaleScroller;
        OverScroller mTranslateScroller;
        int ANIMA_DURING = 320;

        Transform() {
            DecelerateInterpolator i = new DecelerateInterpolator();
            mScaleScroller = new Scroller(mContext, i);
            mTranslateScroller = new OverScroller(mContext, i);
        }

        @Override
        public void run() {
            if (!isRuning) return;
            boolean endAnima1 = true;
            boolean endAnima2 = true;

            if (mScaleScroller.computeScrollOffset()) {
                mScale = mScaleScroller.getCurrX() / 10000f;
                endAnima1 = false;
            }

            if (mTranslateScroller.computeScrollOffset()) {
                int tx = mTranslateScroller.getCurrX() - mLastTranslateX;
                int ty = mTranslateScroller.getCurrY() - mLastTranslateY;
                mTranslateX += tx;
                mTranslateY += ty;
                mLastTranslateX = mTranslateScroller.getCurrX();
                mLastTranslateY = mTranslateScroller.getCurrY();
                endAnima2 = false;
            }

            if (!(endAnima1 && endAnima2)) {
                if (!endAnima1) {
//                    setScaleX(mScale);
//                    setScaleY(mScale);
                    mListener.setScaleXY(mScale, mScale);
                }
                if (!endAnima2) {
//                    setScrollX(mTranslateX);
//                    setScrollY(mTranslateY);
                    mListener.setScrollXY(mTranslateX, mTranslateY);
                }
                postExecute();
            } else {
                isRuning = false;
                mListener.invalidateView();
            }
        }

        void withScale(float form, float to) {
            mScaleScroller.startScroll((int) (form * 10000), 0, (int) ((to - form) * 10000), 0, ANIMA_DURING);
        }

        void withTranslate(int startX, int startY, int deltaX, int deltaY) {
            mLastTranslateX = 0;
            mLastTranslateY = 0;
            mTranslateScroller.startScroll(0, 0, deltaX, deltaY, ANIMA_DURING);
        }

        void start() {
            isRuning = true;
            postExecute();
        }

        void stop() {
            mListener.removeAction(this);
            mTranslateScroller.abortAnimation();
            mScaleScroller.abortAnimation();
            isRuning = false;
        }

        private void postExecute() {
            if (isRuning) mListener.postAction(this);
        }
    }
    //===================手势部分==================

    public void initGesture() {
        mTranslate = new Transform();
        mScaleDetector = new ScaleGestureDetector(mContext, mScaleListener);
        mDetector = new GestureDetector(mContext, mGestureListener);
    }

    public void setControllListener(ControllListener mListener) {
        this.mListener = mListener;
    }

    public boolean dispatchTouchEvent(MotionEvent event, ViewParent parent) {
        if (isEnable) {
            LogUtils.eTag(TAG, "dispatchTouchEvent: " + event.getPointerCount());
            if (event.getPointerCount() >= 2) {
                hasMultiTouch = true;
            } else {
                hasMultiTouch = false;
            }
            mDetector.onTouchEvent(event);
            mScaleDetector.onTouchEvent(event);
            if (mScale > 1) {
                parent.requestDisallowInterceptTouchEvent(true);
            } else {
                parent.requestDisallowInterceptTouchEvent(false);
            }
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                onFitEvent(event);
            return true;
        } else {
            return false;
        }
    }

    private void onFitEvent(MotionEvent event) {
        if (mTranslate.isRuning) return;

        float scale = mScale;
        if (mScale < MIN_SCALE) {
            scale = MIN_SCALE;
            mTranslate.withScale(mScale, scale);
        } else if (mScale > MAX_SCALE) {
            scale = MAX_SCALE;
            mTranslate.withScale(mScale, scale);
        }
        if (mScale <= 1) {
            mTranslate.withTranslate(0, 0, -mTranslateX, -mTranslateY);
        }
        mTranslate.start();
    }

    private ScaleGestureDetector.OnScaleGestureListener mScaleListener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor))
                return false;

            float lastScale = mScale;
            mScale *= scaleFactor;

            if (isDebug) {
                LogUtils.eTag(TAG, lastScale + "/" + mScale + " / " + scaleFactor);
            }

//            setScaleX(mScale);
//            setScaleY(mScale);
            mListener.setScaleXY(mScale, mScale);
            //return true 表示不重新计算scaleFactor,值保持不变 0.5->0.5 可能抖动;
            //return false 表示重新计算scaleFactor,值重新变化 0.5->1.0
            return false;
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            //需要双击还原;
            isZoonUp = mScale < 1 || mScale >= MAX_SCALE;
        }
    };

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            LogUtils.eTag(TAG, "mGestureListener down " + e.getPointerCount());
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (isDebug) {
                LogUtils.eTag(TAG, "velocityX: " + velocityX + " velocityY:" + velocityY);
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (hasMultiTouch) return false;
            if (mTranslate.isRuning) {
                mTranslate.stop();
            }

            mTranslateX += distanceX;
            mTranslateY += distanceY;

            if (isDebug) {
                LogUtils.eTag(TAG, "distanceX: " + distanceX + " distanceY:" + distanceY
                                + "\n transX " + mTranslateX + " transY " + mTranslateY
//                        + "\n scrollX " + getScrollX() + " scrollY " + getScrollY()
//                        + "\n pointcount: " + e1.getPointerCount() + "   " + e2.getPointerCount()
                );
            }

//            setScrollX(mTranslateX);
//            setScrollY(mTranslateY);
            mListener.setScrollXY(mTranslateX, mTranslateY);
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mTranslate.stop();

            float from = 1;
            float to = 1;

            if (isZoonUp) {
                from = mScale;
                to = 1;
            } else {
                from = mScale;
                to = MAX_SCALE;
            }

            isZoonUp = !isZoonUp;

            mTranslate.withTranslate(0, 0, -mTranslateX, -mTranslateY);
            mTranslate.withScale(from, to);

            mTranslate.start();
            return false;
        }
    };
}
