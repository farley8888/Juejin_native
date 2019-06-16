package com.juejinchain.android.event;

/**
 * 跳转的vue内页
 */
public class ShowVueEvent {

    /** 文章详情，需要id参数 */
    public static String PAGE_ART_DETAIL = "/ArticleDetails";
    //网页浏览文章
    public static String PAGE_BROWSER = "/browser";
    public static String PAGE_LOGIN = "/login";
    /** 查看攻略 */
    public static String PAGE_LOCK_FAN = "/lockfanpage";
    /** 未读消息 */
    public static String PAGE_MESSAGE_LIST = "/my/message";

    public String page;

    public String param;

    public ShowVueEvent(String page, String param) {
        this.page = page;
        this.param = param;
    }

}
