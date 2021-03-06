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
    private TextView mTvAppReward;
    private TextView mTvCoin;
    //首页60分钟
    public final static int TYPE_INDEX60 = 1;
    //视频
    public final static int TYPE_VIDEO = 2;

    private int mType;
    //显示多久自动消失
    private final int SHOW_DURATION = 2500;

    public TimeRewardPopup(Context context) {
        super(context);
        initView();
    }
    public TimeRewardPopup(Context context, int type) {
        this(context);
        mType = type;
    }

    public void setView(JSONObject jo){
//        mTvTitle.setText(jo.getString("user_count"));
//        mTvJJB.setText(jo.getString("vcoin")+"个掘金宝");
        if (jo == null) return;

        mTvCoin.setText(String.format("+%s金币", jo.getString("coin")));
        switch (mType){
            case TYPE_INDEX60:
                mTvTitle.setText("奖励到账啦");

                break;
            case TYPE_VIDEO:
                mTvTitle.setText("奖励到账啦");

                Object extObj = jo.get("extra_coin");
                if (extObj instanceof Integer && Integer.parseInt(extObj.toString()) != 0){
                    mTvAppReward.setVisibility(View.VISIBLE);
                    mTvCoin.setText(String.format("+%d金币", jo.getInteger("coin")+jo.getInteger("extra_coin") ));

                    mTvAppReward.setText(String.format("用掘金宝App看视频能多赚%s金币！", jo.getString("extra_coin")));
                }
                break;
        }
        if (mType == TYPE_VIDEO){

        }
    }

    @Override
    public boolean onOutSideTouch() {
//        return super.onOutSideTouch();
        return false;  //不消失
    }

    void initView(){
        mTvTitle = findViewById(R.id.tv_rewardTitle);
        mTvAppReward = findViewById(R.id.tv_shareReward);
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
