package com.juejinchain.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.juejinchain.android.config.TTAdManagerHolder;
import com.juejinchain.android.tools.L;
import com.juejinchain.android.ui.fragment.MainFragment;
import com.juejinchain.android.tools.TToast;

import java.util.List;

import io.dcloud.common.DHInterface.IActivityHandler;
import io.dcloud.common.DHInterface.IApp;
import io.dcloud.common.DHInterface.ICallBack;
import io.dcloud.common.DHInterface.ISysEventListener;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.feature.internal.reflect.BroadcastReceiver;
import me.yokeyword.fragmentation.SupportActivity;

/**
 *
 */
public class MainActivity extends SupportActivity implements IActivityHandler {

    private final String TAG = MainActivity.class.getSimpleName();
    public MainFragment mainFragment;
    ImageView launchImg;
    public static TTAdNative mTTAdNative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        L.d(TAG, "onCreate: ");
        startSplashAnim();
        /**
         * 每次创建都重新加载所有fragment,
         * 否则子页闪退回来不为null时，部分频道数据丢失导致无法正常加载列表数据
         * 或nullPointer闪退
         */
//        if (findFragment(MainFragment.class) == null) {
            L.d(TAG, "onCreate: MainFragment.newInstance");
            mainFragment =MainFragment.newInstance();
            loadRootFragment(R.id.frameLayout, mainFragment);
//        }


        //初始化广告管理对象
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());

        //申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
//        loadListAd();
    }

    private void startSplashAnim(){
        ViewGroup vg = findViewById(R.id.frameLayout);
//        vg.setVisibility(View.GONE);

        launchImg = findViewById(R.id.img_launch);
        launchImg.setVisibility(View.GONE); //可以不用显示，因为在style已经设置了启动图
//        launchImg.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 2500);

        // 此动画不够理想
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.8f);//透明度从
        alphaAnimation.setDuration(1500);//持续时间
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                launchImg.setVisibility(View.GONE);
                vg.setVisibility(View.VISIBLE);
                showGiftDialogAndSetWindowBackground();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        launchImg.startAnimation(alphaAnimation);

    }

    /**
     * 首次启动
     * 设置全局背景色
     */
    void showGiftDialogAndSetWindowBackground(){
        //不能在这显示车大礼包，因为有延时，用户可能已进入到登录页
//        new HomeTipsAlertDialog(MainActivity.this).show();
        //无效？！
        getWindow().getDecorView().setBackgroundResource(R.color.default_bg);
    }

//    @Override
//    public void onFragmentInteraction(Uri uri) {
//
//    }

    @SuppressLint("WrongConstant")
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: ");
        if (intent.getFlags() != 0x10600000) {// 非点击icon调用activity时才调用newintent事件
            mainFragment.webAppFragment.mEntryProxy.onNewIntent(this, intent);
        }
    }

////重写会影响jzpl的全屏播放
//    public void onConfigurationChanged(Configuration newConfig) {
//        if (newConfig.fontScale != 1)//非默认值
//            getResources();
//        try {
//            int temp =  getResources().getConfiguration().orientation;
//            if (mainFragment.webAppFragment.mEntryProxy != null) {
//                mainFragment.webAppFragment.mEntryProxy.onConfigurationChanged(this, temp);
//            }
//            super.onConfigurationChanged(newConfig);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mainFragment.webAppFragment.mEntryProxy.onActivityExecute(this, ISysEventListener.SysEventType.onActivityResult, new Object[] { requestCode, resultCode, data });
    }

    @Override
    public void onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport();
    }

    //系统字体变大导致的布局异常
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

//    @Override
//    public FragmentAnimator onCreateFragmentAnimator() {
//        // 设置横向(和安卓4.x动画相同)
//        return new DefaultHorizontalAnimator();
//    }


    //解决vue视频播放全屏问题 implement IActivityHandler
    public void resumeAppStreamTask(String paramString) {}

    public void addAppStreamTask(String paramString1, String paramString2) {}

    public boolean raiseFilePriority(String paramString1, String paramString2) {
        return false;
    }

    public boolean queryUrl(String paramString1, String paramString2) {
        return false;
    }

    public void bindDCloudServices() {}

    public void unbindDCloudServices() {}

    public Intent registerReceiver(BroadcastReceiver paramBroadcastReceiver, IntentFilter paramIntentFilter) {
        Intent localIntent = null;
        return  localIntent;
    }

    public void unregisterReceiver(BroadcastReceiver paramBroadcastReceiver) {}

    public boolean isStreamAppMode() {
        return false;
    }

    public boolean isMultiProcessMode() {
        return false;
    }

    public void closeAppStreamSplash(String paramString) {}

    public void setSplashCloseListener(String paramString, ICallBack paramICallBack) {}

    public void showSplashWaiting() {}

    public void updateSplash(String paramString) {}

    public void setViewAsContentView(View paramView, FrameLayout.LayoutParams paramLayoutParams) {}

    public void setWebViewIntoPreloadView(View paramView) {}

    public void downloadSimpleFileTask(IApp paramIApp, String paramString1, String paramString2, String paramString3) {}

    public Context getContext() {
        return  getBaseContext();
    }

    public void commitPointData0(String paramString1, int paramInt1, int paramInt2, String paramString2) {}

    public void commitPointData(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4, String paramString5) {}

    public void commitActivateData(String paramString1, String paramString2) {}

    public String getUrlByFilePath(String paramString1, String paramString2) {
        return  "";
    }

    public void updateParam(String paramString, Object paramObject) {}

    public FrameLayout obtainActivityContentView() {
//        return rootView;
        return (FrameLayout) this.mainFragment.webAppFragment.webFrame;
    }

    public void setSideBarVisibility(int paramInt) {}

    public void closeSideBar() {}

    public void sideBarShowMenu(String paramString1, String paramString2, IWebview paramIWebview, String paramString3) {}

    public void sideBarHideMenu() {}

    public boolean hasAdService() {
        return  false;
    }

//    public FrameLayout rootView = null;


}
