package com.juejinchain.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.juejinchain.android.config.TTAdManagerHolder;
import com.juejinchain.android.util.TToast;

import java.util.List;
//测试信息流接口
public class Main2Activity extends AppCompatActivity {

    private TTAdNative mTTAdNative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //初始化广告管理对象
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());
        //申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
        loadListAd();

    }

    //加载穿山甲信息流广告
    void loadListAd() {
        //feed广告请求类型参数
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("920793469")
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 320)
                .setAdCount(3)
                .build();

        //调用feed广告异步请求接口
        mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(Main2Activity.this, "穿山甲异常：" + message);
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    TToast.show(Main2Activity.this, "on FeedAdLoaded: ad is null!");
                    return;
                }

                Log.d("baseWebApp", "onFeedAdLoad: " + ads);
//                int count = mData.size();
//                for (TTFeedAd ad : ads) {
//                    int random = (int) (Math.random() * count);
//                    mData.set(random, ad);
//                }
            }
        });
    }


}
