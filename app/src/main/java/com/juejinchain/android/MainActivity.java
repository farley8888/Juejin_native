package com.juejinchain.android;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;

import com.juejinchain.android.ui.fragment.MainFragment;

import io.dcloud.common.DHInterface.ISysEventListener;
import me.yokeyword.fragmentation.SupportActivity;

/**
 *
 */
public class MainActivity extends SupportActivity {

    public MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        if (findFragment(MainFragment.class) == null) {
            mainFragment =MainFragment.newInstance();
            loadRootFragment(R.id.frameLayout, mainFragment);
        }
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
