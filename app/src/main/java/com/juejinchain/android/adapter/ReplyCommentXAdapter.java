package com.juejinchain.android.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.juejinchain.android.R;
import com.juejinchain.android.base.XRecyclerViewAdapter;
import com.juejinchain.android.base.XViewHolder;
import com.juejinchain.android.listener.OnItemClickListener;
import com.juejinchain.android.model.CommentModel;

/**
 * 回复评论列表
 */
public class ReplyCommentXAdapter extends XRecyclerViewAdapter<CommentModel> {

    private com.juejinchain.android.listener.OnItemClickListener clickListener;

    public ReplyCommentXAdapter(@NonNull RecyclerView mRecyclerView) {
        super(mRecyclerView);
    }

    @Override
    protected void bindData(XViewHolder holder, CommentModel data, int position) {
        View v = holder.itemView;
        Glide.with(getContext()).load(data.avatar).into(findImg(v, R.id.img_cmtHead));
        TextView zanBtn = findTv(v, R.id.btn_cmtZan);
        zanBtn.setEnabled(!data.is_fabulous);
        zanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null){
                    clickListener.onItemClick(position, view);
                }
            }
        });

        Log.d("basetag", "bindData: ");
        zanBtn.setText(data.fabulous+"");
        findTv(v, R.id.tv_cmtContent).setText(data.content);
        findTv(v, R.id.tv_cmtName).setText(data.nickname);
        findTv(v, R.id.tv_cmtDate).setText(data.getComment_time());
        findTv(v, R.id.btn_cmtReply).setVisibility(View.GONE);
    }

    public void setClickListener(com.juejinchain.android.listener.OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemLayoutResId(CommentModel data, int position) {
        Log.d("basetag", "getItemLayoutResId: ");
        return R.layout.item_reply_comment;

    }
}
