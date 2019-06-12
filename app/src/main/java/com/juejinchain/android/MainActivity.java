package com.juejinchain.android;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.juejinchain.android.ui.dialog.HomeTipsAlertDialog;
import com.juejinchain.android.ui.fragment.MainFragment;

import io.dcloud.common.DHInterface.ISysEventListener;
import me.yokeyword.fragmentation.SupportActivity;

/**
 *
 */
public class MainActivity extends SupportActivity {

    public MainFragment mainFragment;
    ImageView launchImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        startSplashAnim();

        if (findFragment(MainFragment.class) == null) {
            mainFragment =MainFragment.newInstance();
            loadRootFragment(R.id.frameLayout, mainFragment);
        }

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
        alphaAnimation.setDuration(2000);//持续时间
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
     * 显示车大礼包、设置全局背景色
     */
    void showGiftDialogAndSetWindowBackground(){
        new HomeTipsAlertDialog(MainActivity.this).show();
        getWindow().getDecorView().setBackgroundResource(R.color.default_bg);
    }

//    @Override
//    public void onFragmentInteraction(Uri uri) {
//
//    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getFlags() != 0x10600000) {// 非点击icon调用activity时才调用newintent事件
            mainFragment.webAppFragment.mEntryProxy.onNewIntent(this, intent);
        }
    }


    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        try {
            int temp =  getResources().getConfiguration().orientation;
            if (mainFragment.webAppFragment.mEntryProxy != null) {
                mainFragment.webAppFragment.mEntryProxy.onConfigurationChanged(this, temp);
            }
            super.onConfigurationChanged(newConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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


}
