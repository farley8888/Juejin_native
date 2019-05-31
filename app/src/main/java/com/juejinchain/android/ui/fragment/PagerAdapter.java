package com.juejinchain.android.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.juejinchain.android.R;
import com.juejinchain.android.model.NewsModel;
import com.juejinchain.android.tools.GlideRoundTransform;

import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by YoKeyword on 16/6/30.
 * RecyclerAdapterWithHF
 * RecyclerView.Adapter<PagerAdapter.MyViewHolder>
 *     RecyclerView.Adapter<RecyclerView.ViewHolder>
 */
public class PagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<NewsModel> mItems = new ArrayList<>();
    private LayoutInflater mInflater;
//    GlideRoundTransform glideRoundTransform ;

    private OnItemClickListener mClickListener;

    public PagerAdapter(Context context, List<NewsModel> data) {
        super();
        mInflater = LayoutInflater.from(context);
        mItems = data;
        //加圆角 centerCrop() 这个属性会影响
//        glideRoundTransform = new GlideRoundTransform(mInflater.getContext(), 5);
    }

    public PagerAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    //非根adapter不要用此方法
    public void setDatas(List<String> items) {
        mItems.clear();
//        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_pager, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (mClickListener != null) {
                    mClickListener.onItemClick(position, v, holder);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        NewsModel model = mItems.get(position);
        MyViewHolder holder = (MyViewHolder) viewHolder;
        holder.lyImages.setVisibility(View.GONE);
        holder.img0.setVisibility(View.GONE);
        if (model.type == 2){       //3图
            holder.lyImages.setVisibility(View.VISIBLE);
            Glide.with(mInflater.getContext())
                    .load(model.img_url[0%model.img_url.length])
                    .into(holder.img1);

             Glide.with(mInflater.getContext())
                    .load(model.img_url[1%model.img_url.length])
                     .into(holder.img2);

             Glide.with(mInflater.getContext())
                     .load(model.img_url[2%model.img_url.length])
                     .placeholder(R.drawable.default_img)
                     .into(holder.img3);

        }else if (model.type == 1){ //小图
            holder.img0.setVisibility(View.VISIBLE);
            Glide.with(mInflater.getContext())
                    .load(model.img_url[0])
                    .into(holder.img0);
        }else if (model.type == 4){ //无图

        }

        holder.tvTitle.setText(model.title);
        holder.tvHot.setText(model.getIs_top());
        holder.tvSource.setText(model.source);
        holder.tvReadTimes.setText(model.getRead_num());
        holder.tvDate.setText(model.getPublish_time());
    }

//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        String item = mItems.get(position);
//        holder.tvTitle.setText(item);
//    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private ViewGroup lyImages;
        private ImageView img0;
        private ImageView img1;
        private ImageView img2;
        private ImageView img3;

        private TextView tvHot;
        private TextView tvSource;
        private TextView tvReadTimes;
        private TextView tvDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            lyImages = itemView.findViewById(R.id.ly_middle);
            tvTitle = itemView.findViewById(R.id.tv_title);

            img0 = itemView.findViewById(R.id.imageView0);
            img1 = itemView.findViewById(R.id.imageView1);
            img2 = itemView.findViewById(R.id.imageView2);
            img3 = itemView.findViewById(R.id.imageView3);

            tvHot = itemView.findViewById(R.id.tv_hot);
            tvSource = itemView.findViewById(R.id.tv_from);
            tvReadTimes = itemView.findViewById(R.id.tv_readTimes);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
