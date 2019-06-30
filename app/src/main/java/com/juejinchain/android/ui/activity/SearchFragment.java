package com.juejinchain.android.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.juejinchain.android.base.BaseBackFragment;
import com.juejinchain.android.event.ShowVueEvent;
import com.juejinchain.android.model.NewsModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.adapter.HomePagerAdapter;
import com.juejinchain.android.ui.fragment.MainFragment;
import com.juejinchain.android.ui.ppw.SearchingPopup;
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
public class SearchFragment extends BaseBackFragment implements View.OnClickListener {
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
    private List<Object> mData = new ArrayList<>();

    /**
     * adapter
     */
    private RecyclerAdapterWithHF mAdapter;

    private List<String> mHistoryList = new ArrayList<>();
    private List<String> mHistoryFilter = new ArrayList<>();

    private RecyclerView.Adapter mHistoryAdapter;
//    private ShowVueEvent mShowVueEvent;

    //分割线
    private RecyclerView.ItemDecoration itemDecoration;

    private boolean iffilter=false;
    private SearchingPopup mLoading;
    private HomePagerAdapter adapter;
    ImageView mCleanButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_search, container, false);
        initView(view);
        initEvent(view);
        initData();
        return view;
    }


    private void initView(View v) {
        mPtrLayout = v.findViewById(R.id.ptr_layout);
        mRecycleView = v.findViewById(R.id.recy);
        mEmptyLoadView = v.findViewById(R.id.ly_no_searchdata);
        mEditSearch = v.findViewById(R.id.edt_search);

        mHistoryLayout = v.findViewById(R.id.history_layout);
        mHistoryEmpty = v.findViewById(R.id.search_empty);
        mHistoryRecycle = v.findViewById(R.id.history_view);

        itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//默认分割线
        mHistoryRecycle.addItemDecoration(itemDecoration);
        mCleanButton = v.findViewById(R.id.btn_clearKw);
        mCleanButton.setVisibility(View.INVISIBLE);
    }

    private void initEvent(View v) {
        v.findViewById(R.id.btn_back).setOnClickListener(this);
        v.findViewById(R.id.btn_search).setOnClickListener(this);
        v.findViewById(R.id.btn_clear).setOnClickListener(this);
        mCleanButton.setOnClickListener(this);

        //不用下拉刷新
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
        adapter = new HomePagerAdapter(getContext(), mData);
        mAdapter = new RecyclerAdapterWithHF(adapter);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleView.setAdapter(mAdapter);
        setEditListener();

        mAdapter.setOnItemClickListener(new RecyclerAdapterWithHF.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerAdapterWithHF adapter, RecyclerView.ViewHolder vh, int position) {

                MainFragment mainFragment = (MainFragment) getPreFragment();
                mainFragment.nextFragment = SearchFragment.this;
                showHideFragment(mainFragment, SearchFragment.this);

                NewsModel model = (NewsModel) mData.get(position);
                Log.d("search", "onItemClick: "+model.getTitle());
                mainFragment.showVue(ShowVueEvent.PAGE_ART_DETAIL, model.id);

//                mShowVueEvent = new ShowVueEvent(ShowVueEvent.PAGE_ART_DETAIL, model.id);
//                EventBus.getDefault().post(mShowVueEvent);
//                finish();
            }
        });

        mHistoryRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        mHistoryAdapter = new RecyclerView.Adapter<HistoryViewHolder>() {
            @NonNull
            @Override
            public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new HistoryViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_search_history, viewGroup, false));
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
                viewHolder.deletBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Log.i("lgq","aabbscccc");
                        cleckAll(i);
                    }
                });
                viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Log.i("lgq","aabb");
                        String text = viewHolder.textView.getText().toString();

                        mEditSearch.setText(text);
                        mEditSearch.setSelection(text.length());
                        saveHistoryadd();

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
                    }
                }
                notifyDataSetChanged();
            }
        };
        mHistoryRecycle.setAdapter(mHistoryAdapter);
    }

    private void setEditListener() {
        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mCleanButton.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mHistoryFilter.clear();
                mCleanButton.setVisibility(s.length() > 0 ? View.VISIBLE: View.GONE);
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

        //监听搜索按钮
        mEditSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE) {
                    String content = textView.getText().toString().trim();
                    if (content.length() < 2){
                        Toast.makeText(getContext(), "请输入完整", Toast.LENGTH_SHORT).show();
                        return true;
                    }else{
                        saveHistoryadd();
                        loadData();
                    }
                    handled = true;
                    /*隐藏软键盘*/
                    hideSoftInput();
                }
                return handled;
//                return false;
            }
        });
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
        if (mCurrpage == 1){
            if (mLoading == null)mLoading = new SearchingPopup(getContext());
            mLoading.showPopupWindow();
        }

        Map<String, String> params = new HashMap<>(3);
        params.put("kw", keys);
        params.put("page", String.valueOf(mCurrpage));
        adapter.setSearchKey(keys);

        NetUtil.getRequest(NetConfig.API_NewsSearch, params, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                if (mLoading != null) mLoading.dismiss();
                mPtrLayout.refreshComplete();
                if (NetUtil.isSuccess(response)) {
                    JSONArray array;
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

                    } else {  //更多
                        mData.addAll(list);
                    }
                    mPtrLayout.setLoadMoreEnable(totalPage > mCurrpage);
                    if (totalPage == mCurrpage && mData.size() > 0)
                        mPtrLayout.loadMoreComplete(true);

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
                onBackPressedSupport();
                pop();
                break;
            case R.id.btn_search:
                if (mEditSearch.getText().toString().trim().length() < 1) {

                    Toast.makeText(getContext(), "请输入完整的搜索内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveHistoryadd();
                loadData();
                break;
            case R.id.btn_clearKw:
                mEditSearch.setText("");
                break;
            case R.id.btn_clear:
                new AlertDialog.Builder(getContext())
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
//        KeyboardUtil.hide(getActivity());
        hideSoftInput();
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


    public boolean onBackPressedSupport() {
        hideSoftInput();
        MainFragment mainFragment = (MainFragment) getPreFragment();
        showHideFragment(mainFragment);
        mainFragment.showHomeFragment();
        return super.onBackPressedSupport();
    }


    private class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView deletBtn;
        private LinearLayout linearLayout;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
            deletBtn = itemView.findViewById(R.id.deleig);
            deletBtn.setVisibility(View.GONE);
            linearLayout = itemView.findViewById(R.id.sosli);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainFragment mainFragment = (MainFragment) getPreFragment();
        mainFragment.nextFragment = null;
    }
}
