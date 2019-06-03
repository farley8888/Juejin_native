package com.juejinchain.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juejinchain.android.base.BaseMainFragment;
import com.juejinchain.android.tools.L;
import com.juejinchain.android.ui.fragment.MainFragment;

import io.dcloud.common.DHInterface.ISysEventListener;
import io.dcloud.common.util.BaseInfo;
import io.dcloud.feature.internal.sdk.SDK;

public class WebAppFragment extends BaseMainFragment {

    static Context context;
    boolean doHardAcc = true;
    static MyEntryProxy mEntryProxy = null;
    static ViewGroup webFrame;
    private static String FNAME = "name";
    private String TAG = WebAppFragment.class.getSimpleName();

    public static WebAppFragment mFragment;
    public String currPage ;
    private WebappModeListener webModeListener;

//    private WebAppFragment(){
//    }

    public static WebAppFragment instance(String name){
        if (mFragment == null)  mFragment = new WebAppFragment();
//        WebAppFragment frgment = new WebAppFragment();
        Bundle args = new Bundle();
        args.putString(FNAME, name);
        mFragment.setArguments(args);
        mFragment.currPage = name;
        return mFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        System.out.println("baseWeb.onCreateView");
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        webFrame = (ViewGroup) view;  //container 不能用这个，切换后无法隐藏
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Log.d(TAG, "baseWeb.onActivityCreated: ");
        if (mEntryProxy == null) {
            // 创建5+内核运行事件监听
//            WebappModeListener wm = new WebappModeListener((Activity) context, webFrame,  getArguments().getString(FNAME));
//            // 初始化5+内核
//            mEntryProxy = new MyEntryProxy().init((Activity) context, wm);;
////            mEntryProxy.clearData2(); 不能在这清除
////            mEntryProxy.init((Activity) context, wm);
//            // 启动5+内核
//            boolean succ = mEntryProxy.onCreate((Activity) context, savedInstanceState, SDK.IntegratedMode.WEBAPP, null);
//            Log.d(TAG, "onActivityCreated: "+ getArguments().getString(FNAME) + "="+succ);
            createVue(savedInstanceState);
        }else {
            // 创建5+内核运行事件监听
            webModeListener = new WebappModeListener((Activity) context, webFrame, currPage);
            // 初始化5+内核
            mEntryProxy = new MyEntryProxy().init((Activity) context, webModeListener);
            // 启动5+内核
            mEntryProxy.onCreate((Activity) context, savedInstanceState, SDK.IntegratedMode.RUNTIME, null);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mEntryProxy.onActivityExecute((Activity) context, ISysEventListener.SysEventType.onActivityResult, new Object[] { requestCode, resultCode, data });
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.d(TAG, "onPause: ");
        mEntryProxy.onPause((Activity) context);
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && BaseInfo.mDeStatusBarBackground == -111111) {
            BaseInfo.mDeStatusBarBackground =  ((Activity) context).getWindow().getStatusBarColor();
        }
        mEntryProxy.onResume((Activity) context);
        showPage(currPage);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        Log.d(TAG, "onHiddenChanged: "+hidden);
        if (hidden){
//            mEntryProxy.clearData(); //不能清
//            mEntryProxy.onStop((Activity) context); //停止了

        }else if (mEntryProxy.isHasStop()){
            createVue(new Bundle());
        }
    }

    public void createVue(Bundle savedInstanceState){
        L.d(TAG, "createVue: ");
        //            FrameLayout f = new FrameLayout(context);
        // 创建5+内核运行事件监听
        webModeListener = new WebappModeListener((Activity) context, webFrame,getArguments().getString(FNAME));
        // 初始化5+内核
        mEntryProxy = new MyEntryProxy().init((Activity) context, webModeListener);
        // 启动5+内核
        mEntryProxy.onCreate((Activity) context, savedInstanceState, SDK.IntegratedMode.WEBAPP, null);
        //            setContentView(f);
    }

    @Override
    public boolean onBackPressedSupport() {
        MainFragment mainFragment = (MainFragment) getParentFragment();
        if (mainFragment.startVuePage){
            mainFragment.showHomeFragment();

            return true;
        }
        return super.onBackPressedSupport();
    }

    public void showPage(String page){

        webModeListener.switchPage(page, null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mEntryProxy.onStop((Activity)context);
    }

}

