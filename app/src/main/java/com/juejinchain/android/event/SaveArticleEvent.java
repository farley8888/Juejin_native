package com.juejinchain.android.event;

import com.juejinchain.android.model.NewsModel;

public class SaveArticleEvent {

    public NewsModel model;

    public SaveArticleEvent(NewsModel model){
        this.model = model;
    }
}
