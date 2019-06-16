package com.juejinchain.android.listener;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.juejinchain.android.MainActivity;
import com.juejinchain.android.MyApplication;

import java.util.List;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * 应用前后台状态监听帮助类，仅在Application中使用
 * Created by dway on 2018/1/30.
 */

public class AppFrontBackHelper {

    private OnAppStatusListener mOnAppStatusListener;


    public AppFrontBackHelper() {

    }

    /**
     * 注册状态监听，仅在Application中使用
     * @param application
     * @param listener
     */
    public void register(Application application, OnAppStatusListener listener){
        mOnAppStatusListener = listener;
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    public void unRegister(Application application){
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        //打开的Activity数量统计
        private int activityStartCount = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }


        @Override
        public void onActivityStarted(Activity activity) {
            Log.d("FrontBackHelper", "onActivityStarted: ");
            MyApplication.currentAct = activity;
            activityStartCount++;
            //数值从0变到1说明是从后台切到前台
            if (activityStartCount == 1){
                //从后台切到前台
                if(mOnAppStatusListener != null){
                    mOnAppStatusListener.onFront(activity);
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d("FrontBackHelper", "onActivityPaused: "+activity.getClass().getSimpleName());
        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityStartCount--;
            //数值从1到0说明是从前台切到后台
            if (activityStartCount == 0){
                //从前台切到后台
                if(mOnAppStatusListener != null){
                    mOnAppStatusListener.onBack(activity);
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.d("FrontBackHelper", "onActivitySaveInstanceState: ");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    public interface OnAppStatusListener{
        void onFront(Activity activity);
        void onBack(Activity activity);
    }

    /**
     * 判断应用是否是在后台
     * 方法过时，拿不到其它运行的应用
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            Log.d("FrontBackHelper", "isAppOnForeground: "+appProcess.processName);
            if (TextUtils.equals(appProcess.processName, context.getPackageName())) {
//                Log.d("onChangBack", " isBackground: "+ appProcess.importance );
                boolean isBackground = (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
                        && appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND ); //
                boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
                return  isLockedState || isBackground; //
            }
        }
        return false;
    }

    /**
     * 判断app是否处于前台
     * 方法过时，拿不到其它运行的应用
     * @return
     */
    public static boolean isAppOnForeground(Context ct) {

        ActivityManager activityManager = (ActivityManager) ct.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = ct.getApplicationContext().getPackageName();
        /**
         * 获取Android设备中所有正在运行的App
         */
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {

            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }


}

