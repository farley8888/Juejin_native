package com.juejinchain.android.event;

/**
 * 选中后第二次点击
 * 用于更新页面等
 */
public class TabSelectedEvent {
    public int position;

    public TabSelectedEvent(int position) {
        this.position = position;
    }
}
