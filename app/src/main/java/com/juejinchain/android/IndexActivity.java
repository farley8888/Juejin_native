package com.juejinchain.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.RelativeLayout;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.juejinchain.android.network.HttpsUtils;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.OkHttpUtils;
import com.juejinchain.android.network.callBack.Callback;
import com.juejinchain.android.network.callBack.JSONCallback;

import java.util.HashMap;
import java.util.Map;

import io.dcloud.PandoraEntry;
import io.dcloud.PandoraEntryActivity;
import io.dcloud.WebAppActivity;
import okhttp3.Call;

public class IndexActivity extends Activity {

    static Context context ;

    private static final String TAG = IndexActivity.class.getName();
    private RelativeLayout splash;
    private boolean canJump = false;
    private boolean connectTimeout = true;


    public IndexActivity() {
    }

    @Override
    protected void onCreate(Bundle var1) {
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(var1);
        context = this.getApplicationContext();

        if (NetConfig.CHANNEL.equals("vivo")){
            requestApiAddAd();
        }else {
            addWuganAD();
        }

//        startVueWebView(); 不能在这启动SDK_WebApp，第二次打不开！

        //使用了ADOPenActivity启动开屏广告，本类直接启动vue就好
        startVueWebView();

    }

    void requestApiAddAd(){

        Map<String, String> map = new HashMap<>();
        map.put("vsn", "1.8.2");
        map.put("channel", NetConfig.CHANNEL);
        //http://api.juejinchain.com/v1/system/menu?channel=vivo&vsn=1.8.2
        String url = NetConfig.getUrlByParams(map, NetConfig.API_SYSTEM_MENU);
//        Log.d(TAG, "requestApiAddAd: "+url);
        OkHttpUtils.getAsyn(url, new JSONCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: "+response);
                JSONArray showArr = response.getJSONArray("data");
                for (int i = 0; i< showArr.size(); i++) {
                    JSONObject jo = showArr.getJSONObject(i);
                    //看影视是否开启
                    if (jo.getString("to").contains("movie")){
                        if(jo.getInteger("show") == 1){
//                            Log.d(TAG, "onResponse: 影视打开了");
                            addWuganAD();
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * 初始化无感广告
     */
    void addWuganAD() {

//        if (Build.VERSION.SDK_INT < 28){ //28为9.0的系统
        // 无感广告：蓝莓互娱(米蝶，被9.0系统检测为风险软件！这样开始没提示，但用一段时间就不行了
//        com.thc.std.dc.dc.mpv.Mad.init(context,  "WG20190325165023");
////        }
//
//        //无感广告：优效，被360检测为可疑软件！！
//        com.ssp.brush.core.BrushSDK.init(context, new com.ssp.brush.core.InitCallback() {
//            @Override
//            public void callback(int code, String msg) {
////                Log.d(TAG, "优效无感callback: "+ (code==0?"成功":"失败"));
//                if (code != 0)  Log.e(TAG, "callback: error ="+ msg);
//            }
//        });
//
//        /*
//         * 移动基地无感
//         * channel 去除vivo，
//         * 7.0以下系统，
//         * 必须是有SIM卡用户，且为移动用户
//         */
//        if (Build.VERSION.SDK_INT < 26)
//            com.android.aucm.a.a.b(context, "HUA_WEI");

    }

    public void startWebAppActivity(){
        Intent intent = this.getIntent();
        intent.setClass(context, SDK_WebApp.class);
        this.startActivity(intent);

        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 1000L);
    }

    public void startVueWebView(){
        Intent var2 = this.getIntent();
        boolean var3 = false;

        try {
            var3 = var2.getBooleanExtra("is_stream_app", var3);
        } catch (Exception var5) {
            var5.printStackTrace();
            finish();
            return;
        }

        if (var3) {
            var2.setClass(this, WebAppActivity.class);
//            var2.setClass(this, MyWebAppActivity.class);
            var2.putExtra("is_stream_app", true);
            Log.d("indexActivity", "to WebAppActivity");
        } else {
            var2.putExtra("short_cut_class_name", PandoraEntry.class.getName());
            var2.setClass(this, PandoraEntryActivity.class);
            Log.d("indexActivity", "to PandoraEntryActivity");
        }

        this.startActivity(var2);
//        Intent intent = new Intent(context, ADOpenActivity.class);
//        this.startActivity(intent); //这里启动广告也行，但vueActivity 还是会等待显示才加载

        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 2000L);
    }


    @Override
    protected void onResume() {
        //判断是否该跳转到主页面
        super.onResume();
//        Log.d(TAG, "mYonResum: " +canJump);
        if (canJump) {
//            nextTwo();
            startVueWebView();
        }
        canJump = false;
    }


    @Override
    protected void onPause() {
        super.onPause();
//        Log.d(TAG, "onPause: ");
        canJump = true;
    }

    /** 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
