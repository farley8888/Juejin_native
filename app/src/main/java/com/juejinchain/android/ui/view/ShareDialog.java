package com.juejinchain.android.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dmcbig.mediapicker.utils.ScreenUtils;
import com.juejinchain.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 分享对话框
 */
public class ShareDialog extends Dialog {
    public ShareDialog(Context context) {
        this(context, 0);
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

        List<ShareEntitiy> list = buildData();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new RecyclerView.Adapter<ShareHolder>() {
            @NonNull
            @Override
            public ShareHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new ShareHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_share, viewGroup, false));
            }

            @Override
            public void onBindViewHolder(@NonNull ShareHolder viewHolder, int i) {
                ShareEntitiy shareEntitiy = list.get(i);
                viewHolder.bindView(shareEntitiy);
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });

        findViewById(R.id.cancel).setOnClickListener(v -> dismiss());
    }

    private List<ShareEntitiy> buildData(){
        List<ShareEntitiy> list = new ArrayList<>();
        list.add(new ShareEntitiy("锁粉神器", 0, true));
        list.add(new ShareEntitiy("微博", 0, false));
        list.add(new ShareEntitiy("QQ好友", 0, false));
        list.add(new ShareEntitiy("微信好友", 0, false));
        list.add(new ShareEntitiy("朋友圈", 0, false));
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

        public void bindView(ShareEntitiy shareEntitiy){
            name.setText(shareEntitiy.name);
            recommend.setVisibility(shareEntitiy.withDot ? View.VISIBLE : View.GONE);
            if(shareEntitiy.logo > 0){
                Glide.with(getContext())
                        .load(shareEntitiy.logo)
                        .into(logo);
            }

            itemView.setOnClickListener(v -> {
                Toast.makeText(getContext(), "点击了" + shareEntitiy.name, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private class ShareEntitiy{
        /**
         * 名称
         */
        public String name;
        /**
         * logo
         */
        public int logo;
        /**
         * 是否含有推荐
         */
        public boolean withDot;

        public ShareEntitiy(String name, int logo, boolean withDot) {
            this.name = name;
            this.logo = logo;
            this.withDot = withDot;
        }
    }
}
