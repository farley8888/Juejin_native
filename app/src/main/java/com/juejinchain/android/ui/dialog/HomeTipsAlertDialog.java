package com.juejinchain.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.dmcbig.mediapicker.utils.ScreenUtils;
import com.juejinchain.android.R;
import com.juejinchain.android.event.HideShowGiftCarButtonEvent;
import com.juejinchain.android.event.ShowVueEvent;
import com.juejinchain.android.event.TabSelectEvent;
import com.juejinchain.android.model.UserModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.ui.fragment.HomeFragment;
import com.juejinchain.android.ui.fragment.MainFragment;
import com.juejinchain.android.ui.ppw.GiftSuccessPopup;

import org.greenrobot.eventbus.EventBus;

/**
 * 首页大礼包弹窗提示点，击外面不消失
 * 两种情况
 * 1、每次进入APP未登录显示，
 * 1.2、点击领取跳到登录页，不登录回来不消失
 * 2、登录后未领取的
 */
public class HomeTipsAlertDialog extends Dialog {

    public boolean isGoLogin;
    private JSONObject mJo;
    private HomeFragment mHomeFragment;

    public HomeTipsAlertDialog(Context context, HomeFragment homeFragment) {
        this(context,0);
        mHomeFragment = homeFragment;
        setCanceledOnTouchOutside(false);
    }

    public HomeTipsAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_home_alert);

        Window window = getWindow();
        WindowManager.LayoutParams params =  window.getAttributes();
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        params.width = ScreenUtils.getScreenWidth(context) - ScreenUtils.dp2px(context, 60);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.windowAnimations = R.style.bottomSlideAnim;

        getWindow().setAttributes(params);

        initView();
        loadData();
    }

    @Override
    public void onBackPressed() {

    }

    void setTouch(boolean isfirst){
        if (!isfirst){
            findViewById(R.id.img_get).setOnTouchListener(null);
            findViewById(R.id.cancel).setOnTouchListener(null);
            return;
        }
        findViewById(R.id.img_get).setOnTouchListener(new View.OnTouchListener() { //不能用tou事件
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (UserModel.isLogin()){
                    loadGiftApi();
                }
                else{
                    dismiss(); //去登录不消失
                    isGoLogin = true;
                    //去登录
                    EventBus.getDefault().post(new ShowVueEvent(ShowVueEvent.PAGE_LOGIN, ""));
//                    new GiftSuccessPopup(getContext()).showPopupWindow(); //测试领取成功
                }
                return true;
            }
        });

        findViewById(R.id.cancel).setOnTouchListener(new View.OnTouchListener() { //不能用tou事件
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dismiss();
                EventBus.getDefault().post(new HideShowGiftCarButtonEvent(true));
                return false;
            }
        });

    }

    @Override
    public void show() {

        setTouch(mJo == null);

        if (!isShowing() && mJo != null){
            loadData();
        }
    }
//
    public void myShow(){
        if (!mHomeFragment.isVisible()) return;

        super.show();
    }

    void loadData(){

        NetUtil.getRequest(NetConfig.API_GiftImage, null, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.d("baseTips", "onResponse: "+response.toJSONString());
                if (NetUtil.isSuccess(response)){
                    fillView(response.getJSONObject("data"));
                }
            }
        });
    }

    void fillView(JSONObject jo) {
        mJo = jo ;
        myShow();
        ((TextView) findViewById(R.id.tv_gftCar_shortName)).setText(jo.getString("shortname"));
        ((TextView) findViewById(R.id.tv_gftCar_fullName)).setText(jo.getString("fullname"));
        ImageView logo = findViewById(R.id.img_gftLogo);
        ImageView image = findViewById(R.id.img_gftLarge);


        Glide.with(getContext()).load(jo.getString("brand_logo")).into(logo);
        Glide.with(getContext()).load(jo.getString("images")).into(image);
    }

    /**
     * 加载领取大礼包接口
     */
    void loadGiftApi(){
        NetUtil.getRequest(NetConfig.API_GiftBag, null, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                dismiss();
                if (NetUtil.isSuccess(response)){
                    response = response.getJSONObject("data");
                    UserModel.setGetGiftBag(true);
                    showGiftSuccessDialog(response);
                    EventBus.getDefault().post(new HideShowGiftCarButtonEvent(false));
                }else{
                    Toast.makeText(getContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
                    //已领取
                    if (response.getInteger("code") == 1){
                        UserModel.setGetGiftBag(true);
                        EventBus.getDefault().post(new TabSelectEvent(MainFragment.MINE));
                    }
                }

            }
        });
    }

    /**
     * 显示领取成功对话框
     * @param jo
     */
    void showGiftSuccessDialog(JSONObject jo){
        GiftSuccessPopup successPopup = new GiftSuccessPopup(getContext());
        successPopup.setView(jo);
        successPopup.showPopupWindow();
    }

    private void initView(){
        ImageView gifImage = findViewById(R.id.img_get);
        Glide.with(getContext()).load(R.drawable.get_button).into(gifImage);

        //点击领取按钮
        gifImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserModel.isLogin()){
                    loadGiftApi();
                }
                else{
                    dismiss(); //去登录不消失
                    isGoLogin = true;
                    //去登录
                    EventBus.getDefault().post(new ShowVueEvent(ShowVueEvent.PAGE_LOGIN, ""));
//                    new GiftSuccessPopup(getContext()).showPopupWindow(); //测试领取成功
                }
            }
        });

//        gifImage.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                return true;
//            }
//        });


        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                EventBus.getDefault().post(new HideShowGiftCarButtonEvent(true));
            }
        });
    }
}
