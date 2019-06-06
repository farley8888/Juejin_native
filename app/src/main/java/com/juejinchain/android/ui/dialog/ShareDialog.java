package com.juejinchain.android.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.dmcbig.mediapicker.utils.ScreenUtils;
import com.juejinchain.android.MainActivity;
import com.juejinchain.android.R;
import com.juejinchain.android.event.ShareEvent;
import com.juejinchain.android.event.ShowVueEvent;
import com.juejinchain.android.model.ShareModel;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.NetUtil;
import com.juejinchain.android.tools.L;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分享对话框
 */
public class ShareDialog extends Dialog {
    Activity mActivity ;

    public ShareDialog(Context context) {
        this(context, 0);
        mActivity = (Activity) context;
    }

    private View.OnClickListener clickListener;

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public ShareDialog(Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_share);

        Window window = getWindow();
        WindowManager.LayoutParams params =  window.getAttributes();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        params.width = ScreenUtils.getScreenWidth(context) - ScreenUtils.dp2px(context, 20);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.windowAnimations = R.style.bottomSlideAnim;

        getWindow().setAttributes(params);

        initView();
    }

    private void initView(){
        RecyclerView recyclerView = findViewById(R.id.recycleView);

        List<ShareModel> list = buildData();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new RecyclerView.Adapter<ShareHolder>() {
            @NonNull
            @Override
            public ShareHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new ShareHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_share, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull ShareHolder viewHolder, int i) {
                ShareModel ShareModel = list.get(i);
                viewHolder.bindView(ShareModel);
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
        ImageView img = findViewById(R.id.img_share_seeButton);
        Glide.with(getContext()).load(R.drawable.share_see_button).into(img);
        //查看攻略
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                EventBus.getDefault().post(new ShowVueEvent(ShowVueEvent.PAGE_LOCK_FAN, ""));
                if (clickListener != null) clickListener.onClick(view);
            }
        });
        findViewById(R.id.cancel).setOnClickListener(v -> dismiss());
    }

    private List<ShareModel> buildData(){
        List<ShareModel> list = new ArrayList<>();
        String tag = ShareModel.TAG_INDEX;
        ShareModel suofen = new ShareModel("锁粉神器", ShareModel.WAY_SUOFEN,R.drawable.share_icon_suofen, tag);
        suofen.withDot = true;
        list.add(suofen);
        list.add(new ShareModel("微博",ShareModel.WAY_WEIBO ,R.drawable.share_icon_weibo, tag));
        list.add(new ShareModel("QQ好友",ShareModel.WAY_QQ, R.drawable.share_icon_qq, tag));
        list.add(new ShareModel("微信好友",ShareModel.WAY_WECHAT, R.drawable.share_icon_wechat, tag));
        list.add(new ShareModel("朋友圈",ShareModel.WAY_FRIEND, R.drawable.share_icon_friend, tag));
        return list;
    }

    private class ShareHolder extends RecyclerView.ViewHolder{
        private TextView recommend;
        private ImageView logo;
        private TextView name;

        public ShareHolder(@NonNull View itemView) {
            super(itemView);
            recommend = itemView.findViewById(R.id.recommend);
            logo = itemView.findViewById(R.id.logo);
            name = itemView.findViewById(R.id.name);
        }

        public void bindView(ShareModel ShareModel){
            name.setText(ShareModel.name);
            recommend.setVisibility(ShareModel.withDot ? View.VISIBLE : View.GONE);
            if(ShareModel.logo > 0){
                Glide.with(getContext())
                        .load(ShareModel.logo)
                        .into(logo);
            }

            itemView.setOnClickListener(v -> {
                shareTo (ShareModel);
                if (clickListener != null) clickListener.onClick(v);
//                Toast.makeText(getContext(), "点击了" + ShareModel.name, Toast.LENGTH_SHORT).show();
            });
        }
    }

    void shareTo(ShareModel entitiy){
//        L.d("baseDailog", "shareTo: "+entitiy.way);
//        if (entitiy.way.equals(ShareModel.WAY_SUOFEN))
        dismiss();

        EventBus.getDefault().post(new ShareEvent(entitiy));
    }

//    private class ShareModel{
//
//        /**
//         * 名称
//         */
//        public String name;
//        public String way;
//        /**
//         * logo
//         */
//        public int logo;
//        /**
//         * 是否含有推荐
//         */
//        public boolean withDot;
//
//        public ShareModel(String name, int logo, boolean withDot) {
//            this.name = name;
//            this.logo = logo;
//            this.withDot = withDot;
//        }
//
//
//        public ShareModel(String name, String way, int logo, boolean withDot) {
//            this(name, logo, withDot);
//            this.way = way;
//        }
//    }
}
