package com.juejinchain.android.event;

import com.juejinchain.android.model.ShareModel;

public class ShareEvent {

    public ShareModel shareModel;

    public ShareEvent(ShareModel model){
        this.shareModel = model;
    }
}
