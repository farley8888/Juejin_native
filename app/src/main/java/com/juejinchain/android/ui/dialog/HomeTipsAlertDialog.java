package com.juejinchain.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.dmcbig.mediapicker.utils.ScreenUtils;
import com.juejinchain.android.R;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;

/**
 * 首页弹窗提示
 */
public class HomeTipsAlertDialog extends Dialog {
    public HomeTipsAlertDialog(Context context) {
        this(context,0);
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
        ((TextView) findViewById(R.id.tv_gftCar_shortName)).setText(jo.getString("shortname"));
        ((TextView) findViewById(R.id.tv_gftCar_fullName)).setText(jo.getString("fullname"));
        ImageView logo = findViewById(R.id.img_gftLogo);
        ImageView image = findViewById(R.id.img_gftLarge);


        Glide.with(getContext()).load(jo.getString("brand_logo")).into(logo);
        Glide.with(getContext()).load(jo.getString("images")).into(image);
    }


    void loadGiftApi(){
        //
        NetUtil.getRequest(NetConfig.API_GiftBag, null, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {

            }
        });
    }

    private void initView(){
        ImageView gifImage = findViewById(R.id.img_get);
        Glide.with(getContext()).load(R.drawable.get_button).into(gifImage);
        gifImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadGiftApi();
            }
        });
        findViewById(R.id.cancel).setOnClickListener(v -> dismiss());
    }
}
