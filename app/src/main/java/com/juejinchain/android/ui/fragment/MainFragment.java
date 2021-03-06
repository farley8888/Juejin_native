package com.juejinchain.android.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dmcbig.mediapicker.utils.ScreenUtils;
import com.juejinchain.android.H5Plugin.MyPlugin;
import com.juejinchain.android.R;
import com.juejinchain.android.WebAppFragment;
import com.juejinchain.android.base.BaseBackFragment;
import com.juejinchain.android.base.BaseMainFragment;
import com.juejinchain.android.event.HideShowGiftCarButtonEvent;
import com.juejinchain.android.event.RequestUnreadApiEvent;
import com.juejinchain.android.event.ShowTabPopupWindowEvent;
import com.juejinchain.android.event.ShowVueEvent;
import com.juejinchain.android.event.TabSelectEvent;
import com.juejinchain.android.event.TabSelectedEvent;
import com.juejinchain.android.event.VideoDetailEvent;
import com.juejinchain.android.eventbus_activity.EventBusActivityScope;
import com.juejinchain.android.listener.OnItemClickListener;
import com.juejinchain.android.model.UnreadModel;
import com.juejinchain.android.model.UserModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.network.SpUtils;
import com.juejinchain.android.tools.L;
import com.juejinchain.android.ui.alerter.Alerter;
import com.juejinchain.android.ui.dialog.BackExitDialog;
import com.juejinchain.android.ui.ppw.HomeBottomTipsPopupWindow;
import com.juejinchain.android.ui.view.AdCarHolderView;
import com.juejinchain.android.ui.view.BottomBar;
import com.juejinchain.android.ui.view.BottomBarTab;
import com.juejinchain.android.util.Constant;
import com.juejinchain.android.util.SPUtils;
import com.juejinchain.android.util.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.List;

import cn.jzvd.Jzvd;
import io.dcloud.common.adapter.ui.ReceiveJSValue;
import me.yokeyword.fragmentation.SupportFragment;


/**
 * 所有fragment控制器
 * SupportFragment
 * BaseMainFragment
 */
public class MainFragment extends BaseMainFragment {

    private final String TAG = MainFragment.class.getSimpleName();
    private static final int REQ_MSG = 10;

    public static final int HOME = 0;
    public static final int VIDEO = 1;      //影视
    public static final int Make_Money = 2; //赚车赚房
    public static final int TASK = 3;       //任务
    public static final int MINE = 4;       //我的
//SupportFragment
    private SupportFragment[] mFragments = new SupportFragment[5];

    public BottomBar mBottomBar;
    private FragmentManager fragmentManager;
    /**
     * 登录并没领取大礼包显示
     */
    public AdCarHolderView mAdCarView;
    private BottomBarTab mBottomBarMovie;
    private ImageView mBottomLineImg;
    private FrameLayout mFrameLayout;

    String[] vuePages;
    //是否从原始跳转到vue页面
    public boolean showVuePageFromNative;
    /**
     * 子页fragment,记录用于和mainFragment切换
     * 即子页fragment 要调用mainFragment加载的vueFragment
     */
    public BaseBackFragment nextFragment;
    public HomeFragment homeFragment;
    public WebAppFragment webAppFragment;

    public BottomBarTab mBottomBarTask;
    private BottomBarTab mBottomBarMine;

    public HomeBottomTipsPopupWindow mBottomPopupWindow;
    Alerter mUnreadAlerter; //未读信息提示
    private BackExitDialog mBackDialog;
    private List<UnreadModel> unreadModels;
    private ShowTabPopupWindowEvent showTabPopupEvent;

    public static final int MIN_CLICK_DELAY_TIME = 1500;
    private long lastClickTime = 0;
    private boolean mSwitchOK = true;

    private int mCurrPosition; //当前显示的
    private int mPrePosition;

    public static MainFragment newInstance() {

        Bundle args = new Bundle();

        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
//        removeAll();
        EventBus.getDefault().register(this);
        return view;
    }


    void removeAll(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        List<Fragment> frags = fragmentManager.getFragments();
        if (frags.size() > 0){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            for (Fragment frg: frags ) {
                transaction.remove(frg);
            }
            transaction.commitNowAllowingStateLoss();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //DiscoverFragment
        homeFragment = findChildFragment(HomeFragment.class);

        if (homeFragment == null) {

//            mFragments[HOME] = new DiscoverFragment();
            homeFragment = HomeFragment.newInstance();
            mFragments[HOME] = homeFragment;

            /*
             * WebViewFragment 不能同时加两个，
             * 如同时加载WebAppFragment，webViewFragment要先加载
             */
            mFragments[VIDEO] = WebAppFragment.instance("");
//            mFragments[VIDEO] = BlankFragment.newInstance("","");

            mFragments[Make_Money] = WebAppFragment.instance("");
            mFragments[TASK]     = WebAppFragment.instance("task");
            mFragments[MINE]     = WebAppFragment.instance("");

            loadMultipleRootFragment(R.id.fl_tab_container, HOME,
                    mFragments[HOME],
                    mFragments[VIDEO]);  //,mFragments[Make_Money] 2、3一样的不能同时加载
//            addFragments();
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[HOME] = homeFragment;

            mFragments[VIDEO] = findChildFragment(WebAppFragment.class);

            mFragments[Make_Money] = findChildFragment(WebAppFragment.class);
            mFragments[TASK]      = findChildFragment(WebAppFragment.class);
            mFragments[MINE]      = findChildFragment(WebAppFragment.class);
        }
        webAppFragment = (WebAppFragment) mFragments[Make_Money];
    }

    //加载所有
    void addFragments(){
        //因使用了框架，直接加载不能正常显示！
        fragmentManager = getActivity().getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.fl_tab_container, mFragments[0], String.valueOf(0));
        transaction.add(R.id.fl_tab_container, mFragments[1], String.valueOf(1));
        transaction.hide(mFragments[1]);

        transaction.commitNow();
    }

    //执行切换
    private void doSwitchTab(int position, int prePosition){
        if (position > 0){
            if (vuePages == null){
                vuePages = new String[]{MyPlugin.VP_MOVIE, MyPlugin.VP_MakeMoney, MyPlugin.VP_TASK, MyPlugin.VP_MINE};
            }
            WebAppFragment webFragmentVue = (WebAppFragment) mFragments[position];
            // vue内部切换处理
            webFragmentVue.showPage(vuePages[position - 1], new ReceiveJSValue.ReceiveJSValueCallback() {
                @Override
                public String callback(org.json.JSONArray jsonArray) {
                    if (UserModel.isLogin()){
                        if (mCurrPosition == TASK && mBottomBarTask.isShowUnreadDot()){
                            mBottomBarTask.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mBottomBarTask.showUnreadDot(false);
                                }
                            }, 500);
                        }else if (mCurrPosition == MINE && mBottomBarMine.isShowUnreadDot()){
                            mBottomBarMine.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    requestUnreadHintEvent(null);
                                }
                            }, 2000);
                        }
                    }
                    return null;
                }
            });
        }

        if (prePosition == 0){
            //从首页native切换到vue时加个延时, 太早切换显示会有个白屏的过程
            mBottomBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showHideFragment(mFragments[position], mFragments[prePosition]);
                }
            }, 300);
        }else if (position == 0){
            //从vue切回首页native
            showHideFragment(mFragments[position], mFragments[prePosition]);
        }
    }


    private void initView(View view) {
        mAdCarView = view.findViewById(R.id.adsView);
        mBottomLineImg = view.findViewById(R.id.img_bottomLine);
        mFrameLayout = view.findViewById(R.id.fl_tab_container);

        mBottomBar = (BottomBar) view.findViewById(R.id.bottomBar);
        //影视动态加载
        mBottomBarMovie = new BottomBarTab(_mActivity, R.drawable.ic_nav_movie, getString(R.string.movie));
        mBottomBarTask = new BottomBarTab(_mActivity, R.drawable.ic_nav_task, getString(R.string.task));
        mBottomBarMine = new BottomBarTab(_mActivity, R.drawable.ic_nav_my, getString(R.string.mine));

        mBottomBar
                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_nav_home, getString(R.string.home)))
                .addItem(mBottomBarMovie)
                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_car_and_money, null))
                .addItem(mBottomBarTask)
                .addItem(mBottomBarMine);
//        mBottomBar.updateItem(mBottomBarTask,0);


        // 模拟未读消息，提示点
//        mBottomBarMine.setUnreadCount(3);
//        mBottomBarTask.showUnreadDot(true);
        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {

            @Override
            public void onTabSelected(int position, int prePosition) {

                mCurrPosition = position;
                mPrePosition = prePosition;
                if (!mSwitchOK){
                    return;
                }
                mSwitchOK = false;

                mBottomBar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwitchOK = true;
                        doSwitchTab(mCurrPosition, mPrePosition); //延时切换，否则快速点击会有问题
                    }
                }, 200);


//                fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                Fragment frg = fragmentManager.findFragmentByTag(String.valueOf(position));
//                L.d(TAG, "onTabSelected: "+position + ", frag ="+frg);

                //只有两个fragment ,size 会不止2 时间长了frg会为空！
//                if (frg == null && fragmentManager.getFragments().size() < 2)
//                    transaction.add(R.id.fl_tab_container, mFragments[position], String.valueOf(position));
//                else {
//                    if (mFragments[prePosition] != null){
//                        transaction.hide(mFragments[prePosition]);
//                    }
//                    transaction.show(frg);
//                }
//                transaction.commit();
            }

            @Override
            public void onTabUnselected(int position) {
//                Log.d(TAG, "onTabUnselected: "+position);
                if (position == MINE && mBottomBarMine.isShowUnreadDot()) requestUnreadHintEvent(null);
            }

            @Override
            public void onTabReselected(int position) {
                L.d("baseMainFrg", "onTabReselected: "+position);
                if (position == HOME){

                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    Log.i("zzz", "onTabReselected: " + (currentTime - lastClickTime));
                    if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                        lastClickTime = currentTime;
                        HomeFragment homeFragment = (HomeFragment) mFragments[0];
//                    Log.d(TAG, "onTabReselected: "+ homeFragment.currFragment);
                        if (homeFragment.currChannel == null) return;
                        EventBusActivityScope.getDefault(_mActivity).post(new TabSelectedEvent(position,homeFragment.currChannel.getName() ));
                        mBottomBar.getItem(0).showLoadingByPos(R.drawable.refresh, 0);
                    }else {
                        Log.w(TAG,"点击过快");
                    }
                }else if (position == TASK){
                    if (UserModel.isLogin()) mBottomBarTask.showUnreadDot(false);
                }

                // 在FirstPagerFragment,FirstHomeFragment中接收, 因为是嵌套的Fragment
                // 主要为了交互: 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
//                EventBusActivityScope.getDefault(_mActivity).post(new TabSelectedEvent(position));
//                EventBus.getDefault().post(new TabSelectedEvent(position));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserModel.isLogin()){
            //vue是20s 请求一次
            if (System.currentTimeMillis() - (long)SpUtils.get(getContext(), "unreadKey", 0l) < 10*1000 ){
                return;
            }
            requestUnreadHintEvent(new RequestUnreadApiEvent());
        }
    }

    @Subscribe()
    public void requestUnreadHintEvent(RequestUnreadApiEvent event){
        NetUtil.getRequest(NetConfig.API_UnreadMsg, null, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (NetUtil.isSuccess(response)){
                    SpUtils.put(getContext(), "unreadKey", System.currentTimeMillis());

                    JSONArray array = response.getJSONArray("data");
                    List<UnreadModel> temp = JSON.parseArray(array.toJSONString(), UnreadModel.class);
                    //只弹框显示一次
                    if (unreadModels == null && temp.size() > 0){
                        if (mCurrPosition == 0){
                            unreadModels = temp;
                            showUnreadAlert(unreadModels);
                        }
                    }
                    mBottomBarMine.showUnreadDot(temp != null && temp.size()>0);
                }

            }
        }, true);
    }

    /** 显示未读消息alert */
    public void showUnreadAlert(List<UnreadModel>  list) {
        if (mUnreadAlerter == null) mUnreadAlerter = Alerter.create(getActivity());

        UnreadModel item = list.get(0); //vue是3秒显示一个。其实没必要，因为跳转过去会自动全部阅读
        mBottomBarMine.showUnreadDot(true);
        mUnreadAlerter
                .setTitle(StringUtils.filterHTMLTag(item.title))
                .setText(StringUtils.filterHTMLTag(item.content))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Alerter.hide();
                        showVue(ShowVueEvent.PAGE_MESSAGE_LIST, "");
                        mBottomBarMine.showUnreadDot(false);
                    }
                }).setDuration(3000).show();
        mUnreadAlerter.enableSwipeToDismiss();

    }

    @Subscribe()
    public void showVuePageEvent(ShowVueEvent event){
        showVue(event.page, event.param);
    }

    /**
     * 子页使用vue显示
     * ArticleDetails/5090577 文章详情
     * login 登录
     * 等等
     * @see ShowVueEvent
     */
    public void showVue(String page, String params){
        showVuePageFromNative = true;
//        mCurrPosition = 1; 这个会影响 reselected，从文章详情回来后

        WebAppFragment webVueFragment = (WebAppFragment) mFragments[1];

        webVueFragment.showPage(page+ ( params.length() > 0 ? "/"+params : ""), jsonArray -> {
            showHideFragment(webVueFragment, mFragments[0]);
            return null;
        });
        changeBottomTabBar(false);
    }

    public void showHomeFragment(){
        showVuePageFromNative = false;
        showHideFragment(mFragments[0], mFragments[1]); //这样按钮状态未能修改
        if (mCurrPosition != 0)mBottomBar.getItem(0).performClick();
        changeBottomTabBar(true);

    }

    //跳转到vue登录页面
    public void goLogin(){
        showVue(ShowVueEvent.PAGE_LOGIN, "");
    }

    public void changeBottomTabBar(boolean isShow){
        changeBottomPopup(isShow);
        if (isShow && showVuePageFromNative){
            showVuePageFromNative = false;
            showHideFragment(mFragments[0], mFragments[1]);
        }
        mBottomBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mBottomLineImg.setVisibility(isShow ? View.VISIBLE : View.GONE);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mFrameLayout.getLayoutParams();
        FrameLayout.LayoutParams vueParams = (FrameLayout.LayoutParams) webAppFragment.webFrame.getLayoutParams();

        float bottomHeight = getContext().getResources().getDimension(R.dimen.bottombar_height); //此结果单位为px
//        Log.d(TAG, "changeBottomTabBar: "+bottomHeight);
        if (isShow){
            //ScreenUtils.dp2px(getContext(), 50)
            params.setMargins(0,0, 0, (int) bottomHeight);
            //因为vue取的是手机的高度，所以要加个负值
            vueParams.setMargins(0,0, 0, -(int) bottomHeight);
        }else{
            params.setMargins(0,0, 0, 0);
            vueParams.setMargins(0,0, 0, 0);
        }

        mFrameLayout.setLayoutParams(params);
        webAppFragment.webFrame.setLayoutParams(vueParams);
    }

    public void removeChildFragment(Fragment parentFragment) {
        FragmentManager fragmentManager = parentFragment.getChildFragmentManager();
        Fragment child = fragmentManager.findFragmentById(0); //R.id.child
        if (child != null) {
            fragmentManager.beginTransaction()
                    .remove(child)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQ_MSG && resultCode == RESULT_OK) {

        }
    }


    private void changeBottomPopup(boolean isShow) {
        if (isShow){
            if(mBottomPopupWindow != null && showTabPopupEvent!= null) onBottomTabPopShowEvent(showTabPopupEvent);
        }else {
            if(mBottomPopupWindow != null ){
                mBottomPopupWindow.dismiss();
            }
        }
    }

    @Override
    public boolean onBackPressedSupport() {
//        L.d(TAG, "onBackPressedSupport: ");
        if (mCurrPosition != 0){
            showHomeFragment();
        }else{
            if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.state == Jzvd.STATE_PLAYING){
                if (Jzvd.CURRENT_JZVD.screen == Jzvd.SCREEN_FULLSCREEN)
                    Jzvd.backPress();
                else{
                    Jzvd.resetAllVideos();
                }
            }else
                showExitDialog();
        }
//        return super.onBackPressedSupport(); 此行再按一次退出
        return true;
    }

    //弹出退出对话框
    void showExitDialog(){
//        QuickPopupBuilder.with(getContext()).contentView(R.layout.dialog_get_gift_success).show();
        if(mBackDialog == null) mBackDialog = new BackExitDialog(getContext());
        mBackDialog.setCanceledOnTouchOutside(false);
        mBackDialog.show();
        mBackDialog.setClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (view.getId() == R.id.btn_toGet){    //去领取
                    //切换到任务页
                    mBottomBarTask.performClick();
                }else if(view.getId() == R.id.btn_exit){ //退出
                    getActivity().finish();
                }
            }
        });
    }

    /**
     * start other BrotherFragment
     */
    public void startBrotherFragment(SupportFragment targetFragment) {
        start(targetFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

        if(mBottomPopupWindow != null && mBottomPopupWindow.isShowing()){
            mBottomPopupWindow.dismiss();
        }
    }

    public void switchToNextFragment(){
        showHideFragment(nextFragment,MainFragment.this);
    }
    
    @Subscribe()
    public void resetVideoDetailEvent(VideoDetailEvent event){
        L.d(TAG, "resetVideoDetail: ");
        nextFragment = null;
    }

    @Subscribe()
    public void hideShowAdsEvent(HideShowGiftCarButtonEvent event){
        mAdCarView.setVisibility(event.isShow?View.VISIBLE: View.GONE);
    }

    @Subscribe()
    public void switchTabEvent(TabSelectEvent event){
        L.d(TAG, "switchTabEvent: ");

        mBottomBar.getItem(event.position).performClick();
    }

    @Subscribe()
    public void onBottomTabPopShowEvent(ShowTabPopupWindowEvent event){
        showTabPopupEvent = event;
        //非首页只提示点，不显示popup
        if (mCurrPosition != HOME){
            mBottomBarTask.showUnreadDot(true);
            return;
        }
        int[] location = new int[2];
        int position = 2;
        int delta = position == 1 ? 35 : 25;

        View anchor = mBottomBarTask;
        anchor.getLocationOnScreen(location);
        if(mBottomPopupWindow == null || !mBottomPopupWindow.isShowing()){
            mBottomPopupWindow = new HomeBottomTipsPopupWindow(getActivity());
            mBottomPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] - anchor.getWidth()/2,
                    location[1] - ScreenUtils.dp2px(getActivity(), delta));

            if (UserModel.isNew()){
                mBottomPopupWindow.textView.setText("连续签到7天得"+event.count+"金币");
            }else{
                mBottomPopupWindow.textView.setText("今日奖励可领取");
            }
            SPUtils.getInstance().put(Constant.SKey_BottomPopupTime, System.currentTimeMillis());
            mBottomBarTask.showUnreadDot(true);

            mBottomPopupWindow.mContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBottomPopupWindow.dismiss();
                    mBottomBarTask.performClick();
                    mBottomPopupWindow = null;
                }
            });
        }
    }

}
