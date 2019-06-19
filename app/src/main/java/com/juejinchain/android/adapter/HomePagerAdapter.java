package com.juejinchain.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.callback.ImageOptions;
import com.bumptech.glide.Glide;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.juejinchain.android.R;
import com.juejinchain.android.model.NewsModel;
import com.juejinchain.android.ui.fragment.OnItemClickListener;
import com.juejinchain.android.tools.TToast;
import com.juejinchain.android.util.XSpanUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 文章列表adapter 加入了穿山甲信息流
 * RecyclerAdapterWithHF
 * RecyclerView.Adapter<HomePagerAdapter.MyViewHolder>
 */
public class HomePagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_NORMAL = 0;
    private static final int ITEM_VIEW_TYPE_GROUP_PIC_AD = 1;
    private static final int ITEM_VIEW_TYPE_SMALL_PIC_AD = 2;
    private static final int ITEM_VIEW_TYPE_LARGE_PIC_AD = 3;
    private static final int ITEM_VIEW_TYPE_VIDEO = 4;

    private List<Object> mItems = new ArrayList<>();
    private LayoutInflater mInflater;
//    GlideRoundTransform glideRoundTransform ;
    private String mSearchKey; //搜索页面的关键字
    private XSpanUtils mSpanUtils;

    private OnItemClickListener mClickListener;
    private Context mContext;

//    private AQuery2 mAQuery;//AQuery2可以播放gif。有问题，改用glide

    public HomePagerAdapter(Context context, List<Object> data) {
        super();
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mItems = data;
//        mAQuery = new AQuery2(context);
        //加圆角 centerCrop() 属性会影响 GlideRoundTransform 对象属性
//        glideRoundTransform = new GlideRoundTransform(mInflater.getContext(), 5);
    }

    public void setSearchKey(String searchKey) {
        this.mSearchKey = searchKey;
    }

    public HomePagerAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    //非根adapter不要用此方法
    public void setDatas(List<String> items) {
        mItems.clear();
//        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case ITEM_VIEW_TYPE_NORMAL:
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
            case ITEM_VIEW_TYPE_SMALL_PIC_AD:
                return new SmallAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_small_pic, parent, false));
            case ITEM_VIEW_TYPE_LARGE_PIC_AD:
                return new LargeAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_large_pic, parent, false));
            case ITEM_VIEW_TYPE_GROUP_PIC_AD:
                return new GroupAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_group_pic, parent, false));
            case ITEM_VIEW_TYPE_VIDEO:
//                return new VideoAdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_large_video, parent, false));
            default:
                View defaultView = mInflater.inflate(R.layout.item_other, parent, false);
                return new RecyclerView.ViewHolder(defaultView) {

                };
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mItems != null) {
//            if (position >= mItems.size())
//            return ITEM_VIEW_TYPE_LOAD_MORE;

            if (mItems.get(position) instanceof NewsModel){
                return ITEM_VIEW_TYPE_NORMAL;
            }
            else {
                TTFeedAd ad = (TTFeedAd) mItems.get(position);
                if (ad == null) {
                    return ITEM_VIEW_TYPE_NORMAL;
                } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_SMALL_IMG) {
                    return ITEM_VIEW_TYPE_SMALL_PIC_AD;
                } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_LARGE_IMG) {
                    return ITEM_VIEW_TYPE_LARGE_PIC_AD;
                } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_GROUP_IMG) {
                    return ITEM_VIEW_TYPE_GROUP_PIC_AD;
                } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) {
                    return ITEM_VIEW_TYPE_VIDEO;
                } else {
                    TToast.show(mContext, "图片展示样式错误");
                    return ITEM_VIEW_TYPE_NORMAL;
                }
            }
        }

        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (mItems.get(position) instanceof  NewsModel){
            bindMyViewHolder(viewHolder, position);
        }else {
            bindAdViewHolder(viewHolder, position);
        }
    }

    //广告viewHolder
    private void bindAdViewHolder(RecyclerView.ViewHolder holder, int position) {
        TTFeedAd ttFeedAd = (TTFeedAd) mItems.get(position);
        if (holder instanceof SmallAdViewHolder) {
            SmallAdViewHolder smallAdViewHolder = (SmallAdViewHolder) holder;
            bindData(smallAdViewHolder, ttFeedAd);
            if (ttFeedAd.getImageList() != null && !ttFeedAd.getImageList().isEmpty()) {
                TTImage image = ttFeedAd.getImageList().get(0);
                if (image != null && image.isValid()) {
//                    mAQuery.id(smallAdViewHolder.mSmallImage).image(image.getImageUrl());
                    Glide.with(mContext).load(image.getImageUrl()).into(smallAdViewHolder.mSmallImage);
                }
            }

        } else if (holder instanceof LargeAdViewHolder) {
            LargeAdViewHolder largeAdViewHolder = (LargeAdViewHolder) holder;
            bindData(largeAdViewHolder, ttFeedAd);
            if (ttFeedAd.getImageList() != null && !ttFeedAd.getImageList().isEmpty()) {
                TTImage image = ttFeedAd.getImageList().get(0);
                if (image != null && image.isValid()) {
//                    mAQuery.id(largeAdViewHolder.mLargeImage).image(image.getImageUrl());
                    Glide.with(mContext).load(image.getImageUrl()).into(largeAdViewHolder.mLargeImage);
                }
            }

        } else if (holder instanceof GroupAdViewHolder) {
            GroupAdViewHolder groupAdViewHolder = (GroupAdViewHolder) holder;
            bindData(groupAdViewHolder, ttFeedAd);
            if (ttFeedAd.getImageList() != null && ttFeedAd.getImageList().size() >= 3) {
                TTImage image1 = ttFeedAd.getImageList().get(0);
                TTImage image2 = ttFeedAd.getImageList().get(1);
                TTImage image3 = ttFeedAd.getImageList().get(2);
                if (image1 != null && image1.isValid()) {
//                    mAQuery.id(groupAdViewHolder.mGroupImage1).image(image1.getImageUrl());
                    Glide.with(mContext).load(image1.getImageUrl()).into(groupAdViewHolder.mGroupImage1);
                }
                if (image2 != null && image2.isValid()) {
//                    mAQuery.id(groupAdViewHolder.mGroupImage2).image(image2.getImageUrl());
                    Glide.with(mContext).load(image2.getImageUrl()).into(groupAdViewHolder.mGroupImage2);
                }
                if (image3 != null && image3.isValid()) {
//                    mAQuery.id(groupAdViewHolder.mGroupImage3).image(image3.getImageUrl());
                    Glide.with(mContext).load(image3.getImageUrl()).into(groupAdViewHolder.mGroupImage3);
                }
            }
        }
    }

    /**
     * 绑定广告数据
     * @param adViewHolder
     * @param ad
     */
    private void bindData(final AdViewHolder adViewHolder, TTFeedAd ad) {
        //可以被点击的view, 也可以把convertView放进来意味item可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(adViewHolder.itemView);
        //触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        creativeViewList.add(adViewHolder.mCreativeButton);
        //如果需要点击图文区域也能进行下载或者拨打电话动作，请将图文区域的view传入
//            creativeViewList.add(convertView);
        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        ad.registerViewForInteraction((ViewGroup) adViewHolder.itemView, clickViewList, creativeViewList, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                if (ad != null) {
                    TToast.show(mContext, "广告" + ad.getTitle() + "被点击");
                }
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                if (ad != null) {
                    TToast.show(mContext, "广告" + ad.getTitle() + "被创意按钮被点击");
                }
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                if (ad != null) {
//                    TToast.show(mContext, "广告" + ad.getTitle() + "展示");
                }
            }
        });
        adViewHolder.mTitle.setText(ad.getTitle());
        adViewHolder.mDescription.setText(ad.getDescription());
        adViewHolder.mSource.setText(ad.getSource() == null ? "广告来源" : ad.getSource());
        TTImage icon = ad.getIcon();
        if (icon != null && icon.isValid()) {
            ImageOptions options = new ImageOptions();
//            mAQuery.id(adViewHolder.mIcon).image(icon.getImageUrl(), options);
            Glide.with(mContext).load(icon.getImageUrl()).into(adViewHolder.mIcon);
        }
        Button adCreativeButton = adViewHolder.mCreativeButton;
        switch (ad.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
                if (mContext instanceof Activity) {
                    ad.setActivityForDownloadApp((Activity) mContext);
                }
                adCreativeButton.setVisibility(View.VISIBLE);
                adViewHolder.mStopButton.setVisibility(View.VISIBLE);
                adViewHolder.mRemoveButton.setVisibility(View.VISIBLE);
//                bindDownloadListener(adCreativeButton, adViewHolder, ad);
//                //绑定下载状态控制器
//                bindDownLoadStatusController(adViewHolder, ad);
                break;
            case TTAdConstant.INTERACTION_TYPE_DIAL:
                adCreativeButton.setVisibility(View.VISIBLE);
                adCreativeButton.setText("立即拨打");
                adViewHolder.mStopButton.setVisibility(View.GONE);
                adViewHolder.mRemoveButton.setVisibility(View.GONE);
                break;
            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
            case TTAdConstant.INTERACTION_TYPE_BROWSER:
//                    adCreativeButton.setVisibility(View.GONE);
                adCreativeButton.setVisibility(View.VISIBLE);
                adCreativeButton.setText("查看详情");
                adViewHolder.mStopButton.setVisibility(View.GONE);
                adViewHolder.mRemoveButton.setVisibility(View.GONE);
                break;
            default:
                adCreativeButton.setVisibility(View.GONE);
                adViewHolder.mStopButton.setVisibility(View.GONE);
                adViewHolder.mRemoveButton.setVisibility(View.GONE);
                TToast.show(mContext, "交互类型异常");
        }
    }

    //文章viewHolder
    private void bindMyViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        NewsModel model = (NewsModel) mItems.get(position);
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

        if (mSearchKey != null && model.getTitle().indexOf(mSearchKey)!=-1){
            holder.tvTitle.setText(getSearchTitle(model.getTitle()));
        }else {
            holder.tvTitle.setText(model.getTitle());
        }
        holder.tvHot.setText(model.getIs_top());
        holder.tvSource.setText(model.source);
        holder.tvReadTimes.setText(model.getRead_num());
        holder.tvDate.setText(model.getPublish_time());
    }

    SpannableStringBuilder getSearchTitle(String title){
        if (mSpanUtils == null) mSpanUtils = new XSpanUtils(mInflater.getContext());

        String first = title.substring(0, title.indexOf(mSearchKey));
        String end = title.substring(title.indexOf(mSearchKey)+mSearchKey.length());
        //搜索关键字颜色
        int keyColor = mInflater.getContext().getResources().getColor(R.color.text_yellow);
        return mSpanUtils.append(first).append(mSearchKey).setForegroundColor(keyColor).append(end).create();
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

    private static class LargeAdViewHolder extends AdViewHolder {
        ImageView mLargeImage;

        @SuppressWarnings("RedundantCast")
        public LargeAdViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
            mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
            mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
            mLargeImage = (ImageView) itemView.findViewById(R.id.iv_listitem_image);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
            mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);
            mStopButton = (Button) itemView.findViewById(R.id.btn_listitem_stop);
            mRemoveButton = (Button) itemView.findViewById(R.id.btn_listitem_remove);
        }
    }

    private static class SmallAdViewHolder extends AdViewHolder {
        ImageView mSmallImage;

        @SuppressWarnings("RedundantCast")
        public SmallAdViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
            mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
            mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
            mSmallImage = (ImageView) itemView.findViewById(R.id.iv_listitem_image);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
            mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);
            mStopButton = (Button) itemView.findViewById(R.id.btn_listitem_stop);
            mRemoveButton = (Button) itemView.findViewById(R.id.btn_listitem_remove);
        }
    }

    @SuppressWarnings("CanBeFinal")
    private static class GroupAdViewHolder extends AdViewHolder {
        ImageView mGroupImage1;
        ImageView mGroupImage2;
        ImageView mGroupImage3;

        @SuppressWarnings("RedundantCast")
        public GroupAdViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.tv_listitem_ad_title);
            mSource = (TextView) itemView.findViewById(R.id.tv_listitem_ad_source);
            mDescription = (TextView) itemView.findViewById(R.id.tv_listitem_ad_desc);
            mGroupImage1 = (ImageView) itemView.findViewById(R.id.iv_listitem_image1);
            mGroupImage2 = (ImageView) itemView.findViewById(R.id.iv_listitem_image2);
            mGroupImage3 = (ImageView) itemView.findViewById(R.id.iv_listitem_image3);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_listitem_icon);
            mCreativeButton = (Button) itemView.findViewById(R.id.btn_listitem_creative);
            mStopButton = (Button) itemView.findViewById(R.id.btn_listitem_stop);
            mRemoveButton = (Button) itemView.findViewById(R.id.btn_listitem_remove);
        }
    }

    private static class AdViewHolder extends RecyclerView.ViewHolder {
        ImageView mIcon;
        Button mCreativeButton;
        TextView mTitle;
        TextView mDescription;
        TextView mSource;
        Button mStopButton;
        Button mRemoveButton;

        public AdViewHolder(View itemView) {
            super(itemView);
        }
    }

}
