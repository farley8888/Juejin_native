package com.juejinchain.android.ui.ppw;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.PopupWindow;

import com.juejinchain.android.R;

public class HomeTipsPopupWindow extends PopupWindow {
    private Context mContext;

    private View mContentView;
    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

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

        mContentView.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    if(onClickListener != null) onClickListener.onClick(mContentView);
                }
            }
        );

    }
}
