package com.juejinchain.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.dmcbig.mediapicker.utils.ScreenUtils;
import com.juejinchain.android.R;
import com.juejinchain.android.listener.OnItemClickListener;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;

import org.w3c.dom.Text;

/**
 * 首页返回退出弹窗提示
 */
public class BackExitDialog extends Dialog {
    public BackExitDialog(Context context) {
        this(context,0);
    }

    private OnItemClickListener clickListener;

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public BackExitDialog(Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_back_alert);

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

        NetUtil.getRequest(NetConfig.API_ShareTop, null, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.d("baseBack", "onResponse: "+response.toJSONString());
                if (NetUtil.isSuccess(response)){
                    fillView(response.getJSONObject("data"));
                }
            }
        });
    }

    void fillView(JSONObject jo) {
        ((TextView) findViewById(R.id.tv_currentCar)).setText(jo.getString("shortname"));
        ((TextView) findViewById(R.id.tv_dotask_rewardCar)).setText(jo.getString("fullname"));
        ImageView image = findViewById(R.id.img_rewardCar);


        Glide.with(getContext()).load(jo.getString("thumb"))
                .placeholder(R.drawable.default_img)
                .into(image);
    }


    private void initView(){
        TextView toGetButton = findViewById(R.id.btn_toGet);
        toGetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (clickListener != null){
                    clickListener.onItemClick(0, view);
                }
            }
        });
        findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (clickListener != null){
                    clickListener.onItemClick(1, view);
                }
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> dismiss());
    }
}
