package com.juejinchain.android.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.juejinchain.android.R;
import com.juejinchain.android.event.ShowVueEvent;
import com.juejinchain.android.model.NewsModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.ui.fragment.PagerAdapter;
import com.juejinchain.android.ui.view.AlertProDialog;
import com.juejinchain.android.ui.view.More_LoadDialog;
import com.juejinchain.android.util.KeyboardUtil;
import com.juejinchain.android.util.SPUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //分割线
    private RecyclerView.ItemDecoration itemDecoration;

    private boolean iffilter=false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 1 :
//                    NetUtil.showLoading(null);

                    break;
            }
        }
    };

    private static More_LoadDialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initData();
        dialog = new More_LoadDialog(this);
    }

    private void initView() {
        mPtrLayout = findViewById(R.id.ptr_layout);
        mRecycleView = findViewById(R.id.recy);
        mEmptyLoadView = findViewById(R.id.ly_no_data);
        mEditSearch = findViewById(R.id.edt_search);

        mHistoryLayout = findViewById(R.id.history_layout);
        mHistoryEmpty = findViewById(R.id.search_empty);
        mHistoryRecycle = findViewById(R.id.history_view);

        itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//默认分割线
        mHistoryRecycle.addItemDecoration(itemDecoration);

    }

    private void initEvent() {
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
            mCurrpage++;
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
                if (s.length() > 0) {
                    iffilter=true;
                    for (String history : mHistoryList) {
                        if (history.contains(s)) {
                            mHistoryFilter.add(history);
                        }
                    }
                    mHistoryAdapter.notifyDataSetChanged();
                    mHistoryLayout.setVisibility(mHistoryFilter.isEmpty() ? View.GONE : View.VISIBLE);

                    mData.clear();
                    mAdapter.notifyDataSetChanged();
                } else if (mHistoryList.isEmpty()) {
                    iffilter=false;
                    mHistoryLayout.setVisibility(View.GONE);
                } else {
                    mHistoryLayout.setVisibility(View.VISIBLE);
                    mHistoryEmpty.setVisibility(View.GONE);
                    mHistoryFilter.addAll(mHistoryList);
                    mHistoryAdapter.notifyDataSetChanged();
                }
                mPtrLayout.setLoadMoreEnable(false);
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
                String text = "";
                if (iffilter){
                    text = mHistoryFilter.get(i);
                }else {
                    text = mHistoryList.get(i);
                }


                viewHolder.textView.setText(text);
                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("lgq","aabbscccc");
                        cleckAll(i);
                    }
                });
                viewHolder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("lgq","aabb");
                        String text = viewHolder.textView.getText().toString();

                        mEditSearch.setText(text);
                        mEditSearch.setSelection(text.length());
                        saveHistoryadd();
                        dialog.show();
                        performRefresh();

                    }
                });
            }

            @Override
            public int getItemCount() {
                if (iffilter){
                    return mHistoryFilter.size();
                }else {
                    return mHistoryList.size();
                }
            }


            //  删除 打勾 全选
            public void cleckAll(int  is_checked) { //全选 删除多少那里要删除全部
                for (int a = 0;a<mHistoryList.size();a++){
                    if (is_checked==a){
                        mHistoryList.remove(is_checked);
                        saveHistoryrm();
                        Log.i("lgq","aabbscccc1111");
                    }
                }
                notifyDataSetChanged();
//                Log.i("lgq","aabbscccc2222");
//                Message msg = new Message();
//                msg.what = 1;
//                handler.sendMessage(msg);//用activity中的handler发送消息
            }
        };
        mHistoryRecycle.setAdapter(mHistoryAdapter);
    }

    private void initData() {
        String histories = SPUtils.getInstance().getString("search_history");
        if (TextUtils.isEmpty(histories)) {
            mHistoryEmpty.setVisibility(View.VISIBLE);
        } else {
            mHistoryEmpty.setVisibility(View.GONE);
            List<String> list = Arrays.asList(histories.split(","));
            for (int i = 0; i < list.size(); i++) {
                mHistoryFilter.add(list.get(i));
            }
            mHistoryList.addAll(mHistoryFilter);
            mHistoryAdapter.notifyDataSetChanged();
        }
    }

    private void performRefresh() {
        mCurrpage = 1;
        loadData();
    }

    private void loadData() {
        String keys = mEditSearch.getText().toString();
        if (TextUtils.isEmpty(keys)) {
            return;
        }

        Map<String, String> params = new HashMap<>(3);
        params.put("kw", keys);
        params.put("page", String.valueOf(mCurrpage));

        String url = NetConfig.getUrlByParams(params, NetConfig.API_NewsSearch);
        NetUtil.getRequest(NetConfig.API_NewsSearch, params, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (NetUtil.isSuccess(response)) {
                    JSONArray array;
                    dialog.dismiss();
                    if (response.get("data") instanceof JSONArray) {
                        array = response.getJSONArray("data");
                    } else {
                        array = response.getJSONObject("data").getJSONArray("data");
                    }

                    int totalPage = response.getJSONObject("data").getIntValue("last_page");

                    List<NewsModel> list = JSON.parseArray(array.toJSONString(), NewsModel.class);
                    if (mCurrpage == 1) {
                        mData.clear();
                        mData.addAll(list);
                        mPtrLayout.refreshComplete();
                    } else {  //更多
                        mData.addAll(list);
                        mPtrLayout.loadMoreComplete(list.size() > 0);
                    }
                    mPtrLayout.setLoadMoreEnable(totalPage != mCurrpage);

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
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_search:
                if (mEditSearch.getText().toString().trim().length() < 1) {

                    Toast.makeText(this, "请输入完整的搜索内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveHistoryadd();
                dialog.show();
                loadData();
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

    private void saveHistoryadd() {
        KeyboardUtil.hide(this);
        String keyword = mEditSearch.getText().toString();
        if (mHistoryList.contains(keyword)) {
            mHistoryList.remove(keyword);
        }
        mHistoryList.add(0, keyword);


        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mHistoryList.size(); i++) {
            builder.append(mHistoryList.get(i)).append(",");
        }
        SPUtils.getInstance().put("search_history", builder.substring(0, builder.lastIndexOf(",")));
    }
    private void saveHistoryrm() {
        if (mHistoryList.isEmpty()){
            SPUtils.getInstance().put("search_history", "");
        }else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < mHistoryList.size(); i++) {
                if (!TextUtils.isEmpty(mHistoryList.get(i)))
                    builder.append(mHistoryList.get(i)).append(",");
            }
            SPUtils.getInstance().put("search_history", builder.substring(0, builder.lastIndexOf(",")));
        }
    }


    private class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
            imageView = itemView.findViewById(R.id.deleig);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mShowVueEvent != null) {
            EventBus.getDefault().post(mShowVueEvent);
        }
    }
}
