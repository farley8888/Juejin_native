package com.juejinchain.android.ui.ppw;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.juejinchain.android.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * 时段或阅读奖励popup
 */
public class TimeRewardPopup extends BasePopupWindow {
    private TextView mTvTitle;
//    private TextView mTvJJB;
    private TextView mTvCoin;

    //显示多久自动消失
    private final int SHOW_DURATION = 2500;

    public TimeRewardPopup(Context context) {
        super(context);
        initView();
    }

    public void setView(JSONObject jo){
//        mTvTitle.setText("101");
//        mTvTitle.setText(jo.getString("user_count"));
//        mTvJJB.setText(jo.getString("vcoin")+"个掘金宝");
        mTvCoin.setText(String.format("+%s金币", jo.getString("coin")));
    }

    void initView(){
        mTvTitle = findViewById(R.id.tv_rewardTitle);

        mTvCoin = findViewById(R.id.tv_reward_coin);
    }

    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
        mTvCoin.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, SHOW_DURATION);
    }

    @Override
    public View onCreateContentView() {
        //不能在此方法初始化view
        return createPopupById(R.layout.popup_reward_readtime);
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
