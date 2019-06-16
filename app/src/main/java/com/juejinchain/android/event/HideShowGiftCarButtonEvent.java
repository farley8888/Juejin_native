package com.juejinchain.android.event;

/**
 * 隐藏显示首页车标领取大礼包按钮
 */
public class HideShowGiftCarButtonEvent {

    public boolean isShow;

    public HideShowGiftCarButtonEvent(boolean isShow) {
        this.isShow = isShow;
    }
}
