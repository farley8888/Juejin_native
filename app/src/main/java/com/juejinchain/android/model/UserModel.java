package com.juejinchain.android.model;

import android.util.Log;

import com.juejinchain.android.util.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户登录信息
 */
public class UserModel {

    private static final String Key_UserToken = "UKUserToken";
    private static final String Key_NickName = "UKNickName";
    private static final String Key_PHone = "UKPhone";
    //当前登录用户邀请码
    private static final String Key_Invitation = "UKInvitation";
    private static final String Key_ISLogin = "UKLoginIs";
    private static final String Key_ISNew = "UKLoginIsNew";
    private static final String Key_HasGetReward = "UKGetReward";

    private String userName;
    private String token;
    private String invitation;

    public static void setLoginUserInfo(JSONObject json) throws JSONException {
        /**
         *["plus91559093798227","登录结果",
         * "{\"user_token\":\"da69985f35b6fe97435d7dc2116c77cd\",\"nickname\":\"掘金宝9141\",\"avatar\":\"https:\/\/jjlmobile.oss-cn-shenzhen.aliyuncs.com\/default\/avatar.png\",
         * \"city\":\"\",\"is_new\":1,\"invitation\":\"9916588\",\"follow\":0,\"collection\":0,
         * \"first_login\":0,\"unionid\":\"\",\"achieved_gift_bag\":0,\"mobile\":\"13682419141\"}",null]
         * achieved_gift_bag 1已经领取，0 未领取奖励
         */
        String userToken = json.getString("user_token");
//        Log.d("userMode", "setLoginUserInfo: "+userToken);
//        if (userToken != null)
            SPUtils.getInstance().put(Key_ISLogin, userToken != null);

        SPUtils.getInstance().put(Key_UserToken, userToken);
        SPUtils.getInstance().put(Key_NickName, json.getString("nickname"));
        SPUtils.getInstance().put(Key_Invitation, json.getString("invitation"));
        SPUtils.getInstance().put(Key_PHone, json.getString("mobile"));
        SPUtils.getInstance().put(Key_HasGetReward, json.getInt("achieved_gift_bag") == 1);
        SPUtils.getInstance().put(Key_ISNew, json.getInt("is_new") == 1);
    }


    public static void setGetGiftBag(boolean hasGet) {
        SPUtils.getInstance().put(Key_HasGetReward, hasGet);
    }

    //是否领取大礼包
    public static boolean hasGetGiftBag(){
        return SPUtils.getInstance().getBoolean(Key_HasGetReward);
    }
    //是否为新用户
    public static boolean isNew(){
        return SPUtils.getInstance().getBoolean(Key_ISNew);
    }

    public static boolean isLogin(){
        return SPUtils.getInstance().getBoolean(Key_ISLogin);
    }

    public static String getUserName() {
        return SPUtils.getInstance().getString(Key_NickName);
    }

    public static String getUserToken(){
        return SPUtils.getInstance().getString(Key_UserToken);
    }

    public static String getInvitation(){

        return SPUtils.getInstance().getString(Key_Invitation);
    }

    public static String getPhone(){
        return SPUtils.getInstance().getString(Key_PHone);
    }

    public static void cleanData(){

        SPUtils.getInstance().put(Key_ISLogin, false);

        SPUtils.getInstance().put(Key_UserToken, "");
        SPUtils.getInstance().put(Key_NickName, "");
        SPUtils.getInstance().remove(Key_Invitation);
        SPUtils.getInstance().remove(Key_PHone);
    }


}
