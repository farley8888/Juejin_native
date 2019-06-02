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
import android.webkit.WebView;
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
import com.juejinchain.android.model.NewsModel;
import com.juejinchain.android.model.VideoCategoryModel;
import com.juejinchain.android.model.VideoModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.network.OkHttpUtils;
import com.juejinchain.android.network.callBack.JSONCallback;

import org.greenrobot.eventbus.Subscribe;
import org.w3c.dom.Text;

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
    int currPage = 0;
    int pageSize = 10;
    public VideoCategoryModel mCategory;
    public String mAPI;
    VideoFragment homeFragment;
    TextView tvFabulous; //点赞

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
        currPage = 1;
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
        ptrClassicFrameLayout = view.findViewById(R.id.test_recycler_view_frame);
        noDataView  = view.findViewById(R.id.ly_no_data);
        noDataView.setVisibility(View.GONE);

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
                Log.d(TAG, "onResponse:点赞 "+response.toJSONString());
                model.video_like_count += 1;
                model.is_fabulous = true;
                mAdapter.notifyDataSetChanged();
            }
        }, false);

//        String url = NetConfig.getUrlByAPI(NetConfig.API_VideoFabulous);
//        OkHttpUtils.postAsyn(url, param, new JSONCallback() {
//            @Override
//            public void onError(Call call, Exception e) {
//
//            }
//
//            @Override
//            public void onResponse(JSONObject response) {
//
//            }
//        });

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

            }

            @Override
            public void onResponse(JSONObject response) {
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
//                    if (array.size() == 0) return;  //没有数据时

                    Log.d(TAG, "onResponse:data.arr= "+ array.toJSONString());
                    List<VideoModel> temp = JSON.parseArray(array.toJSONString(), VideoModel.class);

                    if (currPage == 1){
                        mData.clear();
//                        mData = temp; //不能用这个赋值，adapter会监听不到数据有变化
                        mData.addAll(temp);
                        ptrClassicFrameLayout.refreshComplete();
                        if (mData.size() > 5)ptrClassicFrameLayout.setLoadMoreEnable(true);
                    }else {  //更多
                        mData.addAll(temp);
                        ptrClassicFrameLayout.loadMoreComplete(temp.size()>0);
                    }
                    noDataView.setVisibility(mData.size() == 0? View.VISIBLE:View.GONE);
                    mAdapter.notifyDataSetChanged();
                }else {

                }
            }
        });
    }

    private void init() {

//        ptrClassicFrameLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ptrClassicFrameLayout.autoRefresh(true);
//            }
//        }, 150);

        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                currPage = 1;
//                mData.clear();
//                     for (int i = 0; i < 17; i++)  mData.add(new String("  RecyclerView item  -" + i));
                if (noDataView.getVisibility() == View.VISIBLE) noDataView.setVisibility(View.GONE);
                loadData();

            }
        });

        ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
//                 mData.add(new String("  RecyclerView item  - add " + page+"0"));
//                mAdapter.notifyDataSetChanged();
//                ptrClassicFrameLayout.loadMoreComplete(true);
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
        if (event.position != MainFragment.SECOND) return;

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
