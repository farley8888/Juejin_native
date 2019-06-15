package com.juejinchain.android.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Point;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

public class AnimUtil {
    public static void animHeightToView(final View v, final int start, final int end, final boolean isToShow,long duration) {
        ValueAnimator va = ValueAnimator.ofInt(start, end);
        final ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        va.addUpdateListener(animation -> {
            int h = (int) animation.getAnimatedValue();
            layoutParams.height = h;
            v.setLayoutParams(layoutParams);
            v.requestLayout();
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (isToShow) {
                    v.setVisibility(View.VISIBLE);
                }
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isToShow) {
                    v.setVisibility(View.GONE);
                }else{
                    new Handler().postDelayed(() -> animHeightToView(v, v.getLayoutParams().height, 0, false, duration), 1000);
                }
            }
        });
        va.setDuration(duration);
        va.setStartDelay(500);
        va.start();
    }

    public static void animHeightToView(final Activity context, final View v, final boolean isToShow, final long duration) {
        if (isToShow) {
            Display display = context.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            v.measure(size.x, size.y);
            int end = v.getMeasuredHeight();
            animHeightToView(v, 0, end, isToShow, duration);
        } else {
            animHeightToView(v, v.getLayoutParams().height, 0, isToShow, duration);
        }
    }
}