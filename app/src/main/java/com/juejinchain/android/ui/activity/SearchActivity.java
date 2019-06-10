package com.juejinchain.android.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.juejinchain.android.MainActivity;
import com.juejinchain.android.R;
import com.juejinchain.android.event.ShowVueEvent;
import com.juejinchain.android.event.UpdateChannelEvent;
import com.juejinchain.android.model.NewsModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.network.OkHttpUtils;
import com.juejinchain.android.network.callBack.JSONCallback;
import com.juejinchain.android.ui.fragment.PagerAdapter;
import com.juejinchain.android.ui.view.AlertProDialog;
import com.juejinchain.android.util.KeyboardUtil;
import com.juejinchain.android.util.SPUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 搜索页面
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 刷新layout
     */
    private PtrClassicFrameLayout mPtrLayout;

    /**
     * recycle
     */
    private RecyclerView mRecycleView;

    /**
     * 历史记录
     */
    private FrameLayout mHistoryLayout;

    /**
     * 搜索历史为空
     */
    private FrameLayout mHistoryEmpty;

    /**
     * 搜索历史
     */
    private RecyclerView mHistoryRecycle;

    /**
     * search
     */
    private EditText mEditSearch;

    /**
     * empty load data view
     */
    private View mEmptyLoadView;

    /**
     * curr page
     */
    private int mCurrpage = 1;

    /**
     * page size
     */
    public final int PAGE_SIZE = 10;

    /**
     * data
     */
    private List<NewsModel> mData = new ArrayList<>();

    /**
     * adapter
     */
    private RecyclerAdapterWithHF mAdapter;

    private List<String> mHistoryList = new ArrayList<>();
    private List<String> mHistoryFilter = new ArrayList<>();

    private RecyclerView.Adapter mHistoryAdapter;
    private ShowVueEvent mShowVueEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initData();
    }

    private void initView(){
        mPtrLayout = findViewById(R.id.ptr_layout);
        mRecycleView = findViewById(R.id.recy);
        mEmptyLoadView = findViewById(R.id.ly_no_data);
        mEditSearch = findViewById(R.id.edt_search);

        mHistoryLayout = findViewById(R.id.history_layout);
        mHistoryEmpty = findViewById(R.id.search_empty);
        mHistoryRecycle = findViewById(R.id.history_view);
    }

    private void initEvent(){
        setEditerListener();

        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);

        mPtrLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                performRefresh();
            }
        });
        mPtrLayout.setOnLoadMoreListener(() -> {
            mCurrpage ++;
            loadData();
        });
        mPtrLayout.setAutoLoadMoreEnable(true);

        mAdapter = new RecyclerAdapterWithHF(new PagerAdapter(this, mData));
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RecyclerAdapterWithHF.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerAdapterWithHF adapter, RecyclerView.ViewHolder vh, int position) {
//                MainActivity mainActivity = (MainActivity) getParent(); 空的
                mShowVueEvent = new ShowVueEvent(ShowVueEvent.PAGE_ART_DETAIL, mData.get(position).id);
//                EventBus.getDefault().post(mShowVueEvent);
                finish();
            }
        });

        mHistoryRecycle.setLayoutManager(new LinearLayoutManager(this));
        mHistoryAdapter = new RecyclerView.Adapter<HistoryViewHolder>() {
            @NonNull
            @Override
            public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new HistoryViewHolder(LayoutInflater.from(SearchActivity.this).inflate(R.layout.item_search_history, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull HistoryViewHolder viewHolder, int i) {
                String text = mHistoryFilter.get(i);
                viewHolder.bindView(text);
            }

            @Override
            public int getItemCount() {
                return mHistoryFilter.size();
            }
        };
        mHistoryRecycle.setAdapter(mHistoryAdapter);
    }

    void setEditerListener(){

        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mHistoryFilter.clear();
                if(s.length() > 0){
                    for (String history : mHistoryList){
                        if(history.contains(s)){
                            mHistoryFilter.add(history);
                        }
                    }
                    mHistoryAdapter.notifyDataSetChanged();
                    mHistoryLayout.setVisibility(mHistoryFilter.isEmpty() ? View.GONE : View.VISIBLE);

                    mData.clear();
                    mAdapter.notifyDataSetChanged();
                }else if(mHistoryList.isEmpty()){
                    mHistoryLayout.setVisibility(View.GONE);
                }else{
                    mHistoryLayout.setVisibility(View.VISIBLE);
                    mHistoryEmpty.setVisibility(View.GONE);
                    mHistoryFilter.addAll(mHistoryList);
                    mHistoryAdapter.notifyDataSetChanged();
                }
                mPtrLayout.setLoadMoreEnable(false);
            }
        });

        mEditSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE) {
                    String content = textView.getText().toString().trim();
//                    Log.d(TAG, "onEditorAction: "+content);
                    if (content.length() < 2){
                        Toast.makeText(SearchActivity.this, "请输入完整的字段", Toast.LENGTH_SHORT).show();
                        return true;
                    }else{
                       doSearch();
                    }
                    handled = true;
                    /*隐藏软键盘*/
//                    KeyboardUtil.hide(getApplicationContext());
                }
                return handled;
            }
        });

    }

    private void initData(){
        String histories = SPUtils.getInstance().getString("search_history");
        if(TextUtils.isEmpty(histories)){
            mHistoryEmpty.setVisibility(View.VISIBLE);
        }else{
            mHistoryEmpty.setVisibility(View.GONE);
            List<String> list = Arrays.asList(histories.split(","));
            for (int i = 0; i < list.size(); i++){
                mHistoryFilter.add(list.get(i));
            }
            mHistoryList.addAll(mHistoryFilter);
            mHistoryAdapter.notifyDataSetChanged();
        }
    }

    private void performRefresh(){
        mCurrpage = 1;
        loadData();
    }

    private void loadData(){
        String keys = mEditSearch.getText().toString();
        if(TextUtils.isEmpty(keys)){
            return;
        }

        Map<String,String> params = new HashMap<>(3);
        params.put("kw", keys);
        params.put("page", String.valueOf(mCurrpage));

        NetUtil.getRequest(NetConfig.API_NewsSearch, params, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (NetUtil.isSuccess(response)){
                    JSONArray array ;
                    if (response.get("data") instanceof JSONArray){
                        array = response.getJSONArray("data");
                    }else {
                        array = response.getJSONObject("data").getJSONArray("data");
                    }

                    int totalPage = response.getJSONObject("data").getIntValue("last_page");

                    List<NewsModel> list = JSON.parseArray(array.toJSONString(), NewsModel.class);
                    if (mCurrpage == 1){
                        mData.clear();
                        mData.addAll(list);
                        mPtrLayout.refreshComplete();
                    }else {  //更多
                        mData.addAll(list);
                        mPtrLayout.loadMoreComplete(list.size() > 0);
                    }
//                    mPtrLayout.setLoadMoreEnable(totalPage != mCurrpage);

                    mAdapter.notifyDataSetChanged();
                    mEmptyLoadView.setVisibility(mData.size() == 0 ? View.VISIBLE : View.GONE);
                }
            }
        }, true);

        mEmptyLoadView.setVisibility(View.GONE);
        mHistoryLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_search:
                if (mEditSearch.getText().toString().trim().length() < 2){

                    Toast.makeText(this, "请输入完整的搜索内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                doSearch();
                break;
            case R.id.btn_clear:
                new AlertDialog.Builder(this)
                        .setCancelable(true)
                        .setMessage("确认清空输入记录?")
                        .setPositiveButton("清空", (dialog, which) -> {
                            dialog.dismiss();
                            SPUtils.getInstance().put("search_history", "");
                            mHistoryList.clear();
                            mHistoryFilter.clear();
                            mHistoryAdapter.notifyDataSetChanged();
                            mHistoryEmpty.setVisibility(View.VISIBLE);
                        })
                        .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
                break;
        }
    }

    void doSearch(){
        saveHistory();
        AlertProDialog.showLoading(true);
        loadData();
    }

    private void saveHistory(){
        KeyboardUtil.hide(this);
        String keyword = mEditSearch.getText().toString();
        if(mHistoryList.contains(keyword)){
            mHistoryList.remove(keyword);
        }
        mHistoryList.add(0, keyword);

        StringBuilder builder = new StringBuilder();
        for (int i = 0 ; i < mHistoryList.size() ; i ++){
            builder.append(mHistoryList.get(i)).append(",");
        }
        SPUtils.getInstance().put("search_history", builder.substring(0, builder.lastIndexOf(",")));
    }

    private class HistoryViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }

        public void bindView(String text){
            textView.setText(text);
            itemView.setOnClickListener(v -> {
                mEditSearch.setText(text);
                mEditSearch.setSelection(text.length());
                saveHistory();
                NetUtil.showLoading(null);
                performRefresh();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mShowVueEvent != null){
            EventBus.getDefault().post(mShowVueEvent);
        }
    }
}
