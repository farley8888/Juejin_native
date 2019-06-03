package com.juejinchain.android.ui.fragment;

import android.os.Bundle;
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

import com.dmcbig.mediapicker.utils.ScreenUtils;
import com.juejinchain.android.H5Plugin.MyPlugin;
import com.juejinchain.android.R;
import com.juejinchain.android.WebAppFragment;
import com.juejinchain.android.event.ShowTabPopupWindowEvent;
import com.juejinchain.android.event.TabSelectedEvent;
import com.juejinchain.android.eventbus_activity.EventBusActivityScope;
import com.juejinchain.android.tools.L;
import com.juejinchain.android.ui.ppw.HomeBottomTipsPopupWindow;
import com.juejinchain.android.ui.view.AdsHolderView;
import com.juejinchain.android.ui.view.BottomBar;
import com.juejinchain.android.ui.view.BottomBarTab;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.yokeyword.fragmentation.SupportFragment;


/**
 * Created by YoKeyword on 16/6/30.
 */
public class MainFragment extends SupportFragment {
    private static final int REQ_MSG = 10;

    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
//SupportFragment
    private SupportFragment[] mFragments = new SupportFragment[5];

    private BottomBar mBottomBar;
    private FragmentManager fragmentManager;
    /**
     * 登录并没领取大礼包显示
     */
    private AdsHolderView mAdsHolderView;
    private BottomBarTab mBottomBarMovie;
    private ImageView mBottomLineImg;
    private FrameLayout mFrameLayout;

    String[] vuePages;
    public boolean startVuePage; //
    private BottomBarTab mBottomBarTask;
    private BottomBarTab mBottomBarMine;

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

        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //DiscoverFragment
        HomeFragment firstFragment = findChildFragment(HomeFragment.class);

        if (firstFragment == null) {

//            mFragments[FIRST] = new DiscoverFragment();
            mFragments[FIRST] = HomeFragment.newInstance();

            /*
             * WebViewFragment 不能同时加两个，
             * 如同时加载WebAppFragment，webViewFragment要先加载
             */
            mFragments[SECOND] = WebAppFragment.instance("");
//            mFragments[SECOND] = BlankFragment.newInstance("","");

            mFragments[THIRD] = WebAppFragment.instance("task");
            mFragments[3]     = WebAppFragment.instance("");
            mFragments[4]     = WebAppFragment.instance("");

            loadMultipleRootFragment(R.id.fl_tab_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND]);  //,mFragments[THIRD] 2、3一样的不能同时加载
//            addFragments();
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = firstFragment;

            mFragments[SECOND] = findChildFragment(WebAppFragment.class);

            mFragments[THIRD]  = findChildFragment(WebAppFragment.class);
            mFragments[3]      = findChildFragment(WebAppFragment.class);
            mFragments[4]      = findChildFragment(WebAppFragment.class);
        }
    }

    //初始化所有
    void addFragments(){
        //因使用了框架，直接加载不能正常显示！
        fragmentManager = getActivity().getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.fl_tab_container, mFragments[0], String.valueOf(0));
        transaction.add(R.id.fl_tab_container, mFragments[1], String.valueOf(1));
        transaction.hide(mFragments[1]);

        transaction.commitNow();
    }

    private void initView(View view) {
        mAdsHolderView = view.findViewById(R.id.adsView);
        mBottomLineImg = view.findViewById(R.id.img_bottomLine);
        mFrameLayout = view.findViewById(R.id.fl_tab_container);

        mBottomBar = (BottomBar) view.findViewById(R.id.bottomBar);
        //影视动态加载
        mBottomBarMovie = new BottomBarTab(_mActivity, R.drawable.ic_discover_white_24dp, getString(R.string.movie));
        mBottomBarTask = new BottomBarTab(_mActivity, R.drawable.ic_discover_white_24dp, getString(R.string.task));
        mBottomBarMine = new BottomBarTab(_mActivity, R.drawable.ic_discover_white_24dp, getString(R.string.mine));

        mBottomBar
                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_message_white_24dp, getString(R.string.home)))
                .addItem(mBottomBarMovie)
                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_car_and_money, null))
                .addItem(mBottomBarTask)
                .addItem(mBottomBarMine);

        // 模拟未读消息，提示点
//        mBottomBarMine.setUnreadCount(3);
//        mBottomBarTask.showUnreadDot(true);
        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {

            @Override
            public void onTabSelected(int position, int prePosition) {

             /* vue切换处理
              * /movie
              * /make_money
              * /task
              * /personal_center
              * ArticleDetails/5090577 文章详情
              * task
              */
                if (position > 0){
                    if (vuePages == null){
                        vuePages = new String[]{MyPlugin.PK_MOVIE, MyPlugin.PK_MakeMoney, MyPlugin.PK_TASK, MyPlugin.PK_MINE};
                    }
                    WebAppFragment webFragmentVue = (WebAppFragment) mFragments[position];
                    webFragmentVue.showPage(vuePages[position-1]);
                }

                showHideFragment(mFragments[position], mFragments[prePosition]);

                if (mFragments[prePosition] == mFragments[position] ){
                    return;
                }

                fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment frg = fragmentManager.findFragmentByTag(String.valueOf(position));
                L.d("MainFragment", "onTabSelected: "+position + ", frag ="+frg);
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

            }

            @Override
            public void onTabReselected(int position) {
                // 在FirstPagerFragment,FirstHomeFragment中接收, 因为是嵌套的Fragment
                // 主要为了交互: 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
                EventBusActivityScope.getDefault(_mActivity).post(new TabSelectedEvent(position));
            }
        });
    }

    public void removeWebAppFragment() {
        fragmentManager = getFragmentManager();
        Fragment child = findChildFragment(WebAppFragment.class);
        if (child != null) {
            fragmentManager.beginTransaction()
                    .remove(child).commit();
//                    .commitAllowingStateLoss();
        }
    }

    public void showVue(String page, String params){
//        mBottomBarMovie.performClick();
        startVuePage = true;
        WebAppFragment webVueFragment = (WebAppFragment) mFragments[1];
        showHideFragment(webVueFragment, mFragments[0]);
        webVueFragment.showPage(page+"/"+params);
        changeBottomTabBar(false);
    }

    public void showHomeFragment(){
        if(!startVuePage) return; //未开始不处理
        startVuePage = false;
//        jumpVuePage--;
        showHideFragment(mFragments[0], mFragments[1]);
        changeBottomTabBar(true);
    }

    public void changeBottomTabBar(boolean isShow){
        mBottomBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mBottomLineImg.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mAdsHolderView.setVisibility(isShow ? View.VISIBLE : View.GONE);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mFrameLayout.getLayoutParams();
        if (isShow){
            params.setMargins(0,0, 0, ScreenUtils.dp2px(getContext(), 50));
        }else{
            params.setMargins(0,0, 0, 0);
        }
        mFrameLayout.setLayoutParams(params);
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

        if(mPopupWindow != null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }
    }

    @Subscribe()
    public void onBottomTabPopShowEvent(ShowTabPopupWindowEvent event){
        int[] location = new int[2];
        int position = 2;
        int delta = position == 1 ? 35 : 25;

        View anchor = mBottomBarTask;
        anchor.getLocationOnScreen(location);
        if(mPopupWindow == null || !mPopupWindow.isShowing()){
            mPopupWindow = new HomeBottomTipsPopupWindow(getActivity());
            mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] - anchor.getWidth()/2,
                    location[1] - ScreenUtils.dp2px(getActivity(), delta));

            mPopupWindow.mContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPopupWindow.dismiss();
                    mBottomBarTask.performClick();
                }
            });
        }
    }

    private HomeBottomTipsPopupWindow mPopupWindow;
}
