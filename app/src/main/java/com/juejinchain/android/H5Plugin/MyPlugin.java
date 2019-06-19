package com.juejinchain.android.H5Plugin;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.android.dingtalk.share.ddsharemodule.DDShareApiFactory;
import com.android.dingtalk.share.ddsharemodule.IDDShareApi;
import com.android.dingtalk.share.ddsharemodule.message.SendAuth;
import com.baidu.api.Baidu;
import com.baidu.api.BaiduDialog;
import com.baidu.api.BaiduDialogError;
import com.baidu.api.BaiduException;
import com.juejinchain.android.MainActivity;
import com.juejinchain.android.ddshare.DDShareActivity;
import com.juejinchain.android.ddshare.DDShareUtil;
import com.juejinchain.android.event.ShareCallbackEvent;
import com.juejinchain.android.model.UserModel;
import com.juejinchain.android.tools.L;
import com.juejinchain.android.ui.fragment.MainFragment;
import com.juejinchain.android.util.Constant;
import com.juejinchain.android.util.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.StandardFeature;
import io.dcloud.common.util.JSUtil;

public class MyPlugin extends StandardFeature {

    //vue tab页 urlName
    public static final String VP_MINE = "/personal_center";
    public static final String VP_MOVIE = "/movie";
    public static final String VP_MakeMoney = "/make_money";
    public static final String VP_TASK = "/task";

    /** 当前显示的vue页面 */
    public static String CurrVuePage;
    /**
     * 调用vue返回方法次数
     * 超过2两次没触发插件的返回方法，强制显示原生首页
     */
    public static int VueCallBackTimes;

    /**所有首页tab */
    public static List<String> TAB_PAGES_OF_VUE;

    Context ctx;
    /**
     * 百度登录
     * 掘金宝 API Key
     */
    final String BaiduClientId = "XIKkAdb4je1yQTb3A8iAh5io";
    private Baidu mBaidu;
    private String TAG = MyPlugin.class.getSimpleName();

    private IDDShareApi iddShareApi ;

    int jumpVuePage; //记录跳转到vue的页数

    static {
        TAB_PAGES_OF_VUE = new ArrayList<>();
        TAB_PAGES_OF_VUE.add(VP_MINE);
        TAB_PAGES_OF_VUE.add(VP_TASK);
        TAB_PAGES_OF_VUE.add(VP_MOVIE);
        TAB_PAGES_OF_VUE.add(VP_MakeMoney);
    }

    //是否在tab页
    public static boolean isOnHomePage(){
        return TAB_PAGES_OF_VUE.contains(CurrVuePage);
    }

    public void PluginTestFunction(IWebview pWebview, JSONArray array)
    {
        // 原生代码中获取JS层传递的参数，
        // 参数的获取顺序与JS层传递的顺序一致
        String CallBackID = array.optString(0);
        JSONArray newArray = new JSONArray();
        newArray.put(array.optString(1));
        newArray.put(array.optString(2));
        newArray.put(array.optString(3));
        newArray.put(array.optString(4));
        // 调用方法将原生代码的执行结果返回给js层并触发相应的JS层回调函数
//        JSUtil.execCallback(pWebview, CallBackID, newArray, JSUtil.OK, false);

        ctx = pWebview.getContext();
//        ctx.startActivity(new Intent(ctx, WebAppActivity.class));

        //不能启动AppCompatActivity ！！??
//        ctx.startActivity(new Intent(ctx, LoginActivity.class));
//        Toast.makeText(ctx, "toast.test="+ array.optString(1), Toast.LENGTH_SHORT).show();

    }

    /**
     * 授权登录
     * @param pWebview
     * @param array way:baidu,dingding
     */
    public void authorLogin(IWebview pWebview, JSONArray array){
        String CallBackID = array.optString(0);
        String way = array.optString(1);
        L.d(TAG, "authorLogin: "+way);
        if (way.equals("baidu")){
            baiduAuthor(pWebview, CallBackID);
        }else if (way.equals("dingding")){
            sendAuthDingDing(pWebview, CallBackID);
        }
    }

    /**
     * 分享到
     * @param pWebview
     * @param array
     */
    public void shareTo(IWebview pWebview, JSONArray array){
        String CallBackID = array.optString(0);
        String way = array.optString(1);
        L.d(TAG, "shareTo: array ="+array.toString());
        JSONObject json = array.optJSONObject(2);
        String title = json.optString("title");

        L.d(TAG, "shareTo: "+title);
         if (way.equals("dingding")) {
             if (checkDDAndInitApi(pWebview, CallBackID, false)) {
                 new DDShareUtil(iddShareApi).sendWebPageMessage(false, json);
             }

         }
    }

    //获取百度授权登录accessToken
    private void baiduAuthor(final IWebview pWebview, final String callBackid){
        mBaidu = new Baidu(BaiduClientId, pWebview.getContext());
//        mBaidu.clearAccessToken(); 注销
        mBaidu.authorize(pWebview.getActivity(), true, false, new BaiduDialog.BaiduDialogListener() {

            String TAG = "BaiduAuthorListener";
            @Override
            public void onComplete(Bundle bundle) {
                String token = mBaidu.getAccessToken();
                Log.d(TAG, "onComplete: "+token);
                callBackVue(token, null);
            }

            @Override
            public void onBaiduException(BaiduException e) {
//                refreshUI("exception");
                Log.e(TAG, "onBaiduException.code="+e.getErrorCode()+"msg="+e.getMessage());
                callBackVue(null, "百度授权异常："+e.getMessage());
            }

            //回调给vue
            private void callBackVue(String token, String err){
                JSONObject jo = new JSONObject();
                try {
                    //0失败，1成功
                    jo.put("rsCode", token == null? 0:1);
                    jo.put("token", token );
                    jo.put("msg" ,token == null? "百度授权成功":err);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSUtil.execCallback(pWebview, callBackid, jo, JSUtil.OK, false);
            }

            @Override
            public void onError(BaiduDialogError baiduDialogError) {
//                refreshUI("error");
                Log.e(TAG, "onError.code="+baiduDialogError.getErrorCode()+"msg="+baiduDialogError.getMessage()
                        +"link="+baiduDialogError.getFailingUrl()+
                        "\n"+baiduDialogError.getStackTrace());
                callBackVue(null, "百度授权失败："+baiduDialogError.getMessage());
            }

            @Override
            public void onCancel() {
//                refreshUI("cancel");
                callBackVue(null, "百度授权已取消");
            }
        });
    }

    //发起授权登录
    private void sendAuthDingDing(final IWebview pWebview, final String callBackid) {
        checkDDAndInitApi(pWebview, callBackid, true);
    }

    /**
     * 检查或授权及初始化钉钉api
     * @param pWebview
     * @param callBackid
     * @param isLogin 是否授权登录
     * @return
     */
    private boolean checkDDAndInitApi(final IWebview pWebview, final String callBackid, boolean isLogin){
        SendAuth.Req req = new SendAuth.Req();
        req.scope = SendAuth.Req.SNS_LOGIN;
         /*
        用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），
        建议第三方带上该参数，可设置为简单的随机数加session进行校验
         */
//        req.state = "juejin"+System.currentTimeMillis(); 用这个授权取消时进不了回调的判断条件
        req.state = "test";
        iddShareApi = DDShareApiFactory.createDDShareApi(pWebview.getContext(), DDShareActivity.APP_ID, true);
        String errMsg = null;
        if (!iddShareApi.isDDAppInstalled()){
            errMsg = "还未安装钉钉";
        }else
        if(req.getSupportVersion() > iddShareApi.getDDSupportAPI()){
            errMsg = "钉钉版本过低，不支持" + (isLogin?"登录授权":"分享");
        }
        if (errMsg == null && isLogin){
            iddShareApi.sendReq(req);
            DDShareActivity.pWebview = pWebview;
            DDShareActivity.callBackid = callBackid;
        }else if (errMsg != null){
            JSONObject jo = new JSONObject();
            try {
                jo.put("rsCode", 0);
                jo.put("token", null);  //不能授权时token返回null
                jo.put("msg", errMsg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSUtil.execCallback(pWebview, callBackid, jo, JSUtil.OK, false);
        }
        return errMsg == null;
    }

    public void loginResult(IWebview pWebview, JSONArray array){
        L.d(TAG, "loginResult: "+array.toString());
        /**
         *["plus91559093798227","登录结果",
         * "{\"user_token\":\"da69985f35b6fe97435d7dc2116c77cd\",\"nickname\":\"掘金宝9141\",\"avatar\":\"https:\/\/jjlmobile.oss-cn-shenzhen.aliyuncs.com\/default\/avatar.png\",
         * \"city\":\"\",\"is_new\":1,\"invitation\":\"9916588\",\"follow\":0,\"collection\":0,
         * \"first_login\":0,\"unionid\":\"\",\"achieved_gift_bag\":0,\"mobile\":\"13682419141\"}",null]
         */
//        Toast.makeText(pWebview.getContext(), "toast.test="+ array.optString(1), Toast.LENGTH_SHORT).show();
        try {
            JSONObject jo =  new JSONObject(array.getString(2));
            if (jo != null){
                UserModel.setLoginUserInfo(jo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pWebview.evalJS("callVue('from native args')");
    }


    public void vueShareCallback(IWebview pWebview, JSONArray array) {
        L.d(TAG, "vueShareCallback: " + array.toString());

        EventBus.getDefault().post(new ShareCallbackEvent("", 0));
    }

    private void showNativeFragment(IWebview pWebview){
        MainActivity mainActivity = (MainActivity) pWebview.getActivity();
        mainActivity.mainFragment.showHomeFragment();
    }

    public void vueJumpto(IWebview pWebview, JSONArray array){
        L.d(TAG, "vueJumpto: "+array.toString());
        String from = array.optString(1);
        String to =  array.optString(2);
        L.d(TAG, "vueJumpto: to = "+to); //

        MainFragment mainFrag = ((MainActivity) pWebview.getActivity()).mainFragment;
        if (mainFrag.nextFragment != null){  //如是跳回下一fragment
            mainFrag.switchToNextFragment();
            return;
        }

        if (!TAB_PAGES_OF_VUE.contains(to)){  //如果不是vue页就为原生首页
            mainFrag.showHomeFragment();
        }else{
            mainFrag.changeBottomTabBar(true);
        }
    }

    public void vueGoNext(IWebview pWebview, JSONArray array){
        L.d(TAG, "vueGoNext: "+array.toString());
        String from = (String) array.opt(1);
        L.d(TAG, "vueGoNext: from = "+from); // \/movie
        CurrVuePage = (String) array.opt(2);
        L.d(TAG, "vueGoNext: to = "+CurrVuePage);

        MainFragment mainFragment = ((MainActivity) pWebview.getActivity()).mainFragment;

        if (TAB_PAGES_OF_VUE.contains(from)){
            L.d(TAG, "vueGoNext: hideBottomTabBar");
            mainFragment.changeBottomTabBar(false);
        }

        if (CurrVuePage.equals("登录")){ //如果跳到登录页面，说明vue登录失效了
            UserModel.cleanData();
            mainFragment.mAdsHolderView.setVisibility(View.GONE);
        }
    }

    public void vueGoBack(IWebview pWebview, JSONArray array){
        VueCallBackTimes = 0; //触发后置零
        L.d(TAG, "vueGoBack: "+array.toString());
        String from = (String) array.opt(1);
        String to = (String) array.opt(2);
        CurrVuePage = to;
        L.d(TAG, "vueGoBack: to = "+to);

        MainFragment mainFrag = ((MainActivity) pWebview.getActivity()).mainFragment;
//        if (from.equals(ShowVueEvent.PAGE_LOCK_FAN)){
        if (mainFrag.nextFragment != null){
            mainFrag.switchToNextFragment();
            return;
        }

        if (TAB_PAGES_OF_VUE.contains(to)){
//            L.d(TAG, "vueGoBack: "+mainActivity.mainFragment.showVuePageFromNative);

            //如果通过首页或视频详情等native跳转过来的，则不用执行显示vue的任务页面
            if (to.equals(VP_TASK) && !mainFrag.showVuePageFromNative){  //!from.equals(ShowVueEvent.PAGE_LOCK_FAN)
                mainFrag.mBottomBarTask.performClick();
            }

            mainFrag.changeBottomTabBar(true);
        }
        else if (CurrVuePage.equals("/")){ // '/'表示首页
//            L.d(TAG, "vueGoBack: showNativeFragment");
            showNativeFragment(pWebview);
        }
    }

    //登出
    public void vueLoginOut(IWebview pWebview, JSONArray array){
        L.d(TAG, "vueLoginOut: "+array.toString());
        UserModel.cleanData();
        String userName = (String) array.opt(1);
    }

    //清缓存
    public void cleanCache(IWebview pWebview, JSONArray array){
        L.d(TAG, "cleanCache: "+array.toString());

        SPUtils.getInstance().put(Constant.CHANNEL_CHCHE, "");

    }

    //显示隐藏tabBar
    public void showHideTabBar(IWebview pWebview, JSONArray array){
        L.d(TAG, "showHideTabBar: "+array.toString());

        boolean isShow = array.optInt(1) == 1;
        MainFragment mainFrag = ((MainActivity) pWebview.getActivity()).mainFragment;
//        mainFrag.mBottomBar.setVisibility(isShow ? View.VISIBLE:View.GONE);
        mainFrag.changeBottomTabBar(isShow);
    }

}
