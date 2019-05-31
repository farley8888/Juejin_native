package com.juejinchain.android.hook;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.juejinchain.android.IndexActivity;
import com.juejinchain.android.R;
import com.juejinchain.android.network.LocalCacheUtils;
import com.juejinchain.android.network.NetConfig;
import com.juejinchain.android.network.OkHttpUtils;
import com.juejinchain.android.network.SpUtils;
import com.juejinchain.android.network.callBack.BitmapCallback;
import com.juejinchain.android.network.callBack.JSONCallback;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
//import pl.droidsonroids.gif.GifImageView;  有问题


/**
 * 开屏广告activity
 * AppCompatActivity
 */
public class ADOpenActivity extends Activity {

    //上次启动开屏广告时间，
    public static final String LAST_OPEN_TIME = "lastOpenTime";
    /*
     * 多久启动一次开屏广告，至少10秒，否则第一次打开会显示两次
     * （1分钟=1000*60*1）
     * 测试时记得改为12秒=1000*6*2
     */
    public static final long INTERVAL_TIME = 1000*60*10;

    //启动APP的启动次数
    public static final String OPEN_TIMES_OF_LAUNCH = "OpenTimesOfLaunch";
    //启动APP时限定时间内最多启动次数
    public static final long LAUNCH_LIMIT_OF_LAUNCH = 3;

    //下次是否打开广点通
    private static final String NextOpenGDT = "openPlatform";

    private Button mButton;
    private ImageView mImage;
//    private com.ant.liao.GifView mGif; //网络的无法显示

    private static final String TAG = IndexActivity.class.getName();
    private RelativeLayout splash;
    private boolean canJump = false;

    private JSONObject adJsonInfo;

    ////广点通开屏广告变量 start
//    private String GDTAppID = "1101152570";         //测试的
//    private String OpenPosID = "8863364436303842593";
    private String GDTAppID = "1108823726";         //媒体ID
    private String OpenPosID = "3000053978942747";  //开屏广告位ID
    private GDTOpenADListener gdtOpenADListener;
    //广点通在模拟器上不显示，通过接口控制
    private boolean openAD_UseGDT = true;          //是否默认使用广点通
    private SplashAD splashAD;
    private ViewGroup container;
    ////广点通 变量 end

    //加载超时时间2~3秒为适,超时直接进入主页
    private final int LOAD_TIME = 3000;
    final int TimeoutWhat = 408;  //超时what
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == TimeoutWhat){
//                Log.d(TAG, "handleMessageFinish: ");
                finish();
            }
        }
    };

    private CountDownTimer mCountDownTimer = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mButton.setText("跳过 " + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            finish();
//            Log.e("TAG", "ADOpenActivity finished");
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        openAD_UseGDT = (boolean) SpUtils.get(getApplicationContext(), NextOpenGDT, openAD_UseGDT);
        //全屏时，性能差的手机有点抖动，因为Vue的activity没有全屏，用@style/Theme也一样抖
//        if (openAD_UseGDT)
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
//        Log.d("TAG", "ADOpenActivity created success");
        setContentView(R.layout.welcome_layout);
        mButton = findViewById(R.id.button);
        ImageView img = findViewById(R.id.imageView);
        mImage = img;

//        mGif = findViewById(R.id.gifView);

        mButton.setVisibility(View.GONE);
        container = findViewById(R.id.splash_container);
        setListener();
//        displayGif("");

        if (openAD_UseGDT) {
            // 如果targetSDKVersion >= 23，就要申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23），那么只需要在这里直接调用fetchSplashAD接口。
            if (Build.VERSION.SDK_INT >= 23) {
                checkAndRequestPermission();
            } else {
                // 如果是Android6.0以下的机器，默认在安装时获得了所有权限，可以直接调用SDK
                fetchSplashAD(this, container, mButton, GDTAppID, OpenPosID, gdtOpenADListener, 0);
            }
            //加载下次要显示的广告平台
            loadOpenAdData(true);
        }else {

            //本平台广告
            loadOpenAdData(false);
        }

    }

    private void setListener(){

        if (openAD_UseGDT){ //如使用广点通开屏
            gdtOpenADListener = new GDTOpenADListener(this, mButton, mImage);
            findViewById(R.id.textView_guangGao).setVisibility(View.GONE);
            return;
        }

        findViewById(R.id.linearLayout).setVisibility(View.GONE); //隐藏底部栏

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(TAG, "onClick: 点击了图片");
                if (adJsonInfo != null) {
                    takeAd("1");
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
//                    Uri content_url = Uri.parse("http://www.cnblogs.com");
                    String url = adJsonInfo.getString("link");
                    if (url != null && url.trim().length() == 0){
                        url =  adJsonInfo.getString("download_android");
                    }
                    if (url == null || url.trim().length() == 0) return;
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    //有些手机不存在
//                    intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
                    startActivity(intent);
                    canJump = false;
                    mCountDownTimer.onFinish();
                    mCountDownTimer.cancel();
                }
            }
        });

    }


    /**
     * 加载自己的开屏广告数据
     * @param onlyGetPlatform 是否只获取下次打开的平台
     */
    private void loadOpenAdData(final boolean onlyGetPlatform){
        String getUrl = NetConfig.BaseUrl + "/" + NetConfig.API_Prefix + NetConfig.API_OpenAd_Start + "?";
        getUrl += "ad_position=1"; //http://dev.api.juejinchain.cn/v1/user/ad_start_alert?ad_position=1
//        Log.d(TAG, "loadOpenAdData: onlyGetPlatform="+onlyGetPlatform +", \n url = "+getUrl);

        OkHttpUtils.getAsyn(getUrl, new JSONCallback() {
            @Override
            public void onError(Call call, Exception e) {
//                Log.d(TAG, "onError: MyAD请求超时");
//                startVueWebView();
                if(!onlyGetPlatform) finish();
            }

            @Override
            public void onResponse(JSONObject response) {
//                Log.d(TAG, "onResponse: ="+response);
                if (response.getInteger("code") == 0){ //成功
                    try {
                        mHandler.removeMessages(TimeoutWhat);

                        adJsonInfo = response.getJSONObject("data").getJSONArray("ad_info").getJSONObject(0);
                        String imgurl = adJsonInfo.getJSONArray("images").getString(0);

                        //1为自平台的广告
                        boolean isGDT = response.getJSONObject("data").getInteger("ad_platform_type") != 1;
                        SpUtils.put(getApplicationContext(), NextOpenGDT, isGDT);

                        if (onlyGetPlatform){ //如果是获取下次要显示的平台，则不显示本平台的，直接返回
                            return;
                        }
                        if (imgurl.endsWith(".gif")){
                            displayGif(imgurl);
                        }else {
                            setImg(imgurl);
                        }
                        setShowType();
                    }catch (Exception ex){
                        Log.e(TAG, "onResponse: error="+ ex.getMessage());
                        if(!onlyGetPlatform) finish();

                    }
                }
            }
        });

//    以下方法无法取消OkHttpUtils.getAsyn()的加载！
// 所以用getInstance().getAsynAndConnectTimeout,这个没网时也无效！有网时一直超时！！
//        OkHttpUtils.getInstance().sendFailResultCallback(null, null, null);
//        OkHttpUtils.getInstance().getOkHttpClient().dispatcher().cancelAll();

        if (!onlyGetPlatform){
            mHandler.sendEmptyMessageDelayed(TimeoutWhat, LOAD_TIME);
        }

    }

    void setShowType(){
        /*
        * style_type
        广告样式{"11":"大图-上文下图","12":"大图-全图","13":"大图-上图下文",
        "16":"首页全屏-全图","17":"首页全屏-非全图",
        "21":"三图-上文下图","22":"三图-上图下文","26":"无感广告-无","31":"标准-左文右图","32":"标准-左图右文",
        "33":"标准-全图","36":"弹窗-默认模板","41":"视频-上文下图","42":"视频-全图","43":"视频-上图下文","46":"浮窗-默认模板",
        "51":"功能按钮-上图下文","52":"功能按钮-左图右文","53":"功能按钮-左文右图","56":"banner-大图","57":"banner-小图",
        "61":"瀑布流-上图下文","66":"搜索-默认模板","71":"icon-全图","72":"icon-左图右文"}
         */
        int showType = adJsonInfo.getInteger("style_type");
//        Log.d(TAG, "setShowType: "+showType);
        if (showType == 16){        //全屏
            RelativeLayout.LayoutParams imgParams = (RelativeLayout.LayoutParams) mImage.getLayoutParams();
            imgParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            findViewById(R.id.linearLayout).setVisibility(View.GONE);

        }else if (showType == 17){  //非全屏

            findViewById(R.id.linearLayout).setVisibility(View.VISIBLE);
            ImageView iv = findViewById(R.id.imageView2);
            TextView title = findViewById(R.id.textView2);
            TextView titleSub = findViewById(R.id.textView3);

//        OkHttpUtils.displayImage(iv, adJsonInfo.getString(""));
            title.setText(adJsonInfo.getString("title"));
            titleSub.setText(adJsonInfo.getString("sub_title"));
        }

    }

    private void setImg(final String url) throws IOException {
//        Log.d(TAG, "setImg: url= "+url);
        Bitmap cacheBm = LocalCacheUtils.getLocalCache(url);
        if (cacheBm != null){
            mImage.setImageBitmap(cacheBm);
            startTick();
            return;
        }

        OkHttpUtils.displayImage(url, new BitmapCallback() {
            @Override
            public void onError(Call call, Exception e) {
                finish();
            }

            @Override
            public void onResponse(Bitmap response) {
                mHandler.removeMessages(TimeoutWhat);
                LocalCacheUtils.setLocalCache(url, response);
                mImage.setImageBitmap(response);
                startTick();
            }
        });
        mHandler.sendEmptyMessageDelayed(TimeoutWhat, LOAD_TIME);

    }

    private void displayGif(String gifUrl){
//       gifUrl = "http://jjb-ad.oss-cn-hongkong.aliyuncs.com/jjb-ad-file/1/2019/04/28/MzbKZfTEDd.gif";
//        Log.d(TAG, "displayGif: "+gifUrl);

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE); //AUTOMATIC RESOURCE
        try {
            //
            Glide.with(this)
                    .load(new URL(gifUrl))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                            finish();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                            mHandler.removeMessages(TimeoutWhat);
                            startTick();
                            return false;
                        }
                    })
                    .apply(options)
                    .into(mImage);

            mHandler.sendEmptyMessageDelayed(TimeoutWhat, LOAD_TIME);

        } catch (MalformedURLException e) {
            finish();
            e.printStackTrace();
        }

    }

    void startTick(){
//        Log.d(TAG, "startTick: ");
        mButton.setVisibility(View.VISIBLE);
        mCountDownTimer.start();
        canJump = true;
        saveADOpenTime();
        takeAd("0");
    }

    /**
     * 统计广告查看或点击
     * @param type  1代表点击 0代表浏览
     */
    void takeAd(String type){
        Map<String, String> param = new HashMap<>();
        param.put("ad_id", adJsonInfo.getString("id"));
        param.put("type", type);
        param.put("uuid", DeviceUtil.getUUID(getBaseContext()));
        String getUrl = NetConfig.getUrlByParams(param, NetConfig.API_ADCount);
//        Log.d(TAG, "takeAd: url="+getUrl);

        OkHttpUtils.getAsyn(getUrl, new JSONCallback() {
            @Override
            public void onError(Call call, Exception e) {

            }

            @Override
            public void onResponse(JSONObject response) {
//                Log.d(TAG, "takeAD.onResponse: ="+response);
            }
        });

    }


    //记录开启开屏广告的时间
    public void saveADOpenTime(){
        SpUtils.put(getApplicationContext(), LAST_OPEN_TIME, System.currentTimeMillis());
        if (getIntent().getBooleanExtra("launch", false)){
            Integer launchTimes = 0;
            launchTimes = (int) SpUtils.get(getApplicationContext(), OPEN_TIMES_OF_LAUNCH, launchTimes);

            if (launchTimes >= LAUNCH_LIMIT_OF_LAUNCH) launchTimes = 1;
            else launchTimes += 1;
            SpUtils.put(getApplicationContext(), OPEN_TIMES_OF_LAUNCH, launchTimes);
        }
    }


    /**
     *
     * ----------非常重要----------
     *
     * Android6.0以上的权限适配简单示例：
     *
     * 如果targetSDKVersion >= 23，那么必须要申请到所需要的权限，再调用广点通SDK，否则广点通SDK不会工作。
     *
     * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
     * 注意：下面的`checkSelfPermission`和`requestPermissions`方法都是在Android6.0的SDK中增加的API，如果您的App还没有适配到Android6.0以上，则不需要调用这些方法，直接调用广点通SDK即可。
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            fetchSplashAD(this, container, mButton, GDTAppID, OpenPosID, gdtOpenADListener, 0);
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
            fetchSplashAD(this, container, mButton, GDTAppID, OpenPosID, gdtOpenADListener, 0);
        } else {
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }

    /**
     * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
     *
     * @param activity        展示广告的activity
     * @param adContainer     展示广告的大容器
     * @param skipContainer   自定义的跳过按钮：传入该view给SDK后，SDK会自动给它绑定点击跳过事件。SkipView的样式可以由开发者自由定制，其尺寸限制请参考activity_splash.xml或者接入文档中的说明。
     * @param appId           应用ID
     * @param posId           广告位ID
     * @param adListener      广告状态监听器
     * @param fetchDelay      拉取广告的超时时长：取值范围[3000, 5000]，设为0表示使用广点通SDK默认的超时时长。
     */
    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
                               String appId, String posId, SplashADListener adListener, int fetchDelay) {
        gdtOpenADListener.fetchSplashADTime = System.currentTimeMillis();
        splashAD = new SplashAD(activity, adContainer, skipContainer, appId, posId, adListener, fetchDelay);
//        initSplash();
    }

    @Override
    protected void onResume() {
        //判断是否该跳转到主页面
        super.onResume();
        Log.d(TAG, "mYonResum: " +canJump);
        if (canJump) {
//            startVueWebView();
        }
        canJump = false;
    }


    @Override
    protected void onPause() {
        super.onPause();
//        Log.d(TAG, "onPause: ");
        canJump = true;
    }

    /** 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
