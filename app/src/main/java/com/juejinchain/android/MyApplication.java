package com.juejinchain.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.juejinchain.android.hook.ADOpenActivity;
import com.juejinchain.android.hook.HookActivityHelper;
import com.juejinchain.android.listener.AppFrontBackHelper;
import com.juejinchain.android.listener.HomeListener;
import com.juejinchain.android.listener.PowerListener;
import com.juejinchain.android.network.SpUtils;

import java.lang.reflect.Method;

import io.dcloud.WebAppActivity;
import io.dcloud.application.DCloudApplication;
import io.dcloud.common.adapter.util.Logger;
import io.dcloud.common.adapter.util.UEH;
import io.dcloud.common.util.BaseInfo;
import io.dcloud.common.util.PdrUtil;

//不能用 Application，因为用的是arr打包
public class MyApplication extends DCloudApplication {

    private static final String a = MyApplication.class.getSimpleName();
    public boolean isQihooTrafficFreeValidate = true;
    private static MyApplication b;
    private static Context c = null;
    public static Activity currentAct;

    private HomeListener mHomeListen = null;
    private PowerListener mPowerListen ;
    //只有home和电源键回来的才会显示开屏广告
    private boolean backHome;


    public MyApplication() {
    }

    public static Context getInstance() {
        return c;
    }

    public static void setInstance(Context var0) {
        if (c == null) {
            c = var0;
        }

    }

    public static MyApplication self() {
        return b;
    }

    public void onCreate() {
        super.onCreate();
        c = getApplicationContext();

        /**
         * 开屏广告启动场景
         * 1、每次启动APP都展示，限定时间内最多三次
         * 2、启动APP后，间隔（10~30分钟）后再次打开会展示
         */
        //监听activity创建时启动广告
        HookActivityHelper.get().open();

        //监听重新回到前台后启动广告, 通过home和电源键处理，不能单凭前后台，因为分享回来也会触发前后台
        setActivityListener();
        addHomeListener();

        Object obj = this;
        if (obj instanceof DCloudApplication){

            PdrUtil.closeAndroidPDialog();
            BaseInfo.initWeex(this);
            io.dcloud.a.a(this);
            b = this;
            BaseInfo.sAppIsTests.init(this.getBaseContext());
            setInstance(this.getApplicationContext());
            UEH.catchUncaughtException(this.getApplicationContext());
        }

    }

    protected void attachBaseContext(Context var1) {
        super.attachBaseContext(var1);
        if (Build.VERSION.SDK_INT < 21) {
            this.a();
        }
    }

    void addHomeListener(){
        mHomeListen = new HomeListener(c);
        mHomeListen.setInterface(new HomeListener.KeyFun() {
            @Override
            public void home() {
                Log.d(a, "home: ");
                backHome = true;
            }

            @Override
            public void recent() {
//                Log.d(a, "recent: ");
            }
            @Override
            public void longHome() {
//                Log.d(a, "longHome: ");
            }
        });
        mHomeListen.startListen();

        mPowerListen = new PowerListener(c);
        mPowerListen.setHomeKeyListener(new PowerListener.OnPowerKeyListener() {
            @Override
            public void onPowerKeyPressed() {
//                Log.d(a, "onPowerKeyPressed: ");
                backHome = true;
            }
        });
        mPowerListen.startListen();
    }


    private void setActivityListener(){

        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront(Activity activity) {
                //应用切到前台处理
//                Log.d(a, "onChangFront: " + activity.getClass().getSimpleName());
                mHomeListen.startListen();
                mPowerListen.startListen();
                if (backHome){
                    backHome = false;
                    long last = (long) SpUtils.get(activity.getApplicationContext(), ADOpenActivity.LAST_OPEN_TIME, 0L);
                    if (System.currentTimeMillis() - last < ADOpenActivity.INTERVAL_TIME) {
                        return;
                    }
                    /*
                     * PandoraEntryActivity 这个才是启动Vue的, 父类为：WebAppActivity
                     */
                    if (activity instanceof WebAppActivity){
//                    System.out.println("onHookActivityCreated: ");
                        Intent intent = new Intent(activity, ADOpenActivity.class);
                        activity.startActivity(intent);
                    }
                }

            }

            @Override
            public void onBack(Activity activity) {
                //应用切到后台处理, 跳到第三方分享平台也会触发此方法
//                Log.d(a, "onChangBack: " );
                mHomeListen.stopListen();
                mPowerListen.stopListen();
            }
        });


    }

    public void onLowMemory() {
        super.onLowMemory();
        Logger.e(a, "onLowMemory" + Runtime.getRuntime().maxMemory());
    }

    public void onTrimMemory(int var1) {
        super.onTrimMemory(var1);
        Logger.e(a, "onTrimMemory");
    }

    protected void a() {
        try {
            Class var1 = Class.forName("android.support.multidex.MultiDex");
            Method var2 = var1.getMethod("install", Context.class);
            var2.invoke((Object)null, this);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }
}
