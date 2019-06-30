package com.juejinchain.android.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.juejinchain.android.R;
import com.juejinchain.android.adapter.HomePagerFragmentAdapter;
import com.juejinchain.android.adapter.VideoPagerFragmentAdapter;
import com.juejinchain.android.model.ChannelModel;
import com.juejinchain.android.model.VideoCategoryModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.network.OkHttpUtils;
import com.juejinchain.android.network.callBack.JSONCallback;
import com.juejinchain.android.ui.view.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd;
import me.yokeyword.fragmentation.SupportFragment;
import okhttp3.Call;

/**
 * 视频主fragment
 */
public class VideoFragment extends SupportFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ViewPager mViewPager;
    private PagerSlidingTabStrip mTabs;
    //    public VideoCategoryModel currCategory;
    private String TAG = VideoFragment.class.getSimpleName();

    public List<VideoCategoryModel> mCategoryList = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * @param model Parameter 1.
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(String model) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mTabs = view.findViewById(R.id.tabs_video);
        mViewPager = view.findViewById(R.id.viewPager_video);
        // 设置最大缩放,是正常状态的0.3倍
//        mTabs.setZoomMax(0.1F);
        setTabStyle();
        //设置Tab文字的左右间距,传入的是dp
        mTabs.setTabPaddingLeftRight(15);
        loadCategory();
    }

    private void setTabStyle() {
        // 设置Tab是自动填充满屏幕的，true不扩展填充！！
//        mTabs.setShouldExpand(true);

        // 设置Tab的分割线的颜色
        mTabs.setDividerColor(getResources().getColor(R.color.colorTranslucent));
        // 设置分割线的上下的间距,传入的是dp
        mTabs.setDividerPaddingTopBottom(10);

        // 设置Tab底部线的高度,传入的是dp
        mTabs.setUnderlineHeight(0);
        //设置Tab底部线的颜色
//        mTabs.setUnderlineColor(getResources().getColor(R.color.color_1A000000));

        // 设置Tab 指示器Indicator的高度,传入的是dp
        mTabs.setIndicatorHeight(0);
        // 设置Tab Indicator的颜色
//        mTabs.setIndicatorColor(getResources().getColor(R.color.color_45c01a));

        // 设置Tab标题文字的大小,传入的是dp
        mTabs.setTextSize(13);
        // 设置选中Tab文字的颜色
        mTabs.setSelectedTextColor(getResources().getColor(R.color.white));
        //设置正常Tab文字的颜色
//        mTabs.setTextColor(getResources().getColor(R.color.black));

        mTabs.setTabBackground(R.drawable.bg_video_tab);


        //是否支持动画渐变(颜色渐变和文字大小渐变)
        mTabs.setFadeEnabled(false);
        // 设置最大缩放,是正常状态的0.3倍
        mTabs.setZoomMax(0F);
        //设置Tab文字的左右间距,传入的是dp
        mTabs.setTabPaddingLeftRight(20);
    }

    private void loadCategory() {

        String url = NetConfig.getUrlByAPI(NetConfig.API_VideoTypeList);
        OkHttpUtils.getAsyn(url, new JSONCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(JSONObject response) {
                if (NetUtil.isSuccess(response)) {
                    mCategoryList = JSON.parseArray(response.getString("data"), VideoCategoryModel.class);
//                    Log.d("hhhh", "onResponse: "+mChannelList.get(0).getName());
                    setTabsAndPage();
                }
            }
        });
    }

    void setTabsAndPage() {

        mViewPager.setAdapter(new VideoPagerFragmentAdapter(getChildFragmentManager(), mCategoryList));

        //重写了adapter的destroyItem, 就不要设置了，否则后面的可能加载不出来
//        mViewPager.setOffscreenPageLimit(2);

        mTabs.setViewPager(mViewPager);
//        setTabsValue();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public boolean onBackPressedSupport() {
        if (Jzvd.backPress()) {
//            return;
        }
        return super.onBackPressedSupport();
    }

    //    protected void onPause() {
//        super.onPause();
//        Jzvd.resetAllVideos();
//    }
    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        Jzvd.resetAllVideos();
        // 当对用户不可见时 回调
        // 不管是 父Fragment还是子Fragment 都有效！
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
