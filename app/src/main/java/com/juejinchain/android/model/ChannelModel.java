package com.juejinchain.android.model;

import java.util.Objects;

public class ChannelModel {

    //推荐
    public static final int ID_RECOMMEND = 0;
    //热门
    public static final int ID_HOT = 1;
    /** 掘金宝频道id */
    public static final int ID_JJB = 101;

    private int id;
    private String name;

    public ChannelModel(){

    }

    public ChannelModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ChannelModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ChannelModel){
            ChannelModel ob = (ChannelModel) obj;
            return Objects.equals(this.id, ob.id) && Objects.equals(this.name, ob.name);
        }
        return false;
    }
}
