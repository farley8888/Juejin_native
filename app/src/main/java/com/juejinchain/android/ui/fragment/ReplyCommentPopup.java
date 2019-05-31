package com.juejinchain.android.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.juejinchain.android.R;
import com.juejinchain.android.adapter.ReplyCommentXAdapter;
import com.juejinchain.android.base.XRecyclerViewAdapter;
import com.juejinchain.android.model.CommentModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.ui.view.AlertProDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import razerdp.basepopup.BasePopupWindow;

/**
 * 回复评论
 */
public class ReplyCommentPopup extends BasePopupWindow {

    private String TAG ="ReplyCommentPopup";
    private RecyclerView mRecyclerVeiw;
    private EditText editText;
    private CommentModel mModel;
    private ReplyCommentXAdapter mAdapter;

    List<CommentModel> mData = new ArrayList<>();
    int currPage = 1;
    int pageSize = 10;
    int total;

    public ReplyCommentPopup(Context context, boolean delayInit) {
        super(context, delayInit);
        setPopupGravity(Gravity.BOTTOM);
//        bindEvent();
    }

    public ReplyCommentPopup(Context context, CommentModel model) {
        super(context);
        setPopupGravity(Gravity.BOTTOM);
        this.mModel = model;
        Log.d(TAG, "ReplyCommentPopup: "+mModel.cid);
        bindEvent();

    }
//    public ReplyCommentPopup(Context context, int width, int height, boolean delayInit) {
//        super(context, width, height, delayInit);
//    }

    private void bindEvent() {
        initView(null);
        mAdapter = new ReplyCommentXAdapter(mRecyclerVeiw);
        mRecyclerVeiw.setAdapter(mAdapter);
        setListener();

    }

    @Override
    protected View onFindDecorView(Activity activity) {

        setHeadView();
        return super.onFindDecorView(activity);
    }

    @Override
    public View onCreateContentView() {
        View v = createPopupById(R.layout.popup_reply_comment);
//        initView(v);
////        mAdapter = new ReplyCommentXAdapter(mRecyclerVeiw);
////        mRecyclerVeiw.setAdapter(mAdapter);
////        setListener(); 不能在这设置
        return v;
    }

    void setHeadView(){
        ImageView img = findViewById(R.id.img_cmtHead);
        Glide.with(getContext()).load(mModel.avatar).into(img);

        TextView tv = findViewById(R.id.tv_cmtName);
        tv.setText(mModel.nickname);

        tv = findViewById(R.id.tv_cmtContent);
        tv.setText(mModel.content);

        tv = findViewById(R.id.tv_cmtDate);
        tv.setText(mModel.getComment_time());

        tv = findViewById(R.id.btn_cmtReply);
        tv.setText(mModel.reply+"条回复");

        tv = findViewById(R.id.btn_cmtZan);
        tv.setText(mModel.fabulous+"");
        tv.setEnabled(!mModel.is_fabulous);

    }

    void loadList(){
        Map<String,String> param = new HashMap<>();
        param.put("cid", mModel.cid);
        param.put("page", currPage+"");
        param.put("pre_page", pageSize+"");
        if (currPage == 1){
            AlertProDialog.showLoading(true);
        }
        NetUtil.getRequest(NetConfig.API_VideoReplyList, param, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: "+response);
                response = response.getJSONObject("data");
                total = response.getInteger("total");
                List<CommentModel> temp ;
                if (response.containsKey("top")){
                    temp = JSON.parseArray(response.getString("top"), CommentModel.class);
                }else {
                    temp = JSON.parseArray(response.getString("data"), CommentModel.class);
                }
                mData.addAll(temp);
                Log.d(TAG, "onResponse: size="+mData.size());
                mRecyclerVeiw.setAdapter(mAdapter);
                mAdapter.setDataList(mData);  //无法自动刷新列表？
//                mAdapter.notifyDataSetChanged();
                if (mData.size() < total){
                    mAdapter.isLoadMore(true);
                }else{
                    mAdapter.showLoadComplete();
                }
            }
        }, false);

    }

    void initView(View v){
        mRecyclerVeiw = findViewById(R.id.recycler_view);
        editText = findViewById(R.id.edt_replyComment);
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadList();
            }
        }, 1000);
    }

    void setListener(){
        mAdapter.setOnLoadMoreListener(new XRecyclerViewAdapter.OnLoadMoreListener() {
            @Override
            public void onRetry() {

            }

            @Override
            public void onLoadMore() {
                currPage++;
                loadList();
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()){
                    case R.id.btn_publish: //发布评论
                        String content = editText.getText().toString().trim();
                        if (content.length() < 2){
                            Toast.makeText(getContext(), "请输入完整的评论",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            publishComment(content);
                        }
                        break;
                    case R.id.btn_cmtZan: //点赞

                        break;
                    case R.id.btn_close:
                        dismiss();
                        break;
                }
            }
        };
        findViewById(R.id.btn_close).setOnClickListener(listener);
        findViewById(R.id.btn_publish).setOnClickListener(listener);
        findViewById(R.id.btn_cmtZan).setOnClickListener(listener);
    }

    void publishComment(String content){
        Map<String,String> param = new HashMap<>();
        param.put("cid", mModel.cid);
        param.put("vid", mModel.vid);
        param.put("content", content);
        NetUtil.getRequestShowLoading(NetConfig.API_VideoReply, param, new NetUtil.OnResponse() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.d("replay", "onResponse: "+response);
                Toast.makeText(getContext(),"回复成功", Toast.LENGTH_SHORT).show();
                editText.setText("");
                currPage = 1;
                mData.clear();
                loadList();
            }
        }, false);
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return getTranslateVerticalAnimation(1f, 0f, 300);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return getTranslateVerticalAnimation(0f, 1f, 300);
    }


}
