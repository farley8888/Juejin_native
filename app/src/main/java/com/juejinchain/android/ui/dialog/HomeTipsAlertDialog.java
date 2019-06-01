package com.juejinchain.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.dmcbig.mediapicker.utils.ScreenUtils;
import com.juejinchain.android.R;

/**
 * 首页弹窗提示
 */
public class HomeTipsAlertDialog extends Dialog {
    public HomeTipsAlertDialog(Context context) {
        this(context,0);
    }

    public HomeTipsAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_home_alert);

        Window window = getWindow();
        WindowManager.LayoutParams params =  window.getAttributes();
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        params.width = ScreenUtils.getScreenWidth(context) - ScreenUtils.dp2px(context, 60);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.windowAnimations = R.style.bottomSlideAnim;

        getWindow().setAttributes(params);

        initView();
    }

    private void initView(){
        findViewById(R.id.cancel).setOnClickListener(v -> dismiss());
    }
}
