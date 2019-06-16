package com.juejinchain.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.juejinchain.android.H5Plugin.MyPlugin;
import com.juejinchain.android.base.BaseMainFragment;
import com.juejinchain.android.event.CallVueBackEvent;
import com.juejinchain.android.event.CallVueBackIndexEvent;
import com.juejinchain.android.event.SaveArticleEvent;
import com.juejinchain.android.event.ShareEvent;
import com.juejinchain.android.model.ShareModel;
import com.juejinchain.android.model.UserModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.tools.L;
import com.juejinchain.android.ui.fragment.MainFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import io.dcloud.common.DHInterface.ISysEventListener;
import io.dcloud.common.adapter.ui.ReceiveJSValue;
import io.dcloud.common.util.BaseInfo;
import io.dcloud.feature.internal.sdk.SDK;

public class WebAppFragment extends BaseMainFragment {

    Context context;
//    boolean doHardAcc = true;
    MyEntryProxy mEntryProxy = null;
    public FrameLayout webFrame;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webvue, container, false);
        webFrame = (FrameLayout) view;  //container 不能用这个，切换后无法隐藏
        EventBus.getDefault().register(this);
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
        showPage(currPage, null);

    }


    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (NetConfig.SHARE_BASE_HREF == null){
            getShareHref();
        }
    }

    private void getShareHref() {
        NetUtil.getRequest(NetConfig.API_SYSTEM_HREF, null, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (NetUtil.isSuccess(response)){
                    response = response.getJSONObject("data");
                    NetConfig.SHARE_BASE_HREF = response.getJSONArray("domain").getString(0);
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        Log.d(TAG, "onHiddenChanged: "+hidden);
        if (hidden){
//            mEntryProxy.clearData(); //不能清
//            mEntryProxy.onStop((Activity) context); //停止了
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
        mEntryProxy.onCreate((Activity) context, savedInstanceState, SDK.IntegratedMode.RUNTIME, null);
        //            setContentView(f);
    }

    @Subscribe()
    public void onCallShareEvent(ShareEvent event){
        ShareModel shareModel = event.shareModel;
        L.d(TAG, "onCallShareEvent: "+shareModel.name);

        Map<String, String> param = new HashMap<>();
        param.put("tag", shareModel.tag);
        param.put("url", NetConfig.BaseUrl+"/"+shareModel.tag);
        NetUtil.getRequestShowLoading(NetConfig.API_ShareCopy, param, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject data = response.getJSONObject("data");

                JSONObject shareJsonParam = new JSONObject();
                shareJsonParam.put("content", data.getString("desc"));
                shareJsonParam.put("title", data.getString("title"));
                shareJsonParam.put("thumbs", data.getString("img_url"));
                //%2F是‘/’的url转义
                String href = "";
                /**
                 * path 视频 = VideoDetail/id
                 * path 首页 = /
                 * path 文章 = Article/id
                 */
                href = String.format("%s/?path=%s&inviteCode=%s",
                        NetConfig.SHARE_BASE_HREF, event.typePath , UserModel.getInvitation());

                shareJsonParam.put("href", href);

                webModeListener.shareTo(shareModel.way, shareJsonParam.toJSONString(), shareModel.tag);
            }
        }, false);
    }

    @Subscribe()
    public void saveArticleEvent(SaveArticleEvent event){
        L.d(TAG, "saveArticleEvent: ");
        webModeListener.saveReadArticle(JSON.toJSONString(event.model));
    }

    @Subscribe()
    public void  callVueBack(CallVueBackEvent event){
        webModeListener.callVueBack();
    }

    @Subscribe()
    public void  callBackIndex(CallVueBackIndexEvent event){
        webModeListener.backVueIndex();
    }

    @Override
    public boolean onBackPressedSupport() {
        MainFragment mainFragment = (MainFragment) getParentFragment();

        if (!MyPlugin.isOnHomePage()){
            //这个是vue提供的返回方法
//            webModeListener.callVueBack();

            //DCloud框架的返回方法
            boolean _ret = mEntryProxy != null && mEntryProxy.onActivityExecute(getActivity(), ISysEventListener.SysEventType.onKeyUp, new Object[]{KeyEvent.KEYCODE_BACK, null});
//            if(!_ret && mEntryProxy != null)
//                mEntryProxy.destroy(this);

            return true;
        }

        if (mainFragment.showVuePageFromNative){
            mainFragment.showHomeFragment();
            return true;
        }
//        return super.onBackPressedSupport();
        return false;
    }

    public void showPage(String page , @Nullable ReceiveJSValue.ReceiveJSValueCallback jsCallback){
        //启动前不能调用两次vue，会导致vue强制显示成第一个fragment
        webModeListener.switchPage(page, jsCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mEntryProxy.onStop((Activity)context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}

