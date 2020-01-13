package com.yhao.floatwindow;

/**
 * Created by yhao on 2018/5/5
 * https://github.com/yhaolpz
 */
public interface ViewStateListener {
    void onPositionUpdate(int x, int y);

    void onShow();

    void onHide();

    void onDismiss();

    void onMoveAnimStart();

    void onMoveAnimEnd();

    void onBackToDesktop();

    //多任务键,返回键 ,需要回到初始状态;
    void onSpecialEvent(String reason);
}
