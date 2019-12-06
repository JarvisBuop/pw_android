package com.jdev.wandroid.popwindow;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.jdev.kit.support.popwindow.BasePopWindow;
import com.jdev.wandroid.R;
import com.jdev.wandroid.utils.ViewUtils;

public class BasicFunctionPopWindow extends BasePopWindow {

    public View mAnimView;

    public TextView mContent;

    public View mConfirm;

    public View mCancel;

    protected OnConfirmClickListener mOnConfirmClickListener;

    public interface OnConfirmClickListener {
        void onClick();
    }

    @Override
    protected void initView() {
        super.initView();
        mContent = getMRootView().findViewById(R.id.content);
        mConfirm = getMRootView().findViewById(R.id.confirm);
        mCancel = getMRootView().findViewById(R.id.cancel);
        mAnimView = getMRootView().findViewById(R.id.anim_view);
    }

    private OnCancelClickListener mOnCancelClickListener;

    public interface OnCancelClickListener {
        void onClick();
    }

    public void addOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        mOnConfirmClickListener = onConfirmClickListener;
    }

    public void addOnCancelClickListener(OnCancelClickListener onCancelClickListener) {
        mOnCancelClickListener = onCancelClickListener;
    }

    public BasicFunctionPopWindow(Context context) {
        super(context);
        Companion.setSHOWDURATION(300);
        initialParams();
    }

    protected void initialParams() {
        initialViews();
        registerClick();
    }

    protected void initialViews() {
    }

    public BasicFunctionPopWindow(Context context, int layoutId) {
        super(context, layoutId);
        Companion.setSHOWDURATION(300);
        registerClick();
    }

    protected void registerClick() {
        registerParentClickListener();
        registerCancelClickListener();
        registerConfirmClickListener();
    }

    protected void registerParentClickListener() {
        if (getParent() != null)
            getParent().setOnClickListener(v -> {
                if (isSupportBGDismiss())
                    dismissAfterAnimation();
            });
    }

    protected void registerConfirmClickListener() {
        if (mConfirm != null)
            mConfirm.setOnClickListener(v -> {
                dismissAfterAnimation();
                if (mOnConfirmClickListener != null) {
                    mOnConfirmClickListener.onClick();
                }
            });
    }

    protected void registerCancelClickListener() {
        if (mCancel != null)
            mCancel.setOnClickListener(v -> {
                dismissAfterAnimation();
                if (mOnCancelClickListener != null) {
                    mOnCancelClickListener.onClick();
                }
            });
    }

    public BasicFunctionPopWindow(Context context, String content) {
        this(context);
        ViewUtils.setMsgIntoView(content, mContent);
    }

    public void setContent(String content) {
        ViewUtils.setMsgIntoView(content, mContent);
    }

    public void setContentMsg(String content) {
        ViewUtils.setMsgIntoView(content, mContent);
    }

    public BasicFunctionPopWindow(Context context, String content, String confirm, String cancel) {
        this(context, content);
        if (mCancel instanceof TextView)
            ViewUtils.setMsgIntoView(cancel, (TextView) mCancel);
        if (mConfirm instanceof TextView)
            ViewUtils.setMsgIntoView(confirm, (TextView) mConfirm);
    }

    @Override
    public int getLayoutId() {
        return R.layout.pop_basic_function;
    }

    @Override
    public View getAnimateView() {
        return mAnimView;
    }

    public View getCancelView() {
        return mCancel;
    }

    public View getConfirmView() {
        return mConfirm;
    }

}
