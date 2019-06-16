package com.juejinchain.android;

import io.dcloud.EntryProxy;
import io.dcloud.RInformation;
import io.dcloud.common.DHInterface.IApp;
import io.dcloud.common.DHInterface.IApp.IAppStatusListener;
import io.dcloud.common.DHInterface.ICore;
import io.dcloud.common.DHInterface.ICore.ICoreStatusListener;
import io.dcloud.common.DHInterface.IOnCreateSplashView;
import io.dcloud.common.DHInterface.ISysEventListener.SysEventType;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.IWebviewStateListener;
import io.dcloud.common.adapter.ui.ReceiveJSValue;
import io.dcloud.common.util.BaseInfo;
import io.dcloud.common.util.ImageLoaderUtil;
import io.dcloud.feature.internal.sdk.SDK;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSONObject;
import com.juejinchain.android.H5Plugin.MyPlugin;
import com.juejinchain.android.tools.L;
import com.juejinchain.android.ui.fragment.MainFragment;

import org.json.JSONArray;

/**
 * 本demo为以widget方式集成5+ sdk，
 *
 */
public class SDK_WebApp extends Activity {

	boolean doHardAcc = true;
	EntryProxy mEntryProxy = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (mEntryProxy == null) {
			FrameLayout f = new FrameLayout(this);
			// 创建5+内核运行事件监听
			WebappModeListener wm = new WebappModeListener(this, f, "1");
			// 初始化5+内核
			mEntryProxy = EntryProxy.init(this, wm);
			// 启动5+内核
			mEntryProxy.onCreate(this, savedInstanceState, SDK.IntegratedMode.WEBAPP, null);
			setContentView(f);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return mEntryProxy.onActivityExecute(this, SysEventType.onCreateOptionMenu, menu);
	}

	@Override
	public void onPause() {
		super.onPause();
		mEntryProxy.onPause(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && BaseInfo.mDeStatusBarBackground == -111111) {
			BaseInfo.mDeStatusBarBackground = getWindow().getStatusBarColor();
		}
		mEntryProxy.onResume(this);
	}

	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getFlags() != 0x10600000) {// 非点击icon调用activity时才调用newintent事件
			mEntryProxy.onNewIntent(this, intent);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mEntryProxy.onStop(this);
	}

	@Override
	public void onBackPressed() {
		boolean _ret = mEntryProxy != null && mEntryProxy.onActivityExecute(this, SysEventType.onKeyUp, new Object[]{KeyEvent.KEYCODE_BACK, null});
		if(!_ret && mEntryProxy != null){
			mEntryProxy.destroy(this);
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyDown, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyUp, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyLongPress, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyLongPress(keyCode, event);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		if (newConfig.fontScale != 1)//非默认值
			getResources();
		try {
			int temp = this.getResources().getConfiguration().orientation;
			if (mEntryProxy != null) {
				mEntryProxy.onConfigurationChanged(this, temp);
			}
			super.onConfigurationChanged(newConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//系统字体变大导致的布局异常
	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		if (res.getConfiguration().fontScale != 1) {//非默认值
			Configuration newConfig = new Configuration();
			newConfig.setToDefaults();//设置默认
			res.updateConfiguration(newConfig, res.getDisplayMetrics());
		}
		return res;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mEntryProxy.onActivityExecute(this, SysEventType.onActivityResult, new Object[] { requestCode, resultCode, data });
	}
}

class WebappModeListener implements ICoreStatusListener, IOnCreateSplashView {

	final String TAG = "WebappModeListener";
	boolean vueLaunchFinish;
	Activity activity;
	View splashView = null;
	ViewGroup rootView;
	IApp app = null;
	ProgressDialog pd = null;
	String name =null;
	IWebview iWebview;
	boolean switchOK ; //是否切换完成
	int switchTimes;

	public WebappModeListener(Activity activity, ViewGroup rootView, String name) {
		this.activity = activity;
		this.rootView = rootView;
		this.name = name;
	}

	public void switchPage(final String page, @Nullable ReceiveJSValue.ReceiveJSValueCallback jsCallback){
//        iWebview.evalJS("nativeJump('"+page+"')");
		L.d(TAG, "switchPage: "+page);
		if (!vueLaunchFinish){
			L.w(TAG, "switchPage: vue还未启动完");
			return;
		}
		iWebview.evalJS("nativeJump('"+page+"')", new ReceiveJSValue.ReceiveJSValueCallback() {
			@Override
			public String callback(JSONArray jsonArray) {
				L.d(TAG, "SwitchPage.callback: = "+jsonArray);
				switchOK = true;
				if(jsCallback != null) jsCallback.callback(jsonArray);
				return "{}";
			}
		});

//		rootView.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				if (!switchOK && switchTimes<2){
//					switchTimes++;
//					switchPage(page);
//				}else{
//					switchOK = false;
//					switchTimes = 0;
//				}
//			}
//		}, 1500);
	}

	/**
	 * 调用vue 的分享
	 * @param way 		eg: qq, weibo, system, friend, wechat
	 * @param jsonParam eg: {"title":"掘金宝0006即将把宝马X", "content":"赚豪车赚洋房，回家过年提车就靠它了,
	 *                  	"thumbs":["http://jjlmobile.oss-cn-shenzhimage/resize,h_50"],
	 *               		"href":"http://wechat.juejinbaonews.fun/?path=%2F&inviteCode=6812939" }
	 * @param from 		eg: index, task etc.
	 */
	public void shareTo(String way, String jsonParam, String from){

		iWebview.evalJS(String.format("shareFn('%s','%s','%s')", way, jsonParam, from), new ReceiveJSValue.ReceiveJSValueCallback() {
			@Override
			public String callback(JSONArray jsonArray) {
				L.d(TAG, "shareTo.callback: ");
				return "{}";
			}
		});

	}

	/**
	 * 保存阅读文章记录
	 * @param json
	 */
	public void saveReadArticle(String json){
		//window.setHistory vue页面的
		iWebview.evalJS(String.format("setHistory('%s')", json), new ReceiveJSValue.ReceiveJSValueCallback() {
			@Override
			public String callback(JSONArray jsonArray) {
				L.d(TAG, "saveReadArticle.callback: ");
				return "{}";
			}
		});
	}

	/**
	 * 返回vue首页，空白的
	 */
	public void backVueIndex(){
		Log.d(TAG, "backVueIndex: ");
		iWebview.evalJS("backIndex()", new ReceiveJSValue.ReceiveJSValueCallback() {
			@Override
			public String callback(JSONArray jsonArray) {
				L.d(TAG, "backVueIndex.callback: ");
				return "{}";
			}
		});
	}

	/**
	 * 调用vue 返回
	 */
	public void callVueBack(){

		iWebview.evalJS("nativeBack()", new ReceiveJSValue.ReceiveJSValueCallback() {
			@Override
			public String callback(JSONArray jsonArray) {
				L.d(TAG, "callVueBack.callback: ");
				MyPlugin.VueCallBackTimes++;
				rootView.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (MyPlugin.VueCallBackTimes > 1){
							MainActivity mainActivity = (MainActivity) iWebview.getActivity();
							mainActivity.mainFragment.showHomeFragment();
							MyPlugin.VueCallBackTimes = 0;
						}
					}
				},300);
				return "{}";
			}
		});
	}

	/**
	 * 5+内核初始化完成时触发
	 * */
	@Override
	public void onCoreInitEnd(ICore coreHandler) {
		L.d("webAppListener."+name, "onCoreInitEnd: ");
		// 表示Webapp的路径在 file:///android_asset/apps/HelloH5
//		String appBasePath = "/apps/HelloH5";
		String appBasePath = "/apps/juejin";

		// 设置启动参数,可在页面中通过plus.runtime.arguments;方法获取到传入的参数
		String args = "{url:'http://www.baidu.com'}";

		// 启动启动独立应用的5+ Webapp
		app = SDK.startWebApp(activity, appBasePath, args, new IWebviewStateListener() {
			// 设置Webview事件监听，可在监监听内获取WebIvew加载内容的进度
			@Override
			public Object onCallBack(int pType, Object pArgs) {
				switch (pType) {
					case IWebviewStateListener.ON_WEBVIEW_READY:
						// WebApp准备加载事件
						// 准备完毕之后添加webview到显示父View中，
						// 设置排版不显示状态，避免显示webview时html内容排版错乱问题
						iWebview =  (IWebview) pArgs;
						View view = iWebview.obtainApp().obtainWebAppRootView().obtainMainView();
						view.setVisibility(View.INVISIBLE);

						L.d("webAppListener."+name, "ON_WEBVIEW_READY: ");
						if(view.getParent() != null){
							((ViewGroup)view.getParent()).removeView(view);
						}
						rootView.addView(view, 0);
						break;
					case IWebviewStateListener.ON_PAGE_STARTED:
						// 首页面开始加载事件
//						pd = ProgressDialog.show(activity, "加载中", "0/100");
						break;
					case IWebviewStateListener.ON_PROGRESS_CHANGED:
						// WebApp首页面加载进度变化事件
						if (pd != null) {
							pd.setMessage(pArgs + "/100");
						}
						break;
					case IWebviewStateListener.ON_PAGE_FINISHED:
						// WebApp首页面加载完成事件
//					iWebview.evalJS("nativeJump('"+name+"')");
						vueLaunchFinish = true;
						L.d("webAppListener."+name, "ON_PAGE_FINISHED: ");
						if (pd != null) {
							pd.dismiss();
							pd = null;
						}
						// 页面加载完毕，设置显示webview
						// 如果不需要显示spalsh页面将此行代码移动至onCloseSplash事件内
						app.obtainWebAppRootView().obtainMainView().setVisibility(View.VISIBLE);
						break;
				}
				return null;
			}
		}, this);

		app.setIAppStatusListener(new IAppStatusListener() {
			// 设置APP运行事件监听
			@Override
			public boolean onStop() {
				// 应用运行停止时调用
				rootView.removeView(app.obtainWebAppRootView().obtainMainView());
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onStart() {
				// 独立应用启动时触发事件
			}

			@Override
			public void onPause(IApp arg0, IApp arg1) {
				// WebApp暂停运行时触发事件
			}

			@Override
			public String onStoped(boolean arg0, String arg1) {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	@Override
	public void onCoreReady(ICore coreHandler) {
		// 初始化5+ SDK，
		// 5+SDK的其他接口需要在SDK初始化后才能調用
		SDK.initSDK(coreHandler);
		// 设置当前应用可使用的5+ API
		SDK.requestAllFeature();
		//调用nativeObj.bitmap时  load之前 需要 onCoreReady中进行 imageloader初始化
		ImageLoaderUtil.initImageLoaderL(activity);
	}

	@Override
	public boolean onCoreStop() {
		// 当返回false时候回关闭activity
		return false;
	}

	// 在Widget集成时如果不需要显示splash页面可按照如下步骤操作
	// 1 删除onCreateSplash方法内的代码
	// 2 将5+mainView添加到rootview时将页面设置为不可见
	// 3 在onCloseSplash方法中将5+mainView设置为可见
	// 4 修改androidmanifest.xml文件 将SDK_WebApp的主题设置为透明
	// 注意！
	// 如果不显示splash页面会造成用户点击后页面短时间内会没有变化，
	// 可能会给用户造成程序没响应的错觉，
	// 所以开发者需要对5+内核启动到5+应用页面显示之间的这段事件进行处理

	@Override
	public Object onCreateSplash(Context pContextWrapper) {
		splashView = new FrameLayout(activity);
		splashView.setBackgroundResource(R.drawable.splash);
		rootView.addView(splashView);
		return null;
	}

	@Override
	public void onCloseSplash() {
		rootView.removeView(splashView);
	}
}
