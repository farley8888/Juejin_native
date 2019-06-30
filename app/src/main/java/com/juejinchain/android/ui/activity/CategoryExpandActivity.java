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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.juejinchain.android.R;
import com.juejinchain.android.event.UpdateChannelEvent;
import com.juejinchain.android.model.ChannelModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.ui.view.DragHelper.ChannelAdapter;
import com.juejinchain.android.ui.view.DragHelper.ItemDragHelperCallback;
import com.juejinchain.android.util.SPUtils;
import com.juejinchain.android.util.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


import io.dcloud.common.util.JSONUtil;

import static com.juejinchain.android.util.Constant.CHANNEL_CHCHE;
//BaseBackFragment
public class CategoryExpandActivity extends AppCompatActivity implements View.OnClickListener {

//    private String url = "http://api.juejinchain.com/v1/channel/recommend?user_token=6f735e3e7b871f71785b283f6a8a719b&source_style=6";

    public static final int Default_Min = 4;
    public static final int Default_Max = 16;
    /**
     * 默认频道，如果删除少于7个使用
     * 推荐、热门、视频、掘金宝、社会、娱乐、体育。就算把这7个频道删除了，在进入频道就默认展示这7个频道
     */
    private List<ChannelModel> defaultList = new ArrayList<>();
    private String defaultChannelNames = "推荐,热门,视频,掘金宝,社会,娱乐,体育";

    private RecyclerView mRecy;
    private ImageView mBackIv;

    //首页频道原列表，用于判断编辑后是否修改
    private List<ChannelModel> originList = new ArrayList<>();

    //我的频道组合
    private List<ChannelModel> showList = new ArrayList<>();

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

    void setDefalutList(){
        for (ChannelModel model:originList){
            if (defaultChannelNames.contains(model.getName())){
//                Log.d("categorybase", "loadChannel: "+model.getName());
                defaultList.add(model);
            }
        }
    }

    private void setData(List<ChannelModel> otherChannels) {
        String channelCacheData = SPUtils.getInstance().getString(CHANNEL_CHCHE);

        if (JSON.parse(channelCacheData) instanceof JSONArray){
            showList = JSON.parseArray(channelCacheData, ChannelModel.class);
        }else{
            finish(); //没有缓存的直接退出
            return;
        }

        if(showList != null && !showList.isEmpty()){
            originList.addAll(showList);

            List<ChannelModel> duplications = new ArrayList<>();
            for (ChannelModel channelModel : otherChannels){
                if(showList.contains(channelModel)){
                    duplications.add(channelModel);
                }
            }
            otherChannels.removeAll(duplications);
        }
        setDefalutList();
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mRecy.setLayoutManager(manager);

        ItemDragHelperCallback callback = new ItemDragHelperCallback();
        final ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecy);

        //推荐的频道是 otherChannels
        final ChannelAdapter adapter = new ChannelAdapter(this, helper, showList, otherChannels);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = adapter.getItemViewType(position);
                return viewType == ChannelAdapter.TYPE_MY || viewType ==  ChannelAdapter.TYPE_MY_CHANNEL_FIRST
                        || viewType == ChannelAdapter.TYPE_OTHER ? 1 : 4;
            }
        });
        mRecy.setAdapter(adapter);

        adapter.setEditingListener(list -> {
//            Log.i("yang", "拖动了view: " + list.toString());
            SPUtils.getInstance().put(CHANNEL_CHCHE, JSON.toJSONString(list));
            showList = list;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        boolean notifyUpdate = showList.size() != originList.size();
        if(!notifyUpdate){
            for (int i = 0 ; i < showList.size(); i++){
                if(!showList.get(i).equals(originList.get(i))){
                    notifyUpdate = true;
                    break;
                }
            }
        }

        if(notifyUpdate){
            if (showList.size() < defaultList.size()){
                //如果小时默认个数则使用默认的
                SPUtils.getInstance().put(CHANNEL_CHCHE, JSON.toJSONString(defaultList));
            }
            EventBus.getDefault().post(new UpdateChannelEvent());
        }
    }
}
