package com.juejinchain.android.model;

public class ShareModel {


    public static String TAG_INDEX = "index";
    public static String TAG_VIDEO = "video";
    public static String TAG_TASK  = "task";

    public static String WAY_QQ     = "qq";
    public static String WAY_WECHAT = "wechat";
    public static String WAY_FRIEND = "friend";
    public static String WAY_WEIBO  = "weibo";
    /**
     * 锁粉神器
     */
    public static String WAY_SUOFEN  = "system";

    /**
     * 名称
     */
    public String name;
    public String way;
    public String tag;

    /**
     * logo
     */
    public int logo;
    /**
     * 是否含有推荐
     */
    public boolean withDot;

    public ShareModel(String name, int logo, boolean withDot) {
        this.name = name;
        this.logo = logo;
        this.withDot = withDot;
    }


    public ShareModel(String name, String way, int logo, String tag) {
        this(name, logo, false);
        this.way = way;
        this.tag = tag;
    }
}
