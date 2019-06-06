package com.juejinchain.android.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtil {

    public static void hide(Context context){
        Activity activity = (Activity) context;
        /*隐藏软键盘*/
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static void show(Context context, View v){
        Activity activity = (Activity) context;
        /*隐藏软键盘*/
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.showSoftInput(v, 0);
        }
    }


}
