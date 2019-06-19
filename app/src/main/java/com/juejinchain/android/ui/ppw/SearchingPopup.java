package com.juejinchain.android.ui.ppw;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.juejinchain.android.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * 搜索中loading
 */
public class SearchingPopup extends BasePopupWindow {

    public SearchingPopup(Context context) {
        super(context);

        setBackground(null); //透明背景

        ImageView img = findViewById(R.id.img_searching);
        Glide.with(context).load(R.drawable.searching).into(img);
        img.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 8000);
    }

    @Override
    public boolean onOutSideTouch() {
        return false;
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.loading_searching);
    }


}
