package com.juejinchain.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
//import com.tencent.smtt.sdk.WebView;
//import com.tencent.smtt.sdk.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.juejinchain.android.R;
import com.juejinchain.android.model.VideoModel;
import com.juejinchain.android.tools.L;
import com.juejinchain.android.tools.WebViewUtil;
import com.juejinchain.android.ui.fragment.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;


/**
 * Created by YoKeyword on 16/6/30.
 * RecyclerAdapterWithHF
 * RecyclerView.Adapter<PagerAdapter.MyViewHolder>
 *     RecyclerView.Adapter<RecyclerView.ViewHolder>
 */
public class VideoPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<VideoModel> mItems = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;
//    GlideRoundTransform glideRoundTransform ;
    private String TAG = "VideoPagerAdapter";
    private OnItemClickListener mClickListener;

     WebView parseWebView;
    public JzvdStd jzvdPlayer;
    public VideoModel playerModel;

    public VideoPagerAdapter(Context context, List<VideoModel> data) {
        super();
        this.context = context;
        mInflater = LayoutInflater.from(context);
        mItems = data;
        //加圆角 centerCrop() 这个属性会影响
//        glideRoundTransform = new GlideRoundTransform(mInflater.getContext(), 5);
    }

    public VideoPagerAdapter(Context context) {
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
        View view = mInflater.inflate(R.layout.item_pager_video, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
//                Log.d("VideoPageAdapter", "onClick: "+position);
                if (mClickListener != null) {
                    mClickListener.onItemClick(position, v, holder);
                    if(v == holder.imgVideo){
                        parseWebView = holder.webView;
                        jzvdPlayer = holder.jzvdStdPlayer;
                        setPlayHtml(mItems.get(position), parseWebView);

                    }
                }
            }
        };
        holder.tvZan.setOnClickListener(clickListener);
//        holder.webView.setOnClickListener(clickListener); //WebView点击无效
        holder.imgVideo.setOnClickListener(clickListener);
        return holder;
    }

    void setPlayHtml(final VideoModel model, WebView webView){
        Log.d(TAG, "setPlayHtml: ");
        playerModel = model;
        String url = "file:///android_asset/video.html";
        parseWebView = webView;

        WebViewUtil.setWebView(mInflater.getContext(), parseWebView);
//        WebViewUtil.setX5WebView(parseWebView);
        parseWebView.loadUrl(url);

        parseWebView.addJavascriptInterface(new WebViewParseClass(), "app");

        parseWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String startJs = String.format("javascript:setVideoContent('%s','%s')",model.video_id,model.img_url);
                Log.d(TAG, "onPageFinished: "+startJs);
                parseWebView.loadUrl(startJs);
//                parseWebView.setLayoutParams(holder.imgVideo.getLayoutParams());
            }
        });

    }



    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        VideoModel model = mItems.get(position);
        MyViewHolder holder = (MyViewHolder) viewHolder;

        Glide.with(mInflater.getContext())
                .load(model.img_url)
                .placeholder(R.drawable.default_img)
                .into(holder.imgVideo);
        Glide.with(mInflater.getContext())
                .load(model.author_logo)
                .into(holder.imgAuther);
        Log.d(TAG, "onBindViewHolder: "+position);

        holder.tvTitle.setText(model.title);
        holder.tvReadTimes.setText(model.getPlayTimesStr());

        holder.tvSource.setText(model.author);
        holder.tvPlayTimes.setText(model.getPlayTimes());
        holder.tvLength.setText(model.getVideo_duration());

        holder.tvZan.setText(model.video_like_count+"");
        holder.tvZan.setTextColor(model.is_fabulous? R.color.text_red:R.color.colorDarker_gray);
        holder.tvZan.setEnabled(!model.is_fabulous);

        holder.tvComments.setText(model.comment_count+"");
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


    class WebViewParseClass {

        @JavascriptInterface
        public void parseData(String src, int height){ //
            L.d(TAG, "parseData: "+height +", src=" +src);
            playerModel.src = src;
            parseWebView.post(new Runnable() {
                @Override
                public void run() {
                    jzvdPlayer.setVisibility(View.VISIBLE);
                    jzvdPlayer.setUp(src, playerModel.title, Jzvd.SCREEN_NORMAL);
                    Glide.with(context).load(playerModel.large_img_url).into(jzvdPlayer.thumbImageView);
                }
            });


        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvReadTimes;
        private ImageView imgVideo;
        private ImageView imgAuther;

        public WebView webView; //仅解析
        JzvdStd jzvdStdPlayer ;

        private TextView tvSource;
        private TextView tvPlayTimes;
        private TextView tvZan;
        private TextView tvLength;
        private TextView tvComments;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            imgAuther = itemView.findViewById(R.id.img_author);
            imgVideo = itemView.findViewById(R.id.img_video);
            tvReadTimes = itemView.findViewById(R.id.tv_readTimes);
            webView = itemView.findViewById(R.id.webview);
            jzvdStdPlayer = itemView.findViewById(R.id.jzvdPlayer);

            tvSource = itemView.findViewById(R.id.tv_from);
            tvZan = itemView.findViewById(R.id.tv_zan);
            tvPlayTimes = itemView.findViewById(R.id.tv_playTimes);
            tvLength = itemView.findViewById(R.id.tv_videoLength);

            tvComments = itemView.findViewById(R.id.tv_comments);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
