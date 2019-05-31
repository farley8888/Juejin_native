package com.juejinchain.android.model;

public class VideoModel {

    public static final int typeHead = 0;
    public static final int typeChild = 1;
    public static final int typeChild2 = 2;

    private int type;

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String id;

    public String url;    //

    public String title;   //
//public int publish_time;    //

    /**
     * 抽象标题
     * json为关键字abstract，加个2区分
     * 在setAbstract()方法内赋值
     */
    public String abstract2;

    public String author;   //
    public String author_logo;    //

    public int comment_count;    //
    public int video_watch_count;    //
    public int video_like_count;    //
    public String source;   //
    public int video_duration;    //
    public boolean is_fabulous;    //

    public String video_id;   //
    /**
     * 通过video_id解析获取的
     */
    public String src;
//public String category_cn;   //
//public String category_en;   //
    public String img_url;   // webp格式的
    public String large_img_url;   //

    public String getPlayTimes() {
        if (video_watch_count> 1000){
            return String.format("%.2fk", video_watch_count/1000.0);
        }else if (video_watch_count > 0 ){
            return video_watch_count + "";
        }
        return "";
    }

    public String getPlayTimesStr(){
        String temp = getPlayTimes();
        return temp.length()>0 ? temp+"次播放" : "";
    }

    public String getVideo_duration() {
        int min = video_duration/60;
        if (min > 60){
            int h = min/60;
            return String.format("%d:%02d:%02d", h, min, video_duration%60);
        }
        int second = video_duration%60;
        return String.format("%02d:%02d", min, second);
    }

    public void setAbstract(String abstr) {
        this.abstract2 = abstr;
    }

}
