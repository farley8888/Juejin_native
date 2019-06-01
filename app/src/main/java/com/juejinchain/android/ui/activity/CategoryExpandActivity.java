package com.juejinchain.android.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.juejinchain.android.R;
import com.juejinchain.android.model.ChannelModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.ui.view.DragHelper.ChannelAdapter;
import com.juejinchain.android.ui.view.DragHelper.ItemDragHelperCallback;
import com.juejinchain.android.util.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import static com.juejinchain.android.util.Constant.CHANNEL_CHCHE;
//BaseBackFragment
public class CategoryExpandActivity extends AppCompatActivity implements View.OnClickListener {

//    private String url = "http://api.juejinchain.com/v1/channel/recommend?user_token=6f735e3e7b871f71785b283f6a8a719b&source_style=6";

    private RecyclerView mRecy;
    private ImageView mBackIv;
    private List<ChannelModel> myChannels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_expand);
        mRecy = findViewById(R.id.recy);
        mBackIv = findViewById(R.id.back_iv);
        mBackIv.setOnClickListener(this);
        findViewById(R.id.toolbar_right_button).setVisibility(View.GONE);
        loadChannel();
    }

    private void loadChannel(){

        NetUtil.getRequestShowLoading(NetConfig.API_ChannelRecommend, null, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                List<ChannelModel> channelList = JSON.parseArray(response.getString("data"), ChannelModel.class);
                setData(channelList);
            }
        }, false);

    }

    private void setData(List<ChannelModel> otherChannels) {
        String channelCacheData = SPUtils.getInstance().getString(CHANNEL_CHCHE);
        List<ChannelModel> temp = JSON.parseArray(channelCacheData, ChannelModel.class);
        myChannels.addAll(temp);

        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mRecy.setLayoutManager(manager);

        ItemDragHelperCallback callback = new ItemDragHelperCallback();
        final ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecy);

        final ChannelAdapter adapter = new ChannelAdapter(this, helper, temp, otherChannels);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = adapter.getItemViewType(position);
                return viewType == ChannelAdapter.TYPE_MY || viewType ==  ChannelAdapter.TYPE_MY_CHANNEL_FIRST
                        || viewType == ChannelAdapter.TYPE_OTHER ? 1 : 4;
            }
        });
        mRecy.setAdapter(adapter);

        adapter.setEditingListener(new ChannelAdapter.EndEditedListener() {
            @Override
            public void finishEdited(List<ChannelModel> lists) {
                //换顺序了也一样
//                if (lists.containsAll(myChannels) && myChannels.containsAll(lists)){
//                    Log.d("baseCategory", "finishEdited: 没变");
//                else
                SPUtils.getInstance().put(CHANNEL_CHCHE, JSON.toJSONString(lists));
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
        }
    }
}
