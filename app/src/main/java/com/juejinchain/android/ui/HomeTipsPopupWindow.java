package com.juejinchain.android.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.PopupWindow;

import com.juejinchain.android.R;

public class HomeTipsPopupWindow extends PopupWindow {
    private Context mContext;

    private View mContentView;


    public HomeTipsPopupWindow(Context context) {
        super(context);
        mContext = context;

        initView();
    }

    private void initView(){
        setFocusable(false);
        setOutsideTouchable(false);
        setTouchable(true);
        setBackgroundDrawable(new ColorDrawable());

        mContentView = View.inflate(mContext, R.layout.ling_prompt, null);
        setContentView(mContentView);

        mContentView.setOnClickListener(v -> dismiss());
    }

    public int getContentHeight() {
        mContentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return mContentView.getMeasuredHeight();
    }
}
