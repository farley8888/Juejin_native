package com.juejinchain.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juejinchain.android.R;
import com.juejinchain.android.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;


/**
 * 视频详情  Adapter
 * 多组item
 */
public class VideoDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//    private List<String> mItems = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
    private OnItemClickListener mClickListener;

    public VideoDetailAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setDatas(List<String> items) {
//        mItems.clear();
//        mItems.addAll(items);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType > 0){
            View view = mInflater.inflate(R.layout.item_video_detail, parent, false);

            final MyViewHolder holder = new MyViewHolder(view);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (mClickListener != null) {
                        mClickListener.onItemClick(position, v);
                    }
                }
            });
            return holder;
        }else {
            View view = mInflater.inflate(R.layout.head_video_detail, parent, false);

            final MyViewHolder holder = new MyViewHolder(view);

            return holder;
        }
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        return position > 0 ? 1:0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        String item = mItems.get(position);
//        holder.tvTitle.setText(item);



    }

    @Override
    public int getItemCount() {
//        return mItems.size();
        return 10;
    }

    //推荐视频item
    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    //头部view
    class HeadViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;

        public HeadViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_vdoTitle);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
