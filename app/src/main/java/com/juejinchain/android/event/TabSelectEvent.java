package com.juejinchain.android.event;

/**
 * 主页tab切换
 */
public class TabSelectEvent {
    public int position;

    public TabSelectEvent(int position) {
        this.position = position;
    }
}
