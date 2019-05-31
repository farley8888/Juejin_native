package com.juejinchain.android.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juejinchain.android.R;
import com.juejinchain.android.WebAppFragment;
import com.juejinchain.android.event.TabSelectedEvent;
import com.juejinchain.android.eventbus_activity.EventBusActivityScope;
import com.juejinchain.android.ui.view.BottomBar;
import com.juejinchain.android.ui.view.BottomBarTab;

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
    private SupportFragment[] mFragments = new SupportFragment[3];

    private BottomBar mBottomBar;
    private FragmentManager fragmentManager;

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
             *
             */
            mFragments[SECOND] = WebAppFragment.instance("");
//            mFragments[SECOND] = BlankFragment.newInstance("","");

//            mFragments[THIRD] = BlankFragment.newInstance("","");
            mFragments[THIRD] = WebAppFragment.instance("task");

            loadMultipleRootFragment(R.id.fl_tab_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND]);  //,mFragments[THIRD] 2、3一样的不能同时加载
//            addFragments();
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = firstFragment;

            mFragments[SECOND] = findChildFragment(WebAppFragment.class);

            mFragments[THIRD] = findChildFragment(WebAppFragment.class);
        }
    }

    //初始化所有
    void addFragments(){
        //这样直接加载不行！！
        fragmentManager = getActivity().getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.add(R.id.fl_tab_container, mFragments[0], String.valueOf(0));
        transaction.add(R.id.fl_tab_container, mFragments[1], String.valueOf(1));
        transaction.hide(mFragments[1]);

        transaction.commitNow();
    }

    private void initView(View view) {
        mBottomBar = (BottomBar) view.findViewById(R.id.bottomBar);
//.addItem(new BottomBarTab(_mActivity, R.drawable.ic_account_circle_white_24dp, getString(R.string.discover)))
        mBottomBar
                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_message_white_24dp, getString(R.string.msg)))
                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_car_and_money, null))
                .addItem(new BottomBarTab(_mActivity, R.drawable.ic_discover_white_24dp, getString(R.string.more)));

        // 模拟未读消息
        mBottomBar.getItem(FIRST).setUnreadCount(9);

        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {


            @Override
            public void onTabSelected(int position, int prePosition) {

                BottomBarTab tab = mBottomBar.getItem(FIRST);
                BottomBarTab tab3 = mBottomBar.getItem(THIRD);
                if (position == FIRST) {
                    tab.setUnreadCount(0);
                    tab3.showUnreadDot(true);
                } else {
                    tab.setUnreadCount(tab.getUnreadCount() + 1);
                    tab3.showUnreadDot(false);
                }

             /* vue切换处理
              * /movie
              * /make_money
              * /task
              * /personal_center
              */
                if (position > 0){
                    String[] vuePages = new String[]{"make_money", "login"};
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
                Log.d("MainFragment", "onTabSelected: "+position + ", frag ="+frg);
                //只有两个fragment ,size 会不止2 时间长了frg会为空！
//                if (frg == null && fragmentManager.getFragments().size() < 2)
//                    transaction.add(R.id.fl_tab_container, mFragments[position], String.valueOf(position));
//                else {
                    if (mFragments[prePosition] != null){
//                        transaction.hide(mFragments[prePosition]);
                    }
//                    transaction.show(frg);
//                }
//                transaction.hide(mFragments[prePosition>0? 1:0]);
//                transaction.show(mFragments[position > 0? 1:0]);
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

}
