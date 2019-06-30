package com.juejinchain.android.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.juejinchain.android.R;
import com.juejinchain.android.base.XRecyclerViewAdapter;
import com.juejinchain.android.base.XViewHolder;
import com.juejinchain.android.model.CommentModel;
import com.juejinchain.android.model.VideoModel;

import org.w3c.dom.Text;

/**
 * 视频详情  Adapter
 * 多组item
 */
public class VideoDetailXAdapter extends XRecyclerViewAdapter<Object> {


    private com.juejinchain.android.ui.fragment.OnItemClickListener mClickListener;

    public VideoDetailXAdapter(@NonNull RecyclerView mRecyclerView) {
        super(mRecyclerView);
    }

    public VideoDetailXAdapter(@NonNull RecyclerView mRecyclerView, int layoutId) {
        super(mRecyclerView, layoutId);
    }

    @Override
    public XViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    public void setMyItemClickListener(com.juejinchain.android.ui.fragment.OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    View.OnClickListener listener;
    @Override
    protected void bindData(XViewHolder holder, Object data, int position) {
        View v = holder.itemView;

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null){
                    mClickListener.onItemClick(position, view, holder);
                }
            }
        };

        if (position == 0){
            VideoModel model = (VideoModel) data;
            findTv(v, R.id.tv_vdoTitle).setText(model.title);
            findTv(v, R.id.tv_from).setText(model.author);
            findTv(v, R.id.button1).setText(model.comment_count+"");
            findTv(v, R.id.tv_vdoPlayTimes).setText(model.getPlayTimesStr());

            TextView zan = findTv(v, R.id.buttonZan);
            zan.setText(model.video_like_count+"");
            zan.setEnabled(!model.is_fabulous);

            ImageView img_author = v.findViewById(R.id.img_author);
            Glide.with(getContext()).load(model.author_logo).into(img_author);

            zan.setOnClickListener(listener);
            findTv(v, R.id.button1).setOnClickListener(listener);
            findTv(v, R.id.buttonShare).setOnClickListener(listener);

        }else if (data instanceof VideoModel){
            VideoModel model = (VideoModel) data;
            findTv(v, R.id.tv_title).setText(model.title);
            findTv(v, R.id.tv_source).setText(model.author);
            findTv(v, R.id.tv_playTimes).setText(model.getPlayTimesStr());
            findTv(v, R.id.tv_duration).setText(model.getVideo_duration());

            Glide.with(getContext()).load(model.img_url).into(findImg(v, R.id.img_video));
        }else {
            CommentModel cmodel = (CommentModel) data;
            Glide.with(getContext()).load(cmodel.avatar).into(findImg(v, R.id.img_cmtHead));
            TextView zanBtn = findTv(v, R.id.btn_cmtZan);
            zanBtn.setText(cmodel.fabulous+"");
            zanBtn.setEnabled(!cmodel.is_fabulous);
            zanBtn.setOnClickListener(listener);

            findTv(v, R.id.tv_cmtContent).setText(cmodel.content);
            findTv(v, R.id.tv_cmtName).setText(cmodel.nickname);
            findTv(v, R.id.tv_cmtDate).setText(cmodel.getComment_time());

            findTv(v, R.id.btn_cmtReply).setText(" · "+cmodel.reply+"条回复>");
            findTv(v, R.id.btn_cmtReply).setOnClickListener(listener);
        }

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemLayoutResId(Object data, int position) {
//        if (data.getType() == VideoModel.typeHead){
        if (position == 0){
            return R.layout.head_video_detail;
        }else if (data instanceof VideoModel){
            return R.layout.item_video_detail;
        }
        else{
            return R.layout.item_comment;
        }
//        return super.getItemLayoutResId(data, position);

    }
}
