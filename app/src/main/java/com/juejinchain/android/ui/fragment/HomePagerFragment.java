package com.juejinchain.android.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.juejinchain.android.R;
import com.juejinchain.android.adapter.HomePagerAdapter;
import com.juejinchain.android.config.TTAdManagerHolder;
import com.juejinchain.android.event.SaveArticleEvent;
import com.juejinchain.android.event.ShowVueEvent;
import com.juejinchain.android.event.TabSelectedEvent;
import com.juejinchain.android.eventbus_activity.EventBusActivityScope;
import com.juejinchain.android.model.ChannelModel;
import com.juejinchain.android.model.NewsModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.network.OkHttpUtils;
import com.juejinchain.android.network.callBack.JSONCallback;
import com.juejinchain.android.tools.L;
import com.juejinchain.android.util.AnimUtil;
import com.juejinchain.android.util.StringUtils;
import com.juejinchain.android.tools.TToast;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yokeyword.fragmentation.SupportFragment;
import okhttp3.Call;

import static io.dcloud.common.util.ReflectUtils.getApplicationContext;


/**
 * 首页 pageFragment
 * implements SwipeRefreshLayout.OnRefreshListener 不用这个
 * 用pullToRefresh
 */
public class HomePagerFragment extends SupportFragment  {
//    private SwipeRefreshLayout mRefreshLayout;
    PtrClassicFrameLayout mPtrFrameLayout;
    private RecyclerView mRecy;
    private ViewGroup noDataView; //空数据提示view

    private HomePagerAdapter adapter;
    RecyclerAdapterWithHF mAdapter;
    final String TAG = HomePagerFragment.class.getSimpleName();

    private boolean mInAtTop = true;
    private int mScrollTotal;
    Handler handler = new Handler();
    private List<Object> mData = new ArrayList<>();
    int currPage = 0;
    int pageSize = 10;
    public ChannelModel mChannel;
    public String mAPI;
    HomeFragment homeFragment;

    private TextView mTvRecommend;
    private boolean manuallyRefresh;
    private TTAdNative mTTAdNative;
    private boolean mIsVisible;


    public static HomePagerFragment newInstance(ChannelModel model) {

        Bundle args = new Bundle();
        HomePagerFragment fragment = new HomePagerFragment();

//        fragment.mChannel = model; //保存在bundle里面，这样直接赋值，从子页闪退回来会变null
        args.putString("model", JSON.toJSONString(model));
        //推荐和热门
        if(model != null && "0,1".contains(model.getId()) ){
            fragment.mAPI = NetConfig.API_NewsPull;
        }else {
            fragment.mAPI = NetConfig.API_NewsOther;
        }
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_pager_first, container, false);
        initView(view);
//        L.d(TAG, "onCreateView: ");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        L.d(TAG, "onActivityCreated: ");
        Bundle bundle = getArguments();
        mChannel = JSON.parseObject(bundle.getString("model"), ChannelModel.class);
        currPage = 1;
        homeFragment = (HomeFragment) getParentFragment();

        //初始化广告管理对象
        TTAdManager ttAdManager = TTAdManagerHolder.get();
        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());
        //申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
//        TTAdManagerHolder.get().requestPermissionIfNecessary(getApplicationContext());

        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mIsVisible = !hidden;
        if (hidden) NetUtil.dismissLoading();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        mIsVisible = true;
    }


    public void doRefresh(){
        L.d(TAG, "doRefresh: ");
        mPtrFrameLayout.autoRefresh(true);
    }

    private void initView(View view) {
        EventBusActivityScope.getDefault(_mActivity).register(this);

        mRecy = view.findViewById(R.id.recy);
//        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
//        mRefreshLayout.setOnRefreshListener(this);
        mPtrFrameLayout = view.findViewById(R.id.ptr_recycler_view_frame);
        noDataView  = view.findViewById(R.id.ly_no_data);
        noDataView.setVisibility(View.GONE);

        adapter = new HomePagerAdapter(_mActivity, mData);

        mRecy.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(_mActivity);
        mRecy.setLayoutManager(manager);
//        RecyclerView.Adapter<RecyclerView.ViewHolder> _adapter = (RecyclerView.Adapter<RecyclerView.ViewHolder>)adapter;
        mAdapter = new RecyclerAdapterWithHF(adapter);
//        mRecy.setAdapter(adapter);
        mRecy.setAdapter(mAdapter);
//        Log.d(TAG, "initView: "+mCategory.getName());

        mRecy.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mScrollTotal += dy;
                if (mScrollTotal <= 1) {
                    mInAtTop = true;
                } else {
                    mInAtTop = false;
                }
            }
        });

        mAdapter.setOnItemClickListener(new RecyclerAdapterWithHF.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerAdapterWithHF adapter, RecyclerView.ViewHolder vh, int position) {
//                Log.d(TAG, "onItemClick: "+position);
                // 通知MainFragment跳转至NewFeatureFragment
                MainFragment mainFragment = (MainFragment) getParentFragment().getParentFragment();
//                mainFragment.startBrotherFragment(NewFeatureFragment.newInstance());
                NewsModel model = (NewsModel) mData.get(position);
                if (!StringUtils.isEmpty(model.redirect_url)){
                    if (model.redirect_url.startsWith("http")){
                        mainFragment.showVue(ShowVueEvent.PAGE_BROWSER, model.id);
                    }else {
                        String vuePage = model.redirect_url.replace("#", "");
                        mainFragment.showVue(vuePage, model.id);
                    }
                }else {
                    mainFragment.showVue(ShowVueEvent.PAGE_ART_DETAIL, model.id);
                    EventBus.getDefault().post(new SaveArticleEvent(model));
                }
            }
        });

        mTvRecommend = view.findViewById(R.id.tvRecommend);
        init();
    }

    void loadData(){
        L.d(TAG, "loadData: "+mChannel);
        //不可见不加载
        if (homeFragment.currChannel != mChannel){ //!isVisible()无效 ？
//            return;
        }
        if (mChannel == null) return;
        Map<String, String> param = new HashMap<>();
        param.put("page",currPage+"");
        param.put("per_page", pageSize+"");
//        if (!mChannel.getName().equals("推荐"))
            param.put("channel_id", mChannel.getId());
            param.put("columnid", mChannel.getId());
        if (currPage == 1 && mChannel.getId().equals("0")){
            param.put("is_first", 1+"");

            if (mData.size() == 0) NetUtil.showLoading(2000); //加个延时，因为有启动动画
        }
        if (currPage <= 2){
            loadListAd();
        }

        String url = NetConfig.getUrlByParams(param, mAPI);
        OkHttpUtils.getAsyn(url, new JSONCallback() {
            @Override
            public void onError(Call call, Exception e) {
                NetUtil.dismissLoading();
                mPtrFrameLayout.refreshComplete();
            }

            @Override
            public void onResponse(JSONObject response) {
                NetUtil.dismissLoading();
                mPtrFrameLayout.refreshComplete();
                if (NetUtil.isSuccess(response)){
                    /** 变态的接口设计
                     * 没数据时
                     * {"code":0,"data":{"current_page":"1","data":[],"per_page":"10","total":false},"msg":"success",
                     * 有数据时
                     * {"code":0,"data":[{"author":"掘金宝","author_logo":"http://p3.pstatp.comge/","comment_mark":0,"comments":1,"id":4697650,
                     */
//                    System.out.println("onResponse:data= "+ response.toJSONString());
                    JSONArray array ;
                    if (response.get("data") instanceof JSONArray){
                        array = response.getJSONArray("data");
                    }else {
                        array = response.getJSONObject("data").getJSONArray("data");
                    }

//                    L.d(TAG, "onResponse:data.arr= "+ array.toJSONString());
                    List<NewsModel> temp = JSON.parseArray(array.toJSONString(), NewsModel.class);

                    if (currPage == 1){
                        boolean isNewest = compareRefresh(temp, mData);

                        mData.clear();
//                        mData = temp; //不能用这个赋值，adapter会监听不到数据有变化
                        mData.addAll(temp);
                        if (mData.size() > 5) mPtrFrameLayout.setLoadMoreEnable(true);

                        if(mData.size() > 0 && manuallyRefresh){
                            startRecommend(mData.size(), isNewest);
                        }
                    }else {  //更多
                        mData.addAll(temp);
                        mPtrFrameLayout.loadMoreComplete(temp.size()>0);
                    }
                    noDataView.setVisibility(mData.size() == 0? View.VISIBLE:View.GONE);
                    mAdapter.notifyDataSetChanged();
                }else {

                }
            }
        });
    }

    //是否最新推荐的
    boolean compareRefresh(List<NewsModel> temp, List<Object> origin){
        int orgSize = origin.size();
        if (orgSize == 0) return false;

        for (int i = 0; i<temp.size(); i++){
            Object obj = origin.get(i%orgSize);
            if (!(obj instanceof NewsModel)) return true;

            NewsModel model = (NewsModel) obj;
            if (!temp.get(i).id.equals(model.id)){
                return true;
            }
        }
        return false;
    }

    //加载穿山甲信息流广告
    void loadListAd(){
        //feed广告请求类型参数
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("920793469")
                .setSupportDeepLink(false)
                .setImageAcceptedSize(640, 320)
                .setAdCount(3)
                .build();

        //调用feed广告异步请求接口
        mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
                TToast.show(getContext(), "穿山甲异常："+message);
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    TToast.show(getContext(), "on FeedAdLoaded: ad is null!");
                    return;
                }
                Log.d(TAG, "onFeedAdLoad: "+ads);

//                for (int i = 0; i < LIST_ITEM_COUNT; i++) {
//                    mData.add(null);
//                }
                int count = mData.size();
                for (TTFeedAd ad : ads) {
                    int random = (int) (Math.random() * count);
                    mData.set(random, ad);
                }
            }
        });
    }

    private void startRecommend(int count, boolean newest){
        if (newest)
            mTvRecommend.setText(new StringBuilder("为您推荐").append(count).append("条更新"));
        else
            mTvRecommend.setText("暂无更新，请休息会再来~");

        AnimUtil.animHeightToView(getActivity(), mTvRecommend, true, 500);

        manuallyRefresh = false;
    }

    private void init() {

//        mPtrFrameLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mPtrFrameLayout.autoRefresh(true);
//            }
//        }, 150);

        mPtrFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                currPage = 1;
//                mData.clear();
//                     for (int i = 0; i < 17; i++)  mData.add(new String("  RecyclerView item  -" + i));
                if (noDataView.getVisibility() == View.VISIBLE) noDataView.setVisibility(View.GONE);

                manuallyRefresh = true;
                loadData();

            }
        });

        mPtrFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
//                 mData.add(new String("  RecyclerView item  - add " + page+"0"));
//                mAdapter.notifyDataSetChanged();
//                mPtrFrameLayout.loadMoreComplete(true);
                currPage++;
                loadData();
            }
        });
    }

    /**
     * Reselected Tab
     */
    @Subscribe
    public void onTabSelectedEvent(TabSelectedEvent event) {
        // mIsVisible 状态不对

        if (event.position == MainFragment.HOME && event.channelName.equals(mChannel.getName())){
            L.d(TAG, "onTabSelectedEvent: "+mChannel.getName());
            if (mInAtTop) {
                doRefresh();
            } else {
                scrollToTop();
            }
        }
    }

    private void scrollToTop() {
        mRecy.smoothScrollToPosition(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusActivityScope.getDefault(_mActivity).unregister(this);
    }
}
