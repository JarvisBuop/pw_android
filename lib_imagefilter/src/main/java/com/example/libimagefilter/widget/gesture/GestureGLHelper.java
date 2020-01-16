package com.example.libimagefilter.widget.gesture;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;
import android.widget.Scroller;

import com.blankj.utilcode.util.LogUtils;

/**
 * info: create by jd in 2020/1/16
 *
 * @see:
 * @description:
 */
public class GestureGLHelper {
    public final String TAG = "JdGPUDisplayView";
    public final boolean isDebug = true;

    public GestureGLHelper(Context mContext) {
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
                    mListener.setScaleXY(mScale, mScale);
                }
                if (!endAnima2) {
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
//            LogUtils.eTag(TAG, "dispatchTouchEvent: " + event.getPointerCount());
            if (event.getPointerCount() >= 2) {
                hasMultiTouch = true;
            } else {
                hasMultiTouch = false;
            }
            mDetector.onTouchEvent(event);
            mScaleDetector.onTouchEvent(event);
            if (mScale > 1) {
                retrievalRequestDisallowInterceptTouchEvent(parent, true);
            } else {
                retrievalRequestDisallowInterceptTouchEvent(parent, false);
            }
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                onFitEvent(event);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是父view们都不允许拦截;
     *
     * @param parent
     * @param bool
     */
    private void retrievalRequestDisallowInterceptTouchEvent(ViewParent parent, boolean bool) {
        parent.requestDisallowInterceptTouchEvent(bool);
        if (parent.getParent() != null && parent.getParent() instanceof ViewGroup) {
            retrievalRequestDisallowInterceptTouchEvent(parent.getParent(), bool);
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

            mListener.setScaleXY(mScale, mScale);


            /**
             //return true 表示不重新计算scaleFactor,值保持不变 0.5->0.5 可能抖动;
             //return false 表示重新计算scaleFactor,值重新变化 0.5->1.0
             *
             * 经测试可知:
             * - 当使用在 [ JdGPUDisplayView(FrameLayout) ] 中 ,返回false 放缩不违和;
             * - 当使用在 [ GPUImageGestureGLSurfaceView(SurfaceView) ] 中,返回true 不违和;
             *
             *   注释:  是否detector 应该考虑事件是否被处理了;
             *   如果一个事件没有被处理, detector继续累积运动量直到事件被处理;一个用处: 如果改变是大于0.01,仅仅想要去更新缩放因子;
             *   true 事件处理,重新计算因子;
             *   false 事件没有处理,继续累积运动量直到事件处理;
             *
             */
            return true;
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
//                LogUtils.eTag(TAG, "velocityX: " + velocityX + " velocityY:" + velocityY);
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
//                LogUtils.eTag(TAG, "distanceX: " + distanceX + " distanceY:" + distanceY
//                                + "\n transX " + mTranslateX + " transY " + mTranslateY
//                        + "\n scrollX " + getScrollX() + " scrollY " + getScrollY()
//                );
            }

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
