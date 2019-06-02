package com.juejinchain.android.ui.ppw;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.PopupWindow;

import com.juejinchain.android.R;

/**
 * bottom tab ppw
 */
public class HomeBottomTipsPopupWindow extends PopupWindow {
    private Context mContext;

    private View mContentView;


    public HomeBottomTipsPopupWindow(Context context) {
        super(context);
        mContext = context;

        initView();
    }

    private void initView(){
        setFocusable(false);
        setOutsideTouchable(false);
        setTouchable(true);
        setBackgroundDrawable(new ColorDrawable());

        mContentView = View.inflate(mContext, R.layout.ling_prompt_bottom, null);
        setContentView(mContentView);

        mContentView.setOnClickListener(v -> dismiss());
    }
}
