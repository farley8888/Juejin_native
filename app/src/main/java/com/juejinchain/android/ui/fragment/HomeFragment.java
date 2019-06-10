package com.juejinchain.android.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dmcbig.mediapicker.utils.ScreenUtils;
import com.juejinchain.android.R;
import com.juejinchain.android.adapter.HomePagerFragmentAdapter;
import com.juejinchain.android.base.BaseMainFragment;
import com.juejinchain.android.event.ShareCallbackEvent;
import com.juejinchain.android.event.ShareEvent;
import com.juejinchain.android.event.ShowTabPopupWindowEvent;
import com.juejinchain.android.event.ShowVueEvent;
import com.juejinchain.android.event.UpdateChannelEvent;
import com.juejinchain.android.model.ChannelModel;
import com.juejinchain.android.model.UserModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.network.OkHttpUtils;
import com.juejinchain.android.network.callBack.JSONCallback;
import com.juejinchain.android.tools.L;
import com.juejinchain.android.ui.ppw.HomeTipsPopupWindow;
import com.juejinchain.android.ui.activity.CategoryExpandActivity;
import com.juejinchain.android.ui.activity.SearchActivity;
import com.juejinchain.android.ui.dialog.ShareDialog;
import com.juejinchain.android.ui.view.PagerSlidingTabStrip;
import com.juejinchain.android.util.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.juejinchain.android.util.Constant.CHANNEL_CHCHE;


/**
 * 首页 主fragment
 */
public class HomeFragment extends BaseMainFragment implements View.OnClickListener {

//    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private Button mAddBtn;
    //可领取金币
    private TextView tvCoin;
    private PagerSlidingTabStrip mTabs;
    public ChannelModel currChannel;
    private String TAG = HomeFragment.class.getSimpleName();
//    PromptGetPopup mPromptPopup;
    private HomeTipsPopupWindow mPopupWindow;

    public List<ChannelModel> mChannelList = new ArrayList<>();

    private TextView mSearchView;

    private ImageButton mShareView;
    private CountDownTimer mCountDownTimer;
    private TextView tvCountTime;
    private boolean mIsVisible;
    ShareDialog mShareDialog;

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        EventBus.getDefault().register(this);
        return view;
    }

    private void initView(View view) {
        mIsVisible = true;
//        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        mTab = (TabLayout) view.findViewById(R.id.tab);
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mAddBtn = view.findViewById(R.id.btn_add);
        mSearchView = view.findViewById(R.id.button2);
        mShareView = view.findViewById(R.id.button4);
        tvCoin = view.findViewById(R.id.tv_lingCount);
        tvCountTime = view.findViewById(R.id.tv_countTime);

        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int offset) {
//                Log.d(TAG, "onPageScrolled: "+i+", offset="+offset);

            }

            @Override
            public void onPageSelected(int i) {
                currChannel = mChannelList.get(i);
//                Log.d(TAG, "onPageSelected: "+currChannel.getName());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
//        mToolbar.setTitle("发现");
//        mTab.addTab(mTab.newTab());
//        mTab.addTab(mTab.newTab());
        setOnClickListener();

        View lyLing = view.findViewById(R.id.ly_ling);
        lyLing.setOnClickListener(this);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                lyLing.performClick();
            }
        });
    }

    private void setOnClickListener() {
        mAddBtn.setOnClickListener(this);
        mSearchView.setOnClickListener(this);
        mShareView.setOnClickListener(this);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
//        String[] array = new String[]{getString(R.string.msg), getString(R.string.more),"热门s爽肤水","农人asfdasf","NBA都是","CBA地方","视频艾艾","国际afaf"};
//        mViewPager.setAdapter(new HomePagerFragmentAdapter(getChildFragmentManager(), array));

//        mTab.setupWithViewPager(mViewPager);
//        mTabs.setViewPager(mViewPager);
//        setTabsValue();

        String channelCacheData = SPUtils.getInstance().getString(CHANNEL_CHCHE);
        if(TextUtils.isEmpty(channelCacheData)){
            loadChannel();
        }else{
            mChannelList = JSON.parseArray(channelCacheData, ChannelModel.class);
            setTabsPage();
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mIsVisible = !hidden;
        if (hidden){
            if (mPopupWindow != null && mPopupWindow.isShowing()) mPopupWindow.dismiss();
        }
        MainFragment mainFragment = (MainFragment) getParentFragment();
        if (UserModel.isLogin()){

//        Log.d(TAG, "onHiddenChanged: "+mainFragment);
            if (null != mainFragment.mAdsHolderView )
                mainFragment.mAdsHolderView.setVisibility(UserModel.hasGetGiftBag() ? View.GONE:View.VISIBLE);
        }else{
            mainFragment.mAdsHolderView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe()
    public void shareCallbackEvent(ShareCallbackEvent event){

        if (mShareDialog != null) mShareDialog.dismiss();

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (UserModel.isLogin()){
            if (!UserModel.hasGetGiftBag()){ //未领取
                mAddBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showLingRewardPop();
                    }
                }, 1000); //等activity启动完才能加载pop
            }
            if (!UserModel.isNew()){
                mAddBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMessageHint();
                    }
                }, 500); //
            }
        }
    }

    //非新用户调用，没签到的提示
    void loadMessageHint(){
        NetUtil.getRequest(NetConfig.API_MessageHint, null, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (NetUtil.isSuccess(response)){
                    if (response.getJSONObject("data").getInteger("unsigned") == 1){
                        EventBus.getDefault().post(new ShowTabPopupWindowEvent());
                    }
                }
            }
        });
    }

    //加载是否可领取状态api
    void loadLingStatusApi(){
        //
        NetUtil.getRequest(NetConfig.API_Times30, null, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                response = response.getJSONObject("data");
                long remainTime = response.getInteger("time_remaining");
                tvCoin.setText(response.getString("coin"));

                if (remainTime == 0){ //可领取
                    doLingApi();
                }else {               //倒计时
                    if (mCountDownTimer != null) mCountDownTimer.cancel();
                    mCountDownTimer = new CountDownTimer(remainTime*1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long min = millisUntilFinished/1000 / 60;
                            long s = millisUntilFinished/1000 % 60;

                            tvCountTime.setText(String.format("%02d:%02d",min ,s));
                        }

                        @Override
                        public void onFinish() {
                            tvCountTime.setText(getString(R.string.lingqu));
                            showLingRewardPop();
                        }
                    };
                    mCountDownTimer.start();
                }
            }
        });
    }

    //调用领取接口后，才保存在服务器
    void doLingApi(){
        NetUtil.getRequest(NetConfig.API_Times30_Save, null, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (NetUtil.isSuccess(response)){
                    UserModel.setGetGiftBag(true);
                }

            }
        });
    }

    //显示领取奖励popup
    private void showLingRewardPop(){
//                mPromptPopup = new PromptGetPopup(getContext());
//                //外面可点,会影响显示位置
//                mPromptPopup.setOutSideTouchable(true);
//                mPromptPopup.setBackground(null);  //背景透明
        int[] location = new int[2];
        tvCoin.getLocationOnScreen(location);
        if(mPopupWindow == null || !mPopupWindow.isShowing()){
            mPopupWindow = new HomeTipsPopupWindow(getActivity());
            if (mIsVisible)
                mPopupWindow.showAtLocation(tvCoin, Gravity.NO_GRAVITY, location[0] - ScreenUtils.dp2px(getActivity(), 70),
                    location[1] + ScreenUtils.dp2px(getActivity(), 25));
        }else if(mPopupWindow != null){
//                EventBus.getDefault().post(new ShowTabPopupWindowEvent());
        }

        mPopupWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadLingStatusApi();
                if (UserModel.isNew())
                    EventBus.getDefault().post(new ShowTabPopupWindowEvent());
            }
        });
    }

    //领取奖励后加载 获取倒计时时间的接口
    void loadRewardTimeApi(){
        NetUtil.getRequest(NetConfig.API_Times30, null, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {

            }
        });
    }


    private void loadChannel() {

        String url = NetConfig.getUrlByAPI(NetConfig.API_ChannelGet);
        OkHttpUtils.getAsyn(url, new JSONCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(JSONObject response) {
                if (NetUtil.isSuccess(response)) {
                    mChannelList = JSON.parseArray(response.getString("data"), ChannelModel.class);
                    SPUtils.getInstance().put(CHANNEL_CHCHE, response.getString("data"));
                    setTabsPage();
                }
            }
        });
    }

    void setTabsPage() {
//        String[] array = new String[mChannelList.size()];
//        for (int i = 0 ; i < mChannelList.size(); i++)
//            array[i] = mChannelList.get(i).getName();

        mViewPager.setAdapter(new HomePagerFragmentAdapter(getChildFragmentManager(), mChannelList));

        mTabs.setViewPager(mViewPager);
        setTabsValue();
    }


    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        mTabs.setShouldExpand(true);

        // 设置Tab的分割线的颜色
//        mTabs.setDividerColor(getResources().getColor(R.color.color_80cbc4));
        // 设置分割线的上下的间距,传入的是dp
        mTabs.setDividerPaddingTopBottom(10);

        // 设置Tab底部线的高度,传入的是dp
        mTabs.setUnderlineHeight(1);
        //设置Tab底部线的颜色
//        mTabs.setUnderlineColor(getResources().getColor(R.color.color_1A000000));

        // 设置Tab 指示器Indicator的高度,传入的是dp
        mTabs.setIndicatorHeight(0);
        // 设置Tab Indicator的颜色
//        mTabs.setIndicatorColor(getResources().getColor(R.color.color_45c01a));

        // 设置Tab标题文字的大小,传入的是dp
        mTabs.setTextSize(15);
        // 设置选中Tab文字的颜色
        mTabs.setSelectedTextColor(getResources().getColor(R.color.text_yellow));
        //设置正常Tab文字的颜色
        mTabs.setTextColor(getResources().getColor(R.color.tab_selected_color));

        //  设置点击Tab时的背景色
//        mTabs.setTabBackground(R.drawable.background_tab);

        //是否支持动画渐变(颜色渐变和文字大小渐变)
        mTabs.setFadeEnabled(true);
        // 设置最大缩放,是正常状态的0.3倍
        mTabs.setZoomMax(0.2F);
        //设置Tab文字的左右间距,传入的是dp
        mTabs.setTabPaddingLeftRight(15);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                startActivity(new Intent(getActivity(), CategoryExpandActivity.class));
                break;
            case R.id.button2:  //搜索
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.button4:  //分享
                if (UserModel.isLogin()){
                   if (mShareDialog == null) mShareDialog = new ShareDialog(getActivity(), ShareEvent.TYPE_INDEX, "");
                   mShareDialog.show();
                }
                else{
                    MainFragment mainFragment = (MainFragment) getParentFragment();
                    mainFragment.showVue(ShowVueEvent.PAGE_LOGIN, "");
                }

                break;
            case R.id.ly_ling:  //领奖励
                if (mPopupWindow != null) mPopupWindow.dismiss();
                loadLingStatusApi();
                break;
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        L.d(TAG, "onBackPressedSupport: ");
//        return super.onBackPressedSupport();
        return false; //返回false 让MainFragment处理
    }

    @Subscribe
    public void onChannelUpdate(UpdateChannelEvent event){
        String channelCacheData = SPUtils.getInstance().getString(CHANNEL_CHCHE);
        if(!TextUtils.isEmpty(channelCacheData)){
            mChannelList = JSON.parseArray(channelCacheData, ChannelModel.class);
            setTabsPage();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if(mPopupWindow != null && !mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }
    }

}
