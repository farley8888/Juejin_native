package com.juejinchain.android.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.juejinchain.android.R;
import com.juejinchain.android.model.ChannelModel;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.network.OkHttpUtils;
import com.juejinchain.android.network.SpUtils;
import com.juejinchain.android.network.callBack.JSONCallback;
import com.juejinchain.android.ui.view.DragHelper.ChannelAdapter;
import com.juejinchain.android.ui.view.DragHelper.ItemDragHelperCallback;
import com.juejinchain.android.util.SPUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import static com.juejinchain.android.util.Constant.CHANNEL_CHCHE;

public class CategoryExpandActivity extends AppCompatActivity implements View.OnClickListener {

    private String url = "http://api.juejinchain.com/v1/channel/recommend?user_token=6f735e3e7b871f71785b283f6a8a719b&source_style=6";

    private RecyclerView mRecy;
    private ImageView mBackIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_expand);
        mRecy = findViewById(R.id.recy);
        mBackIv = findViewById(R.id.back_iv);
        mBackIv.setOnClickListener(this);
        loadChannel();
    }

    private void loadChannel(){
        OkHttpUtils.getAsyn(url, new JSONCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(JSONObject response) {
                if (NetUtil.isSuccess(response)){
                    List<ChannelModel> channelList = JSON.parseArray(response.getString("data"), ChannelModel.class);
                    setData(channelList);
                }
            }
        });
    }

    private void setData(List<ChannelModel> otherChannels) {
        String channelCacheData = SPUtils.getInstance().getString(CHANNEL_CHCHE);
        final List<ChannelModel> items = JSON.parseArray(channelCacheData, ChannelModel.class);

        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mRecy.setLayoutManager(manager);

        ItemDragHelperCallback callback = new ItemDragHelperCallback();
        final ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecy);

        final ChannelAdapter adapter = new ChannelAdapter(this, helper, items, otherChannels);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = adapter.getItemViewType(position);
                return viewType == ChannelAdapter.TYPE_MY ||viewType ==  ChannelAdapter.TYPE_MY_CHANNEL_FIRST || viewType == ChannelAdapter.TYPE_OTHER ? 1 : 4;
            }
        });
        mRecy.setAdapter(adapter);
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
