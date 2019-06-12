package com.juejinchain.android.network;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.juejinchain.android.MyApplication;
import com.juejinchain.android.R;
import com.juejinchain.android.network.callBack.JSONCallback;
import com.juejinchain.android.ui.view.AlertProDialog;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 通用请求util
 */
public class NetUtil {

    private static AlertProDialog dialog;

    public static interface OnResponse{
        void onResponse(JSONObject response);
    }

    /**
     * 是否响应成功
     * @param response
     * @return
     */
    public static boolean isSuccess(JSONObject response){
        return response.getInteger("code") == NetConfig.NR_CODE_SUCCESS;
    }

    public static boolean isSuccessOrShowErr(JSONObject response, Context context){
        if (isSuccess(response)){
            return true;
        }else {
            Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();

            return false;
        }
    }


    //请求显示loading
    public static void postRequestShowLoading( String api, Map<String, String> param, OnResponse response, boolean isProcessError ){
        showLoading(null);
        postRequest(api, param, response, isProcessError);
    }

    //普通请求，所有响应都回调
    public static void postRequest(final String api, Map<String, String> param, final OnResponse response ){
        postRequest(api, param, response, true);
    }

    /**
     * post请求
     * @param api
     * @param param
     * @param response 响应回调接口
     * @param isProcessError 是否处理异常，false时，非成功响应不通知回调接口
     */
    public static void postRequest(final String api, Map<String, String> param, final OnResponse response, final boolean isProcessError){
        String url = NetConfig.getUrlByAPI(api);
        OkHttpUtils.postAsyn(url, param, new JSONCallback() {
            @Override
            public void onError(Call call, Exception e) {
                dismissLoading();
                Log.e("NetUtil","postRequest.errorApi= "+api );
                e.printStackTrace();
                showErrToast("请求异常："+ e.getMessage());
           }

            @Override
            public void onResponse(JSONObject json) {
//                if (isProcessError){
//                    response.onResponse(json);
//                }else if (isSuccess(json)){
//                    response.onResponse(json);
//                }else {
//                    showErrToast(json.getString("msg"));
//                }
                processResponse(response, json, isProcessError);
            }
        });
    }

    public static void showLoading(boolean cancel){
        AlertProDialog.showLoading(cancel);
    }

    public static void showLoading(String msg){
        AlertProDialog.showLoading(true);
//        AlertProDialog.showPopupWindow(MyApplication.getInstance());
//        if (dialog == null)
//            dialog = new AlertProDialog(MyApplication.getInstance()).builder();
//        dialog.show();
    }

    public static void dismissLoading(){
//        if(dialog != null) dialog.dissmiss();
        AlertProDialog.dissmiss();
    }

    //请求显示loading
    public static void getRequestShowLoading( String api,Map<String, String> param, OnResponse response, boolean isProcessError){
//        showLoading(null);
        getRequest(api, param, response,isProcessError);
    }

    //普通请求，所有响应都回调
    public static void getRequest( String api,Map<String, String> param, OnResponse response){
        getRequest(api, param, response,true);
    }

    /**
     * 要走通用响应码请求，如登录超时等处理
     * @param api
     * @param param
     * @param response
     * @param reqFragment
     */
    public static void getRequest( String api, Map<String, String> param, OnResponse response, Fragment reqFragment){
        getRequest(api, param, response,false);
    }

    /**
     * 通用响应处理
     */
    private static void processResponse(OnResponse response,  JSONObject json, boolean isProcessError){
        dismissLoading();
        if (isProcessError){
            response.onResponse(json);
        }else if (isSuccess(json)){
            response.onResponse(json);
        }else {
            showErrToast(json.getString("msg"));
        }
    }

    /**
     *
     * @param api
     * @param response
     * @param isProcessError 是否处理异常，false时，非成功响应不通知回调接口
     */
    public static void getRequest(final String api, Map<String, String> param,final OnResponse response, final boolean isProcessError){

        String url = NetConfig.getUrlByParams(param, api);
        OkHttpUtils.getAsyn(url, new JSONCallback() {
            @Override
            public void onError(Call call, Exception e) {
                dismissLoading();
                Log.e("NetUtil","getRequest.errorApi= "+api );
                e.printStackTrace();
                if (!isProcessError)showErrToast("get异常："+ e.getMessage());
            }

            @Override
            public void onResponse(JSONObject json) {
               processResponse(response, json, isProcessError);
            }
        });
    }


    private static void showErrToast(String msg){
        Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }


}
