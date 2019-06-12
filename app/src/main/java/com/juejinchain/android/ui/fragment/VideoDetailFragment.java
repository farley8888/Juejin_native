package com.juejinchain.android.ui.fragment;

import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.juejinchain.android.R;
import com.juejinchain.android.adapter.VideoDetailXAdapter;
import com.juejinchain.android.base.BaseBackFragment;
import com.juejinchain.android.base.XRecyclerViewAdapter;
import com.juejinchain.android.event.ShareEvent;
import com.juejinchain.android.event.ShowVueEvent;
import com.juejinchain.android.event.VideoDetailEvent;
import com.juejinchain.android.model.CommentModel;
import com.juejinchain.android.model.UserModel;
import com.juejinchain.android.model.VideoModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.tools.L;
import com.juejinchain.android.tools.WebViewUtil;
import com.juejinchain.android.ui.dialog.ShareDialog;
import com.juejinchain.android.ui.ppw.TimeRewardPopup;
import com.juejinchain.android.ui.view.DividerDecoration;
import com.juejinchain.android.util.KeyboardUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;
import me.yokeyword.fragmentation.ISupportFragment;


/**
 * 视频详情fragment
 * <p>
 *
 */
public class VideoDetailFragment extends BaseBackFragment {

    private String TAG = "VideoDetailFragment";
    public VideoModel model;
    private RecyclerView mRecyclerView;
    private VideoDetailXAdapter mAdapter;

    JzvdStd mJzvdPlayer;
    private WebView parseWebView;
    List<Object> mData = new ArrayList<>();
    //推荐列表
    List<VideoModel> recommendList = new ArrayList<>();

    //评论列表参数
    List<CommentModel> commentList = new ArrayList<>();
    private int currPage = 1;
    private int pageSize = 10;
    //评论总条数
    private int totalSize;
    ReplyCommentPopup mCommentPopup;
    private EditText mEditView;
    ShareDialog mShareDialog;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            questReadRewardApi();
        }
    };
    private final int READ_What = 122;
    private int readTimes       = 30*1000; //阅读30秒才请求奖励接口


    public static VideoDetailFragment newInstance(VideoModel model) {
        VideoDetailFragment self = new VideoDetailFragment();
        self.model = model;

        return self;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_detail, container, false);

//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        initToolbarNav(toolbar);
//        toolbar.setTitle("视频详情");
        initToolbar(view, "视频详情");

        mJzvdPlayer = view.findViewById(R.id.jzvdStdPlayer);
        mHandler.sendEmptyMessageDelayed(READ_What, readTimes);

        initView(view);
        parseWebView = new WebView(getContext());
        setViewData();
        return attachToSwipeBack(view);
    }

    //请求奖励接口
    private void questReadRewardApi(){
        Map<String, String> param = new HashMap<>();
        param.put("aid", model.id);
        param.put("type", "video");
        NetUtil.postRequest(NetConfig.API_NewsReading, param, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (NetUtil.isSuccess(response)){
                    JSONObject jo = response.getJSONObject("data");
                    TimeRewardPopup popup = new TimeRewardPopup(getContext(), TimeRewardPopup.TYPE_VIDEO);
                    popup.setView(jo);
                    popup.showPopupWindow();
                }
            }
        }, true);

    }


    /**
     * 点击推荐列表时重新加载数据
     */
    private void setViewData(){
        if (model.src == null){
            setPlayHtml(model, parseWebView);
        }
        mJzvdPlayer.setUp(model.src, model.title);
        Glide.with(getContext()).load(model.large_img_url).into(mJzvdPlayer.thumbImageView);
        mData.add(model);
        mAdapter.setDataList(mData);
        loadRecommend();
    }

    //加载推荐列表
    void loadRecommend(){
        Map<String, String> param = new HashMap<>();
        NetUtil.getRequest(NetConfig.API_VideoRecommend, param, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
//                L.d(TAG, "onResponse: "+response.toJSONString());
                recommendList = JSON.parseArray(response.getString("data"), VideoModel.class);
                mData.addAll(1, recommendList);
                mAdapter.setDataList(mData); //此adapter这个方法才能刷新
//                mAdapter.notifyDataSetChanged();
                loadComment();
            }
        },false);
    }

    //加载详情（和列表的一样，可不用请求
    void loadDetail(){
        Map<String, String> param = new HashMap<>();
        param.put("vid", model.id);
        NetUtil.getRequest(NetConfig.API_VideoDetail, param, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                L.d(TAG, "onResponseDetail: "+response.toJSONString());
            }
        },false);
    }

    //加载评论列表
    void loadComment(){
        Map<String, String> param = new HashMap<>();
        param.put("vid", model.id);
        param.put("per_page", pageSize+"");
        param.put("currPage", currPage +"");
        NetUtil.getRequest(NetConfig.API_VideoCommentList, param, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
//                L.d(TAG, "onResponse: "+response.toJSONString());
                response = response.getJSONObject("data");
                totalSize = response.getInteger("total");
                commentList = JSON.parseArray(response.getString("data"), CommentModel.class);
                mData.addAll( commentList);
                mAdapter.setDataList(mData); //xadapter用这个方法才能刷新
                //减去推荐及头部
                if (totalSize > mData.size() - recommendList.size() -1){
                    mAdapter.isLoadMore(true);
                }else {
                    if (totalSize > 0)  mAdapter.showLoadComplete();
                    else mAdapter.isLoadMore(false);
                }
            }
        },false);
    }

    void showShareDialog(){
        if(mShareDialog == null){
            mShareDialog = new ShareDialog(getContext(), ShareEvent.TYPE_VIDEO, model.id);
        }
        mShareDialog.show();
        mShareDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //查看攻略
                if (view.getId() == R.id.img_share_seeFanc){
                    ISupportFragment temp = getPreFragment();
                    if (temp instanceof VideoDetailFragment){  //多次调用,如果上一个没pop或手动返回，会出现这种情况
                        pop();
                    }else {
                        MainFragment mainFragment = (MainFragment)temp ;
                        mainFragment.videoDetailFragment = VideoDetailFragment.this;

                        //mainFragment.webAppFragment 显示frag unable sub of parent
                        showHideFragment(mainFragment, VideoDetailFragment.this);
                        mainFragment.showVue(ShowVueEvent.PAGE_LOCK_FAN, "");

                    }
                }
            }
        });
    }

    boolean isLogin(){
        if (UserModel.isLogin()){
            return true;
        }
        ISupportFragment temp = getPreFragment();
        if (temp instanceof VideoDetailFragment){
            pop();
        }else {
            MainFragment mainFragment = (MainFragment) temp;
            mainFragment.videoDetailFragment = VideoDetailFragment.this;

            //mainFragment.webAppFragment 显示frag can't used sub of parent
            showHideFragment(mainFragment, VideoDetailFragment.this);
            mainFragment.goLogin();
        }
        return false;
    }

    @Override
    protected void topbarRightButtonClick() {
        Log.d(TAG, "topbarRightButtonClick: ");
        if (isLogin()) showShareDialog();
//        new ShareDialog(getContext()).show();

    }

    public void initView(View view){
        mRecyclerView = view.findViewById(R.id.recycler_view);

        mRecyclerView.addItemDecoration(new DividerDecoration(Color.parseColor("#C4C4C4"), 1));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

//        mAdapter = new VideoDetailAdapter(getContext());
        mAdapter = new VideoDetailXAdapter(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

        mEditView = view.findViewById(R.id.edt_btmComment);
        mEditView.setInputType(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        mEditView.setSingleLine(false);
        setListener();
        bindBottomListener(view);
//        mAdapter.isLoadMore(true);
    }

    void bindBottomListener(View v){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btn_btmBack:
                        _mActivity.onBackPressed();
                        break;
                    case R.id.btn_btmComment: //底部评论数按钮

                        break;
                    case R.id.btn_btmShare:  //分享
                        if (isLogin())
                            showShareDialog();
                        break;
                }
            }
        };
        v.findViewById(R.id.btn_btmBack).setOnClickListener(listener);
        v.findViewById(R.id.btn_btmShare).setOnClickListener(listener);
        v.findViewById(R.id.btn_btmComment).setOnClickListener(listener);

//        mEditView.setOnClickListener(listener); editView要点击两次才触发,通过焦点监听
        mEditView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                Log.d(TAG, "onEditorAction:actionId= "+actionId);
                if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE) {
                   String content = mEditView.getText().toString().trim();
                    Log.d(TAG, "onEditorAction: "+content);
                   if (content.length() < 2){
                       Toast.makeText(getContext(), "请输入完整的评论", Toast.LENGTH_SHORT).show();
                       return true;
                   }else{
                       publishComment(content);
                   }
                    handled = true;
                    /*隐藏软键盘*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    }
                }
                return handled;
//                return false;
            }
        });
        mEditView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (!isLogin()){
                        mEditView.clearFocus();
                        hideSoftInput(); //未登录隐藏键盘
                    }
                }
            }
        });
    }

    void setListener(){
        //点击事件
        mAdapter.setOnItemClickListener(new XRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
//                Toast.makeText(getContext(),"点击第" + position, Toast.LENGTH_SHORT).show();
                //点击了推荐视频
                if (position > 0 && position < recommendList.size()+1){
                    model = recommendList.get(position-1);
                    mData.clear();
                    setViewData();
                }

            }
        });

        //评论、点赞、分享、回复点击
        mAdapter.setMyItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, RecyclerView.ViewHolder vh) {
                TextView tv = (TextView) view;
                L.d(TAG, "onItemClick: "+tv.getText()+",position="+position);

                if (view.getId() != R.id.button1 && view.getId() != R.id.btn_cmtReply){
                    //需要登录时
                    if (!isLogin()) return;
                }
                switch (view.getId()){
                    case R.id.button1:     //定位到评论

                        break;
                    case R.id.buttonZan:  //点赞视频
                        VideoPagerFragment frg = (VideoPagerFragment) getParentFragment();
//                        frg.doFabulous(tv, model); nullPointerException ??
                        doFabulous(tv, model);
                        break;
                    case R.id.buttonShare://分享
                        showShareDialog();
                        break;
                    case R.id.btn_cmtReply: //回复评论
                        CommentModel cmodel = commentList.get(position - recommendList.size() -1);
                        cmodel.vid = model.id;
//                        Log.d(TAG, "onItemClick: cid="+cmodel.cid);
                        mCommentPopup = new ReplyCommentPopup(getContext(),cmodel);
                        mCommentPopup.showPopupWindow();
                        break;
                    case R.id.btn_cmtZan:   //点赞评论
                        doFabulous(tv, commentList.get(position - recommendList.size() -1));
                        break;
                }
            }
        });

        mAdapter.setOnLoadMoreListener(new XRecyclerViewAdapter.OnLoadMoreListener() {
            @Override
            public void onRetry() {//加载失败，重新加载回调方法
//                toast("11");
                mAdapter.showLoadError();//显示加载错误
            }

            @Override
            public void onLoadMore() {//加载更多回调方法
                currPage++;
                loadComment();
                ///模拟几种错误情况和正确情况
//                int random = new Random().nextInt(10);
//                if (random < 3) {
//                    mAdapter.showLoadError();//显示加载错误
//                } else if (random >= 3 && random < 6) {
////                    mAdapter.showLoadComplete();//没有更多数据了
//                } else {
////                    mAdapter.add(mData.size(), "新增加");//加载更多
//                }
            }
        });
    }

    //请求点赞
    public void doFabulous(TextView tv, final Object aModel) {
        Map<String, String> param = new HashMap<>();
        param.put("vid", model.id);
        if (aModel instanceof CommentModel){
            CommentModel cmodel = (CommentModel) aModel;
            param.put("cid", cmodel.cid);

        }

        NetUtil.postRequestShowLoading(NetConfig.API_VideoFabulous, param, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.d(TAG, "onResponse:点赞 " + response.toJSONString());
                if (aModel instanceof VideoModel){
                    model.video_like_count += 1;
                    model.is_fabulous = true;
                }else {  //评论点赞
                    CommentModel cmodel = (CommentModel) aModel;
                    cmodel.is_fabulous = true;
                    cmodel.fabulous += 1;
                }
                mAdapter.notifyDataSetChanged();
            }
        }, false);
    }

    //发表评论
    public void publishComment(String content) {
        mEditView.setText("");
        Map<String, String> param = new HashMap<>();
        param.put("vid", model.id);
        param.put("content", content);
        NetUtil.postRequestShowLoading(NetConfig.API_VideoComment, param, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse:发评论 " + response.toJSONString());
                Toast.makeText(getContext(),"发表成功", Toast.LENGTH_SHORT).show();

                List<Object> removeComment = mData.subList(5, mData.size());
                mData.removeAll(removeComment);
                mAdapter.notifyDataSetChanged();
                //清除评论后重新加载
                currPage = 1;
                loadComment();
            }
        }, false);
    }

    void setPlayHtml(final VideoModel model, WebView webView){
        Log.d(TAG, "setPlayHtml: ");
        String url = "file:///android_asset/video.html";

        WebViewUtil.setWebView(getContext(), webView);
//        WebViewUtil.setX5WebView(parseWebView);
        webView.loadUrl(url);
        webView.addJavascriptInterface(new WebViewParseClass(), "app");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String startJs = String.format("javascript:setVideoContent('%s','%s')",model.video_id,model.img_url);
                Log.d(TAG, "onPageFinished: "+startJs);
                webView.loadUrl(startJs);
//                parseWebView.setLayoutParams(holder.imgVideo.getLayoutParams());
            }
        });
    }

    class WebViewParseClass {

        @JavascriptInterface
        public void parseData(String src, int height){ //
            L.d(TAG, "parseData: "+height +", src=" +src);
            model.src = src;
            mJzvdPlayer.post(new Runnable() {
                @Override
                public void run() {
                    mJzvdPlayer.setUp(src, model.title, Jzvd.SCREEN_NORMAL);
                    Glide.with(getContext()).load(model.large_img_url).into(mJzvdPlayer.thumbImageView);
                }
            });

        }
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        // 懒加载
        // 同级Fragment场景、ViewPager场景均适用
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        // 当对用户可见时 回调
        // 不管是 父Fragment还是子Fragment 都有效！
    }


    public boolean onBackPressedSupport() {
        Log.d(TAG, "onBackPressedSupport: ");
        MainFragment mainFragment = (MainFragment) getPreFragment();
        showHideFragment(mainFragment);
        mainFragment.showHomeFragment();
        return super.onBackPressedSupport();
    }


    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        Jzvd.resetAllVideos();
        // 当对用户不可见时 回调
        // 不能在子fragment调用pre的view切换方法
//        ((MainFragment)getPreFragment()).showVue(ShowVueEvent.PAGE_LOCK_FAN, "");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //多次showHideFragment时，有时是self
//        MainFragment mainFragment = (MainFragment) getPreFragment();
        EventBus.getDefault().post(new VideoDetailEvent());
        mHandler.removeMessages(READ_What);
    }
}
