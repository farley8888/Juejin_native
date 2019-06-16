package com.juejinchain.android.model;

public class NewsModel {

    public String id;
    public String mid;
    public String title;
    public String comments;

    /**
     * 1为小图，2三图，3为大图,4为无图
     */
    public int type;

    //0：正常 1：置顶 2：热
    public int is_top;

    //前提is_top=0; 1微热；2：热文；3：爆文
    public int read_mark;

    /**
     * 前提is_top=0; 1小议；2：热议；3：爆评
     * 可能和read_mark同时显示，用空格隔开
     */
    public int comment_mark;

    public String getIs_top() {
        if (is_top == 0) {
            String isTopStr = "";
            if (read_mark == 1){
                isTopStr = "微热";
            }else if (read_mark == 2){
                isTopStr = "热文";
            }else if (read_mark == 3){
                isTopStr = "爆文";
            }

            if (isTopStr.length()>0) isTopStr += " ";

            if (comment_mark == 1){
                isTopStr += "小议";
            }else if (comment_mark == 2){
                isTopStr += "热议";
            }else if (comment_mark == 3){
                isTopStr += "爆评";
            }

            return isTopStr;

        }else if (is_top == 1){
            return "置顶";
        }else if (is_top == 2){
            return "热";
        }
        return "";

    }

    public int read_num;

    public boolean is_follow;

    public long publish_time;

    public String publish_time_date; //2019-03-18 10:35:07

    public String author;
    public String author_logo;
    //来源
    public String source;
    /**
     * 多图数组
     */
    public String[] img_url;

    /**
     * author 为掘金宝时，可能有这个跳转地址
     * 1、不为空时详情加载此url
     * 2、author==掘金宝，&& 为空时，详情不加载推荐列表！
     */
    public String redirect_url;

    public String getPublish_time() {
        if (publish_time_date == null || publish_time_date.length() < 8) return "";
        return publish_time_date.substring(5,publish_time_date.length()-3);
    }

    public String getRead_num() {
        if (read_num > 10000){
            return String.format("阅读 %.2fw", read_num/10000.0);
        }else {
            return "阅读 " + read_num;
        }
    }
}
