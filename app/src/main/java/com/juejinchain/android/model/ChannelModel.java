package com.juejinchain.android.model;

import java.util.Objects;

public class ChannelModel {

    /**
     * 频道id, 推荐为0，热门1，其它用接口的
     */
    private String id;
    private String name;

    public ChannelModel(){

    }

    public ChannelModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
