package com.juejinchain.android.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.juejinchain.android.R;
import com.juejinchain.android.ui.dialog.HomeTipsAlertDialog;

/**
 * ads
 */
public class AdsHolderView extends LinearLayout {
    private ImageView icon;

    public AdsHolderView(Context context) {
        this(context, null);
    }

    public AdsHolderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View.inflate(context, R.layout.layout_home_ads, this);
        icon = findViewById(R.id.icon);
        icon.setOnClickListener(v -> {
            new HomeTipsAlertDialog(context).show();
        });
    }

    public void setIcon(String url){
        if(!TextUtils.isEmpty(url)){
            setVisibility(VISIBLE);
            Glide.with(getContext())
                    .load(url)
                    .into(icon);
        }else{
            setVisibility(GONE);
        }
    }
}
