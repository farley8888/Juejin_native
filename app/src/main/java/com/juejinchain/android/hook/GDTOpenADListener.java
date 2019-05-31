package com.juejinchain.android.hook;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juejinchain.android.IndexActivity;
import com.juejinchain.android.hook.ADOpenActivity;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

/**
 * 腾讯广点通开屏广告监听,X5加强版的
 */
public class GDTOpenADListener implements SplashADListener {

    private Activity indexActivity;

    private TextView skipView;
    private ImageView splashHolder;
    private static final String SKIP_TEXT = "跳过 %d";

    /**
     * 为防止无广告时造成视觉上类似于"闪退"的情况，设定无广告时页面跳转根据需要延迟一定时间，demo
     * 给出的延时逻辑是从拉取广告开始算开屏最少持续多久，仅供参考，开发者可自定义延时逻辑，如果开发者采用demo
     * 中给出的延时逻辑，也建议开发者考虑自定义minSplashTimeWhenNoAD的值（单位ms）
     **/
    private int minSplashTimeWhenNoAD = 3000;
    /**
     * 记录拉取广告的时间
     */
    public long fetchSplashADTime = 0;
    private Handler handler = new Handler(Looper.getMainLooper());


    public GDTOpenADListener(Activity act, TextView tv, ImageView img){
        skipView = tv;
        splashHolder = img;
        indexActivity = act;
    }


    @Override
    public void onADPresent() {
//        Log.i("AD_DEMO", "SplashADPresent");
        splashHolder.setVisibility(View.INVISIBLE); // 广告展示后一定要把预设的开屏图片隐藏起来
        skipView.setVisibility(View.VISIBLE);
        if (indexActivity instanceof IndexActivity){
//            ((IndexActivity)indexActivity).saveADOpenTime();
        }else{
            ((ADOpenActivity)indexActivity).saveADOpenTime();
        }

    }

    @Override
    public void onADClicked() {
        Log.i("AD_DEMO", "SplashADClicked");
    }

    /**
     * 倒计时回调，返回广告还将被展示的剩余时间。
     * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
     *
     * @param millisUntilFinished 剩余毫秒数
     */
    @Override
    public void onADTick(long millisUntilFinished) {
//        Log.i("AD_DEMO", "SplashADTick " + millisUntilFinished + "ms");
        skipView.setText(String.format(SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
    }

    @Override
    public void onADExposure() {
        Log.i("AD_DEMO", "SplashADExposure");
    }

    @Override
    public void onADDismissed() {
//        Log.i("AD_DEMO", "SplashADDismissed");
        if (indexActivity instanceof IndexActivity){
            ((IndexActivity)indexActivity).startVueWebView();
        }else {
            indexActivity.finish();
        }
    }

    @Override
    public void onNoAD(AdError error) {
//        Log.i("AD_DEMO", String.format("LoadSplashADFail, eCode=%d, errorMsg=%s", error.getErrorCode(), error.getErrorMsg()));
        /**
         * 为防止无广告时造成视觉上类似于"闪退"的情况，设定无广告时页面跳转根据需要延迟一定时间，demo
         * 给出的延时逻辑是从拉取广告开始算开屏最少持续多久，仅供参考，开发者可自定义延时逻辑，如果开发者采用demo
         * 中给出的延时逻辑，也建议开发者考虑自定义minSplashTimeWhenNoAD的值
         **/
        long alreadyDelayMills = System.currentTimeMillis() - fetchSplashADTime;//从拉广告开始到onNoAD已经消耗了多少时间
        long shouldDelayMills = alreadyDelayMills > minSplashTimeWhenNoAD ? 0 : minSplashTimeWhenNoAD
                - alreadyDelayMills;//为防止加载广告失败后立刻跳离开屏可能造成的视觉上类似于"闪退"的情况，根据设置的minSplashTimeWhenNoAD
        // 计算出还需要延时多久
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (indexActivity instanceof IndexActivity){
                    ((IndexActivity)indexActivity).startVueWebView();
                }else {
                    indexActivity.finish();
                }
//                SplashActivity.this.finish();
            }
        }, shouldDelayMills);
    }


}
