package com.juejinchain.android.network.callBack;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

import okhttp3.Response;

public abstract class JSONCallback extends Callback<JSONObject> {

    @Override
    public JSONObject parseNetworkResponse(Response response) throws IOException {
        return JSON.parseObject(response.body().string());
    }
}
