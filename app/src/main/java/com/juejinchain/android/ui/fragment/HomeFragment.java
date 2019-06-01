package com.juejinchain.android.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.juejinchain.android.R;
import com.juejinchain.android.adapter.HomePagerFragmentAdapter;
import com.juejinchain.android.base.BaseMainFragment;
import com.juejinchain.android.model.ChannelModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.network.OkHttpUtils;
import com.juejinchain.android.network.callBack.JSONCallback;
import com.juejinchain.android.ui.activity.CategoryExpandActivity;
import com.juejinchain.android.ui.activity.search.SearchActivity;
import com.juejinchain.android.ui.view.PagerSlidingTabStrip;
import com.juejinchain.android.ui.view.ShareDialog;
import com.juejinchain.android.util.SPUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.juejinchain.android.util.Constant.CHANNEL_CHCHE;


/**
 * 首页 主fragment
 */
public class HomeFragment extends BaseMainFragment implements View.OnClickListener {
//    private TabLayout mTab;
//    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private Button mAddBtn;
    private TextView tvCount;
    private PagerSlidingTabStrip mTabs;
    public ChannelModel currChannel;
    private String TAG = HomeFragment.class.getSimpleName();
    PromptGetPopup mPromptPopup;

    public List<ChannelModel> mChannelList = new ArrayList<>();

    private TextView mSearchView;

    private ImageButton mShareView;

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
        return view;
    }

    private void initView(View view) {
//        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        mTab = (TabLayout) view.findViewById(R.id.tab);
        mTabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
        mAddBtn = view.findViewById(R.id.btn_add);
        mSearchView = view.findViewById(R.id.button2);
        mShareView = view.findViewById(R.id.button4);
        tvCount = view.findViewById(R.id.tv_lingCount);

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
        view.findViewById(R.id.ly_ling).setOnClickListener(this);
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
        loadChannel();
    }

    private void loadChannel(){

        String url = NetConfig.getUrlByAPI(NetConfig.API_ChannelGet);
        OkHttpUtils.getAsyn(url, new JSONCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(JSONObject response) {
                if (NetUtil.isSuccess(response)){
                    mChannelList = JSON.parseArray(response.getString("data"), ChannelModel.class);
                    SPUtils.getInstance().put(CHANNEL_CHCHE, response.getString("data"));
                    setTabsPage();
                }
            }
        });
    }

    void setTabsPage(){
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
            case R.id.button2:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.button4:
                new ShareDialog(getActivity()).show();
                break;
            case R.id.ly_ling:
                mPromptPopup = new PromptGetPopup(getContext());
                //外面可点,会影响显示位置
//                mPromptPopup.setAllowInterceptTouchEvent(false);
                mPromptPopup.setBackground(null);  //背景透明
                int[] location = new  int[2] ;
                tvCount.getLocationOnScreen(location);
                Log.d(TAG, "onClick: "+location[0] + ", y ="+location[1]);
//                mPromptPopup.showPopupWindow(location[0], location[1]);
//                mPromptPopup.setOffsetX(-100);
//                mPromptPopup.setOffsetY(location[1]);
//                mPromptPopup.showPopupWindow(tvCount);
                mPromptPopup.showPopupWindow(v);
                break;
        }
    }
}
