package com.juejinchain.android.ui.ppw;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.juejinchain.android.R;

import razerdp.basepopup.BasePopupWindow;

/**
 *
 * 广告想移除弹窗
 *
 */
public class ADRemovePopup extends BasePopupWindow implements View.OnClickListener {

    private ImageView mLikeAnimaView;
    private TextView mLikeText;

    private View mLikeClikcLayout;
    private View mCommentClickLayout;


    private OnCommentPopupClickListener mOnCommentPopupClickListener;

    private Handler mHandler;

    public ADRemovePopup(Context context) {
        super(context);
        mHandler = new Handler();

        mLikeAnimaView = (ImageView) findViewById(R.id.iv_like);
        mLikeText = (TextView) findViewById(R.id.tv_like);

        mLikeClikcLayout = findViewById(R.id.item_like);
        mCommentClickLayout = findViewById(R.id.ly_popup);

        mLikeClikcLayout.setOnClickListener(this);
        mCommentClickLayout.setOnClickListener(this);

        buildAnima();
        setBackground(R.color.bg_popup);
        setOutSideDismiss(true);
        setKeepSize(true);
        setPopupGravity(Gravity.LEFT | Gravity.TOP);
        setBlurBackgroundEnable(true);
    }

    private AnimationSet mAnimationSet;

    private void buildAnima() {
        ScaleAnimation mScaleAnimation = new ScaleAnimation(1f, 2f, 1f, 2f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleAnimation.setDuration(200);
        mScaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mScaleAnimation.setFillAfter(false);

        AlphaAnimation mAlphaAnimation = new AlphaAnimation(1, .2f);
        mAlphaAnimation.setDuration(400);
        mAlphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAlphaAnimation.setFillAfter(false);

        mAnimationSet = new AnimationSet(false);
        mAnimationSet.setDuration(400);
        mAnimationSet.addAnimation(mScaleAnimation);
        mAnimationSet.addAnimation(mAlphaAnimation);
        mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 150);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


//    @Override
//    protected Animation onCreateShowAnimation() {
//        Animation showAnima = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
//                1f,
//                Animation.RELATIVE_TO_PARENT,
//                0,
//                Animation.RELATIVE_TO_PARENT,
//                0,
//                Animation.RELATIVE_TO_PARENT,
//                0);
//        showAnima.setInterpolator(new DecelerateInterpolator());
//        showAnima.setDuration(350);
//        return showAnima;
//    }

//    @Override
//    protected Animation onCreateDismissAnimation() {
//        Animation exitAnima = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
//                0f,
//                Animation.RELATIVE_TO_PARENT,
//                1f,
//                Animation.RELATIVE_TO_PARENT,
//                0,
//                Animation.RELATIVE_TO_PARENT,
//                0);
//        exitAnima.setInterpolator(new DecelerateInterpolator());
//        exitAnima.setDuration(350);
//        return exitAnima;
//    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.popup_ad_dislike);
    }
    //=============================================================Getter/Setter

    public OnCommentPopupClickListener getOnCommentPopupClickListener() {
        return mOnCommentPopupClickListener;
    }

    public void setOnCommentPopupClickListener(OnCommentPopupClickListener onCommentPopupClickListener) {
        mOnCommentPopupClickListener = onCommentPopupClickListener;
    }

    //=============================================================clickEvent
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_popup:
                if (mOnCommentPopupClickListener != null) {
                    mOnCommentPopupClickListener.onClick(v);
                    mLikeAnimaView.clearAnimation();
//                    mLikeAnimaView.startAnimation(mAnimationSet);
                    dismiss();
                }
                break;
        }
    }

    //=============================================================InterFace
    public interface OnCommentPopupClickListener {
        void onClick(View v);

//        void onCommentClick(View v);
    }
}
