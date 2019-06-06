package com.juejinchain.android.event;

/**
 * 分享回调
 */
public class ShareCallbackEvent {

    public String msg;
    public int code;

    public ShareCallbackEvent(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }
}
