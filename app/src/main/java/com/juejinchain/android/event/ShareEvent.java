package com.juejinchain.android.event;

import com.juejinchain.android.model.ShareModel;

public class ShareEvent {

    public ShareModel shareModel;

    //分享类型
    //首页
    public static final String TYPE_INDEX = "/";
    //文章页
    public static final String TYPE_ARTICLE = "ArticleDetails";
    //视频的
    public static final String TYPE_VIDEO = "VideoDetail";

    public String type;


    /**
     * 不同类型分享href的path不一样
     * 首页   /
     * 文章页 /ArticleDetails/5130958
     * 视频  /VideoDetail/598401
     */
    public String typePath;

    /**
     *
     * @param model
     * @param type 类型
     * @param id 文章或视频id 首页的不用传
     */
    public ShareEvent(ShareModel model, String type, String id){
        this.shareModel = model;
        this.type = type;

        if (type == TYPE_INDEX)
            this.typePath = "/";
        else
            this.typePath = type+"/"+id;
    }

}
