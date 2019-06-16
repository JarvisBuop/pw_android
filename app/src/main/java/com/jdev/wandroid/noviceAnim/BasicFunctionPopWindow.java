package com.jdev.wandroid.noviceAnim;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.jdev.wandroid.R;
import com.jdev.wandroid.utils.CommonUtils;

/**
 * Created by cjh on 17-3-6
 */

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
        mContent = mRootView.findViewById(R.id.content);
        mConfirm = mRootView.findViewById(R.id.confirm);
        mCancel = mRootView.findViewById(R.id.cancel);
        mAnimView = mRootView.findViewById(R.id.anim_view);
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
        SHOWDURATION = 300;
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
        SHOWDURATION = 300;
        registerClick();
    }

    protected void registerClick() {
        registerParentClickListener();
        registerCancelClickListener();
        registerConfirmClickListener();
    }

    protected void registerParentClickListener() {
        if (parent != null)
            parent.setOnClickListener(v -> {
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
        CommonUtils.setMsgIntoView(content, mContent);
    }

    public void setContent(String content) {
        CommonUtils.setMsgIntoView(content, mContent);
    }

    public void setContentMsg(String content) {
        CommonUtils.setMsgIntoView(content, mContent);
    }

    public BasicFunctionPopWindow(Context context, String content, String confirm, String cancel) {
        this(context, content);
        if (mCancel instanceof TextView)
            CommonUtils.setMsgIntoView(cancel, (TextView) mCancel);
        if (mConfirm instanceof TextView)
            CommonUtils.setMsgIntoView(confirm, (TextView) mConfirm);
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
