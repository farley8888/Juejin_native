package com.juejinchain.android.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.juejinchain.android.MainActivity;
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
import com.juejinchain.android.util.SPUtils;
import com.juejinchain.android.util.StringUtils;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yokeyword.fragmentation.SupportFragment;
import okhttp3.Call;


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

    int currPage = 1;
    int pageSize = 10;
    public ChannelModel mChannel;
    public String mAPI;
    HomeFragment homeFragment;
    private boolean mIsVisible;

    private TextView mTvRecommend;
    private boolean manuallyRefresh;

//    private TTAdNative mTTAdNative;

    final int AD_MAX_SIZE = 8;        //每页最多显示多少条广告
    final int AD_Interval = 5;        //间隔多少行显示一条广告
    int adLastPos = AD_Interval-1;    //广告插入位置
    /**
     * 每次请求的广告返回个数，最多支持3个
     */
    final int AD_Per_ReqCount = 2;

    private TTFeedAd lastUnShowADModel;     //上次未显示的广告
    ImageView imgLoadingBg;
    private int mTotal;
//    private int mLastPage;

    //文章频道列表当前加载页码，前缀
    private final String KEY_LoadPage = "AK_PRE_";

    public static HomePagerFragment newInstance(ChannelModel model) {

        Bundle args = new Bundle();
        HomePagerFragment fragment = new HomePagerFragment();

//        fragment.mChannel = model; //保存在bundle里面，这样直接赋值，从子页闪退回来会变null
        args.putString("model", JSON.toJSONString(model));

        //推荐和热门频道用同一个接口
        if(model != null && "0,1".contains(model.getId()+"") ){
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

        if (mAPI != NetConfig.API_NewsPull)
            currPage = SPUtils.getInstance().getInt(KEY_LoadPage+mChannel.getId(), 1);

        homeFragment = (HomeFragment) getParentFragment();

        //初始化广告管理对象
//        TTAdManager ttAdManager = TTAdManagerHolder.get();
//        mTTAdNative = ttAdManager.createAdNative(getApplicationContext());
        //申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
//        TTAdManagerHolder.get().requestPermissionIfNecessary(getApplicationContext());
        isFirst = true;
        imgLoadingBg.setVisibility(View.VISIBLE);
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
//        mPtrFrameLayout.autoRefresh(true);
        manuallyRefresh = true;
        prepareRefresh();
    }

    void prepareRefresh(){
//        if (mAPI == NetConfig.API_NewsPull)
//            currPage = 1;
//        else{
            currPage++;  //频道刷新为++
//        }

        loadData();
    }

    private void initView(View view) {
        EventBusActivityScope.getDefault(_mActivity).register(this);
        imgLoadingBg = view.findViewById(R.id.img_loading_bg);

        mRecy = view.findViewById(R.id.recy);
//        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
//        mRefreshLayout.setOnRefreshListener(this);
        mPtrFrameLayout = view.findViewById(R.id.ptr_recycler_view_frame);
        noDataView  = view.findViewById(R.id.ly_no_data);
        noDataView.setVisibility(View.GONE);

        adapter = new HomePagerAdapter(_mActivity, mData);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        //设置分割线
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.list_div_line));
        mRecy.addItemDecoration(itemDecoration);

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
//
                if (!(mData.get(position) instanceof NewsModel)) return;

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

    /**
     * 文章加载，下拉刷新不清空列表
     * 加载更多要保存页码，下次进入使用
     */
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

        param.put("channel_id", mChannel.getId()+"");
        param.put("columnid", mChannel.getId()+"");

        if (currPage == 1 && mChannel.getId() ==  ChannelModel.ID_RECOMMEND){
            param.put("is_first", 1+"");

            if (mData.size() == 0) NetUtil.showLoading(500); //加个延时，因为有启动动画
        }
        if (currPage == 1){
            adLastPos = AD_Interval-1;
        }

        String url = NetConfig.getUrlByParams(param, mAPI);
        OkHttpUtils.getAsyn(url, new JSONCallback() {
            @Override
            public void onError(Call call, Exception e) {
                NetUtil.dismissLoading();
                mPtrFrameLayout.refreshComplete();
//                imgLoadingBg.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(JSONObject response) {
                NetUtil.dismissLoading();
                mPtrFrameLayout.refreshComplete();
                imgLoadingBg.setVisibility(View.GONE);

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
                        JSONObject jo = response.getJSONObject("data");
                        array = jo.getJSONArray("data");
//                        mTotal = response.getInteger("total"); 有些频道接口没有此字段
//                        mLastPage = response.getJSONObject("data").getInteger(""); 文章接口没有页码
                    }

//                    L.d(TAG, "loadDataTime 31: ");
//                  //model里面不要用太耗时的解析，否则等待太久，如正则, 还要优化，因为放半小时后重新加载又慢了
                    List<NewsModel> temp = JSON.parseArray(array.toJSONString(), NewsModel.class);
//                    L.d(TAG, "loadDataTime 32: ");

                    List<NewsModel> remove = new ArrayList<>();
                    for (NewsModel model: temp){
                        if (model.type == 100)remove.add(model);
                    }
                    //移除本平台广告
                    if (remove.size() > 0)temp.removeAll(remove);


                    mPtrFrameLayout.setLoadMoreEnable(temp.size() > 4);
//                     mPtrFrameLayout.loadMoreComplete(temp.size() < 8 ); //第一页调用无效
                    if (manuallyRefresh){
                        boolean isNewest = compareRefresh(temp, mData);

//                        mData.clear(); //下拉刷新不清空 ,并加到首位
                        mData.addAll(0,temp); // mData = temp; 不能用这个赋值，adapter会监听不到数据有变化

                        if(mData.size() > 0 && manuallyRefresh){
                            startRecommend(temp.size(), isNewest);
                        }
                        manuallyRefresh = false;

                    }else {  //更多
                        mData.addAll(temp);
                        mPtrFrameLayout.loadMoreComplete(temp.size()>0);
                    }

                    if (mData.size() > AD_Interval && adLastPos/AD_Interval < AD_MAX_SIZE
                            && mChannel.getId() != ChannelModel.ID_JJB){
                        loadListAd(); //加载广告
                    }

                    noDataView.setVisibility(mData.size() == 0? View.VISIBLE:View.GONE);
                    mAdapter.notifyDataSetChanged();

                    if (temp.size() == 0) currPage = 1; //如果没有数据了
                    saveCurrPage();

                }else {
                    //待处理
                }
            }
        });
    }

    private void saveCurrPage() {
        if (mAPI != NetConfig.API_NewsPull)
            SPUtils.getInstance().put(KEY_LoadPage+mChannel.getId(), currPage);
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
        /**
         * 头条联盟信息流广告大图的尺寸为（690px*388px）、小图尺寸为（228px*150px）、组图为（228px*150px*3）
         */
        //feed广告请求类型参数
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(TTAdManagerHolder.POS_ID)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 320) //加载返回类型是随机的，通过参数也改不了
                .setAdCount(AD_Per_ReqCount) // 可选参数，针对信息流广告设置每次请求的广告返回个数，最多支持3个
                .build();

        //调用feed广告异步请求接口
        MainActivity.mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int code, String message) {
//                TToast.show(getContext(), "穿山甲异常："+message);
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                if (ads == null || ads.isEmpty()) {
//                    TToast.show(getContext(), "on FeedAdLoaded: ad is null!");
                    return;
                }
                L.d(TAG, "onFeedAdLoad: "+ads);
                if (lastUnShowADModel != null){
                    ads.add(0, lastUnShowADModel);
                    lastUnShowADModel = null;
                }

                for (int pos = 0 ; pos < ads.size(); pos++) {
                    TTFeedAd ad = ads.get(pos);
                    //视频有问题，先不加载
                    if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO)
                        continue;

                    while (mData.get(adLastPos) instanceof TTFeedAd){
                        adLastPos += AD_Interval;
                        if (adLastPos >= mData.size()){
//                            adLastPos = mData.size()-1;
//                            break;
                            lastUnShowADModel = ad;
                            return;
                        }
                    }
//                    int random = (int) (Math.random() * count); 随机
                    mData.set(adLastPos, ad);
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

//      mPtrFrameLayout.autoRefresh(true); 自动加载

        mPtrFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
//                mData.clear();
//                     for (int i = 0; i < 17; i++)  mData.add(new String("  RecyclerView item  -" + i));
                if (noDataView.getVisibility() == View.VISIBLE) noDataView.setVisibility(View.GONE);

                manuallyRefresh = true;
                prepareRefresh();

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
                doRefresh();
//                mRecy.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                        //newState分 0,1,2三个状态,2是滚动状态,0是停止
//                        super.onScrollStateChanged(recyclerView, newState);
//                        //-1代表顶部,返回true表示没到顶,还可以滑
//                        //1代表底部,返回true表示没到底部,还可以滑
//                        boolean b = recyclerView.canScrollVertically(-1);
//                        if(!b){
//
//                        }
//                    }
//
//                    @Override
//                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                        super.onScrolled(recyclerView, dx, dy);
//
//                    }
//                });
            }
        }
    }

    private void scrollToTop() {
        mRecy.scrollToPosition(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusActivityScope.getDefault(_mActivity).unregister(this);
    }
}
