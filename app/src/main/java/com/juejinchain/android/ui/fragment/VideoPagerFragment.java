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
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.juejinchain.android.R;
import com.juejinchain.android.adapter.VideoPagerAdapter;
import com.juejinchain.android.event.TabSelectedEvent;
import com.juejinchain.android.eventbus_activity.EventBusActivityScope;
import com.juejinchain.android.model.UserModel;
import com.juejinchain.android.model.VideoCategoryModel;
import com.juejinchain.android.model.VideoModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.network.OkHttpUtils;
import com.juejinchain.android.network.callBack.JSONCallback;
import com.juejinchain.android.tools.L;
import com.juejinchain.android.util.SPUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jzvd.Jzvd;
import me.yokeyword.fragmentation.SupportFragment;
import okhttp3.Call;


/**
 * 视频 pageFragment
 * implements SwipeRefreshLayout.OnRefreshListener 不用这个
 * 用pullToRefresh
 */
public class VideoPagerFragment extends SupportFragment  {
//    private SwipeRefreshLayout mRefreshLayout;
    PtrClassicFrameLayout ptrClassicFrameLayout;
    private RecyclerView mRecy;
    private ViewGroup noDataView; //空数据提示view

    private VideoPagerAdapter adapter;
    RecyclerAdapterWithHF mAdapter;
    final String TAG = VideoPagerFragment.class.getSimpleName();

    private boolean mInAtTop = true;
    private int mScrollTotal;
    Handler handler = new Handler();
    private List<VideoModel> mData = new ArrayList<>();
    //刷新不是从第一页开始, 而是+1, 然后缓存下来给下次用
    int currPage = 1;
    int pageSize = 10;
    public VideoCategoryModel mCategory;
    public String mAPI;
    VideoFragment homeFragment;
    TextView tvFabulous; //点赞

    /**
     * 视频频道页码缓存key前缀
     */
    public final String VC_PRE_KEY = "VC_KEY_";
    //保存的时间，超过4小时清空
    public final String VC_PRE_KEY_T = "VC_KEY_T_";
    private int mLastPage;
    private int mPerPage;
    private boolean isRefresh;
    private ImageView mImagLoadingBg;

    public static VideoPagerFragment newInstance(VideoCategoryModel model) {

        Bundle args = new Bundle();
        VideoPagerFragment fragment = new VideoPagerFragment();

        fragment.mCategory = model;
        fragment.mAPI = NetConfig.API_VideoList;
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_pager_first, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currPage = getLoadPage();
        homeFragment = (VideoFragment) getParentFragment();
        ptrClassicFrameLayout.autoRefresh(true);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    private void initView(View view) {
        EventBusActivityScope.getDefault(_mActivity).register(this);

        mRecy = view.findViewById(R.id.recy);
//        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
//        mRefreshLayout.setOnRefreshListener(this);
        ptrClassicFrameLayout = view.findViewById(R.id.ptr_recycler_view_frame);
        noDataView  = view.findViewById(R.id.ly_no_data);
        noDataView.setVisibility(View.GONE);
        mImagLoadingBg = view.findViewById(R.id.img_loading_bg);

        adapter = new VideoPagerAdapter(_mActivity, mData);

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
                if (mScrollTotal <= 0) {
                    mInAtTop = true;
                } else {
                    mInAtTop = false;
                }
            }
        });

        setListener();
        init();
    }

    void setListener(){

        mAdapter.setOnItemClickListener(new RecyclerAdapterWithHF.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerAdapterWithHF adapter, RecyclerView.ViewHolder vh, int position) {
//                Log.d(TAG, "onItemClick: "+position);
                VideoModel model = mData.get(position);
                // 通知MainFragment跳转至NewFeatureFragment
                ((MainFragment) getParentFragment().getParentFragment().getParentFragment())
                        .startBrotherFragment(VideoDetailFragment.newInstance(model));
//                Intent it = new Intent(getActivity(), LoginActivity.class);
//                getActivity().startActivity(it);

            }
        });


        //点赞、播放点击
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder holder) {
//                Log.d(TAG, "onItemClick: "+view);
                VideoModel model = mData.get(position);
                if(view instanceof ImageView){

                }else if (view instanceof TextView){
                    tvFabulous = (TextView) view;
                    doFabulous(tvFabulous, model);
                }

            }
        });

        mRecy.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                Jzvd jzvd = view.findViewById(R.id.jzvdPlayer);
                if (jzvd != null && jzvd.jzDataSource != null && Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.jzDataSource != null &&
                        jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.getCurrentUrl())) {
                    if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                        Jzvd.resetAllVideos();
                    }
                }
            }
        });

    }

    //请求点赞
    public void doFabulous(TextView tv, final VideoModel model){

        Map<String, String> param = new HashMap<>();
        param.put("vid", model.id);
        NetUtil.postRequestShowLoading(NetConfig.API_VideoFabulous, param, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                L.d(TAG, "onResponse:点赞 "+response.toJSONString());
                if (NetUtil.isSuccess(response)){
                    model.video_like_count += 1;
                    model.is_fabulous = true;
                    mAdapter.notifyDataSetChanged();
                }else {
                    if (response.getInteger("code") == NetConfig.NR_CODE_LOGIN_TIMEOUT){
                        UserModel.cleanData();
                        ((MainFragment) getParentFragment().getParentFragment().getParentFragment()).goLogin();
                    }
                }
            }
        }, true);


    }

    void loadData(){

        Map<String, String> param = new HashMap<>();
        param.put("page",currPage+"");
        param.put("per_page", pageSize+"");
        param.put("category", mCategory.en);

        String url = NetConfig.getUrlByParams(param, mAPI);
        OkHttpUtils.getAsyn(url, new JSONCallback() {
            @Override
            public void onError(Call call, Exception e) {
                ptrClassicFrameLayout.refreshComplete();
            }

            @Override
            public void onResponse(JSONObject response) {
                ptrClassicFrameLayout.refreshComplete();
                mImagLoadingBg.setVisibility(View.GONE);

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
                        //有数据
                        array = response.getJSONObject("data").getJSONArray("data");
                        mLastPage = response.getJSONObject("data").getInteger("last_page");
                    }

//                    L.d(TAG, "onResponse:data.arr= "+ array.toJSONString());
                    List<VideoModel> temp = JSON.parseArray(array.toJSONString(), VideoModel.class);

                    List<VideoModel> remove = new ArrayList<>();
                    for (VideoModel model:temp) {
                        //去除本地广告
                        if (model.getType() == 100)remove.add(model);
                    }
                    if (remove.size() > 0)temp.removeAll(remove);

                    if (isRefresh){
                        isRefresh = false;
                        mData.clear();
//                        mData = temp; //不能用这个赋值，adapter会监听不到数据有变化
                        mData.addAll(temp);

                        if (mData.size() > 5)ptrClassicFrameLayout.setLoadMoreEnable(true);
                    }else {  //更多
                        mData.addAll(temp);
                        ptrClassicFrameLayout.loadMoreComplete(temp.size()>0);
                    }
                    if (currPage >= mLastPage ) currPage = 1;
                    saveLoadPage();

                    noDataView.setVisibility(mData.size() == 0? View.VISIBLE:View.GONE);
                    mAdapter.notifyDataSetChanged();

                }else {

                }
            }
        });
    }

    void saveLoadPage(){
        SPUtils.getInstance().put(VC_PRE_KEY+mCategory.en, currPage );
        //记录时间
        SPUtils.getInstance().put(VC_PRE_KEY_T+mCategory.en, System.currentTimeMillis() );
    }

    /**
     * 获取之前加载的页码
     * @return
     */
    int getLoadPage(){
        long lastSaveTime = SPUtils.getInstance().getLong(VC_PRE_KEY_T + mCategory.en);
        if (System.currentTimeMillis() - lastSaveTime > 1000*60*60*4)
            return 1;

        //上次加载的页数
        int lastLoadedPage = SPUtils.getInstance().getInt(VC_PRE_KEY+mCategory.en );
        return lastLoadedPage == 0 ? 1:lastLoadedPage;
    }

    private void init() {

//        mPtrFrameLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mPtrFrameLayout.autoRefresh(true);
//            }
//        }, 150);

        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                if (noDataView.getVisibility() == View.VISIBLE)
                    noDataView.setVisibility(View.GONE);

                isRefresh = true;
                loadOrRefresh();
            }
        });

        ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
//                 mData.add(new String("  RecyclerView item  - add " + page+"0"));
//                mAdapter.notifyDataSetChanged();
//                mPtrFrameLayout.loadMoreComplete(true);
                loadOrRefresh();
            }
        });
    }

    //刷新和加载更多页码都是加一，因为视频页内容固定了
    private void loadOrRefresh() {
        currPage++;
        if (mLastPage > 0 && currPage > mLastPage) currPage = 1;
        loadData();
    }

    /**
     * Reselected Tab
     */
    @Subscribe
    public void onTabSelectedEvent(TabSelectedEvent event) {
        if (event.position != MainFragment.VIDEO) return;

        if (mInAtTop) {
//            mRefreshLayout.setRefreshing(true);
//            onRefresh();
        } else {
            scrollToTop();
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
