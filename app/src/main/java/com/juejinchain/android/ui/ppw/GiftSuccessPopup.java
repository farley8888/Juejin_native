package com.juejinchain.android.ui.ppw;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.juejinchain.android.R;

import razerdp.basepopup.BasePopupWindow;

public class GiftSuccessPopup extends BasePopupWindow {
    private TextView mTvCount;
    public Button mLookButton;
    private TextView mTvJJB;
    private TextView mTvMoney;

    public GiftSuccessPopup(Context context) {
        super(context);
        initView();
    }

    public void setView(JSONObject jo){
//        mTvCount.setText("101");
        mTvCount.setText(jo.getString("user_count"));
        mTvJJB.setText(jo.getString("vcoin")+"个掘金宝");
        mTvMoney.setText(String.format("（￥%s元现金）", jo.getString("vcoin")));
    }

    void initView(){
        mTvCount = findViewById(R.id.tv_lingCount);
        mLookButton = findViewById(R.id.btn_seeGift);

        mTvJJB = findViewById(R.id.tv_juejinBao);
        mTvMoney = findViewById(R.id.tv_money);
    }

    @Override
    public View onCreateContentView() {
        //不能在此方法初始化view
        return createPopupById(R.layout.dialog_get_gift_success);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return getDefaultScaleAnimation(true);
    }


    @Override
    protected Animation onCreateDismissAnimation() {
        return super.onCreateDismissAnimation();
    }

}
