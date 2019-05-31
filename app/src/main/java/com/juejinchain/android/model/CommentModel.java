package com.juejinchain.android.model;

import com.juejinchain.android.util.TimeUtils;

/**
 * 评论model
 */
public class CommentModel {

    public String cid;
    public int comment_time;

    public String content;
    public boolean is_fabulous;
    public int fabulous;

    public String nickname;
    public int reply;  //回复数
    public String avatar; //imgurl

    public String getComment_time() {
        return TimeUtils.getTimeLine(comment_time);
    }

    /////回复列表的
    private String aid; //文章ID 文章用
    public String vid; //视频ID 视频用
    private String rid; //回复id
    private int create_time;

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public int getCreate_time() {
        return create_time;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }
}
