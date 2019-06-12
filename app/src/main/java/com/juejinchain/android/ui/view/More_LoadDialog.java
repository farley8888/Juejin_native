package com.juejinchain.android.ui.view;

import android.app.Dialog;
import android.content.Context;

import com.juejinchain.android.R;

/**
 * 作者：created by meixi
 * 邮箱：15913707499@163.com
 * 日期：2019/6/12 11
 */

public class More_LoadDialog extends Dialog {


    public More_LoadDialog(Context context) {
        super(context, R.style.TransparentStyle);
        init();
    }

    private void init() {
        setContentView(R.layout.loading_view);
        setCanceledOnTouchOutside(false);
//        setCancelable(false);
    }

    @Override
    public void show() {
        if (!this.isShowing())
            super.show();
    }

    @Override
    public void dismiss() {
        if (this.isShowing())
            super.dismiss();
    }
}
