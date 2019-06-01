package com.juejinchain.android.ui.fragment;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;

import com.juejinchain.android.R;

import razerdp.basepopup.BasePopupWindow;

public class PromptGetPopup extends BasePopupWindow {

    public PromptGetPopup(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        View v = createPopupById(R.layout.ling_prompt);
        return v;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("basePopup", "onTouchEvent: ");
        if (event.getAction() == MotionEvent.ACTION_UP){
            dismissWithOutAnimate();
        }
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onOutSideTouch() {
//        return super.onOutSideTouch();
        return false;  //不消失
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onDispatchKeyEvent(KeyEvent event) {
        return super.onDispatchKeyEvent(event);
    }
}
