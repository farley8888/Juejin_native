package com.juejinchain.android.ui.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juejinchain.android.MyApplication;
import com.juejinchain.android.R;


/**
 * 进度条对话框，播放菊花gif动画显示等待状态
 * @author
 *
 */
public class AlertProDialog {

	private static WindowManager mWindowManager;
	private static View mView;
	private Context context;
	private static Dialog dialog;
	private ViewGroup rootLayout;//布局根目录
	private Display display;//屏幕分辨率
//	private GifView gf1;//进度条gif动画


	public AlertProDialog(Context context) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}

	public AlertProDialog builder() {
		// 获取Dialog布局
		View view = LayoutInflater.from(context).inflate(
				R.layout.loading_view, null);

		// 获取自定义Dialog布局中的控件
		rootLayout = view.findViewById(R.id.root);
		  // 从xml中得到GifView的句柄  

		// 定义Dialog布局和参数
		dialog = new Dialog(context, R.style.TransparentStyle);
		dialog.setContentView(view);

		// 调整dialog背景大小
		rootLayout.setLayoutParams(new FrameLayout.LayoutParams((int) (display
				.getWidth() * 0.85), 300));

		return this;
	}

	/**
	 * 显示弹出框
	 *
	 * @param context
	 * , final DialogCallback callback
	 */
	public static void showPopupWindow(final Context context) {
		// 获取WindowManager
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);


		final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		// 类型, 需要 <!--全局弹窗的权限-->
		//    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		// 设置flag
		params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		// 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
		// 不设置这个弹出框的透明遮罩显示为黑色
		params.format = PixelFormat.TRANSLUCENT;
		// FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
		// 设置 FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按
		// 不设置这个flag的话，home页的划屏会有问题
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.CENTER;
		TextView textView = new TextView(context);
		textView.setText("sfgsfdsfbsadfbasdfg");
		textView.setTextSize(100);

		mView = LayoutInflater.from(context).inflate(R.layout.loading_view, null);
//		TextView tv_itemdialog_title = (TextView) mView.findViewById(R.id.tv_itemdialog_title);
//		TextView tv_itemdialog_ok = (TextView) mView.findViewById(R.id.tv_itemdialog_ok);
//		TextView tv_itemdialog_close = (TextView) mView.findViewById(R.id.tv_itemdialog_close);

//		tv_itemdialog_ok.setText("重新登录");
//		tv_itemdialog_close.setText("退出登录");
//		tv_itemdialog_title.setText("该账户在其他设备登录,若不是您在操作,请及时修改密码以防泄露信息");
//		tv_itemdialog_ok.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// 隐藏弹窗
//				mWindowManager.removeView(mView);
//				callback.onPositive();
//			}
//		});
//
//		tv_itemdialog_close.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mWindowManager.removeView(mView);
//				callback.onNegative();
//			}
//		});

		mWindowManager.addView(textView, params);
	}

	/**
	 * @param isCancelable
	 */
	public static void showLoading(final Boolean isCancelable) {

		try {

			Context context = MyApplication.currentAct;
			if (context == null)
				//Unable to add window -- token null is not valid; is your activity running?
				context = MyApplication.getInstance(); //这个context 部分手机会出现以上问题


			AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TransparentStyle);
			/*true 代表点击空白可消失   false代表点击空白哦不可消失 */
			builder.setCancelable(isCancelable);
			View view = View.inflate(context, R.layout.loading_view, null);
	//		TextView tv_itemdialog_ok = (TextView) view.findViewById(R.id.tv_itemdialog_ok);
	//		TextView tv_itemdialog_close = (TextView) view.findViewById(R.id.tv_itemdialog_close);

	//		tv_itemdialog_ok.setText(ok);
	//		tv_itemdialog_close.setText(close);

	//		builder.setView(view);
			dialog = builder.create();
			//设置弹出全局对话框
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
			//设置昏暗度为0
			dialog.getWindow().setDimAmount(0.2f);

			dialog.show();
			//设置自定义布局，必须要在show之后, 否则透明度无效
			dialog.getWindow().setContentView(view);
	//		dialog.getWindow().setContentView(R.layout.layout_menu_black);

		}catch (Exception e){
			e.printStackTrace();
		}

	}


	/**
	 * 取消进度条
	 */
	public static void dissmiss(){
		if (mWindowManager !=null ){
			mWindowManager.removeView(mView);
		}
		if (dialog !=null) dialog.dismiss();
//		if (context == null) return;

//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//			if(((Activity)context).isDestroyed()){
//               return;
//            }
//		}
//		if (!((Activity)context).isFinishing()&&dialog!=null&&dialog.isShowing()){
//			dialog.dismiss();
//		}
	}
}
