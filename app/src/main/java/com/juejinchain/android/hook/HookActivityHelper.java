package com.juejinchain.android.hook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.juejinchain.android.network.SpUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.dcloud.WebAppActivity;

/**
 * @author 黄兴伟 (xwd9989@gamil.com)
 * @since 2018/5/9
 */
public class HookActivityHelper {

    private static final HookActivityHelper INSTANCE = new HookActivityHelper();
    private ProxyInstrumentation mProxyInstrumentation;
    boolean alwaysStart ;

    public static HookActivityHelper get() {
        INSTANCE.alwaysStart = false;
        return INSTANCE;
    }


    public void open() {
        mProxyInstrumentation.setActivityCreateListener(new ProxyInstrumentation.OnActivityCreateListener() {
            @Override
            public void onHookActivityCreated(Activity activity, Bundle icicle) {
//                System.out.println("HookActivityHelper class = "+ activity.getClass().getSimpleName());

                long last = (long) SpUtils.get(activity.getApplicationContext(), ADOpenActivity.LAST_OPEN_TIME, 0L);
//
                if (System.currentTimeMillis() - last < ADOpenActivity.INTERVAL_TIME) {
                    int launchTime = (int) SpUtils.get(activity.getApplicationContext(), ADOpenActivity.OPEN_TIMES_OF_LAUNCH, (Integer)0);
//                    System.out.println( "onCreate: lastOpenTimes = " + launchTime);
                    if (launchTime >= ADOpenActivity.LAUNCH_LIMIT_OF_LAUNCH){
                        return;  //如：启动APP每次都显示，注释这行
                    }
                }else {
                    //超过限定时间，启动次数置零
                    SpUtils.put(activity.getApplicationContext(), ADOpenActivity.OPEN_TIMES_OF_LAUNCH, 0);
                }

                /*
                 * PandoraEntryActivity 这个才是启动Vue的, 父类为：WebAppActivity
                 * IndexActivity是引导的！
                 */
//                if (alwaysStart) return; //防止vue框架启动两次
                if (activity instanceof WebAppActivity){
                    Intent intent = new Intent(activity, ADOpenActivity.class);
                    intent.putExtra("launch", true);
                    activity.startActivity(intent);
                    alwaysStart = true;
                }
            }
        });
    }


    private HookActivityHelper() {
        try {
            Class<?> clazz = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = clazz.getDeclaredMethod("currentActivityThread");
            Object object = currentActivityThread.invoke(null);


            Field field = clazz.getDeclaredField("mInstrumentation");
            field.setAccessible(true);
            mProxyInstrumentation = new ProxyInstrumentation();
            field.set(object, mProxyInstrumentation);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
