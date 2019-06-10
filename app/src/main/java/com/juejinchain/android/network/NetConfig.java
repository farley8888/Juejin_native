package com.juejinchain.android.network;

import android.util.Log;

import com.juejinchain.android.model.UserModel;
import com.juejinchain.android.tools.L;

import java.util.Map;

public class NetConfig {


    public static final String BaseUrl = "http://dev.api.juejinchain.cn";
    /*
     * 正式环境
     *  发正式包要修改的地方有
     *  1、L.ISDebug
     *  2、android:debuggable="false"
     */
//    public static final String BaseUrl = "http://api.juejinchain.com";

    //共享base href 通过接口获取
    public static String SHARE_BASE_HREF ;

    //请求接口响应码
    public static final int NR_CODE_SUCCESS  = 0;             //请求成功
    public static final int NR_CODE_LOGIN_TIMEOUT = 706;      //登录超时
    public static final int NR_CODE_LOGIN_OTHER  = 705;       //在其它设备登录

    //设置请求超时s
    public static final int REQUEST_TIME_OUT  = 30;
    //设备类型Android=7，iOS=6
    public static final int DEVICE_TYPE  = 7;
    //请求参数key
    public static final String PK_UserToken = "user_token";

    /**
     * 渠道号
     */
    public static final String CHANNEL = "baidu";

    //接口前缀
    public static final String API_Prefix = "v1/";

    /**
     * 开屏广告统计
     * ad_id=12 广告id
     * type=1 //1代表点击 0代表浏览
     * uuid //设备id
     */
    public static final String API_ADCount = "user/ad_count";

    /**
     * 开屏广告使用
     * ad_platform_type =1 代表自己平台  0代表其他
     */
    public static final String API_OpenAd_Start = "user/ad_start_alert";

    //首页tab显示控制
    public static final String API_SYSTEM_MENU  = "system/menu";
    //获取分享href域名
    public static final String API_SYSTEM_HREF  = "system/get_domain";

    //获取大礼包随机车型数据
    public static final String API_GiftImage    = "gift_image";
    //领取大礼包 点击对话框的领取按钮 调用
    public static final String API_GiftBag      = "task/gift_bag";
    //首页半小时
    public static final String API_Times30      = "task/minute30";
    //首页半小时 已保存
    public static final String API_Times30_Save = "task/minute30_save";
    /**
     * 提示签到接口，非新用户登录后回到首页时调用
     * 如未签到底部弹签到提示popup
     * "data": {
     *     "unsigned": 1,
     */
    public static final String API_MessageHint  = "message/hint";
    /**
     * 未读信息，登录后每20~30秒调用一次（全局的
     * 如果有top弹下滚提示3s后消失，点击跳到消息页面
     */
    public static final String API_UnreadMsg    = "message/unread";
    /**
     * 获取随机分享内容
     */
    public static final String API_ShareCopy    = "share/copywriting";
    /**
     * 首页返回奖励Dialog接口
     */
    public static final String API_ShareTop     = "share/top_car";


    //获取频道，默认调用此接口，没登录返回8个，登录后重新获取
    public static final String API_ChannelGet       = "channel/user";
    //频道调序 channel_id "1,2,3"
    public static final String API_ChannelSet       = "channel/set";
    //频道管理的，推荐频道
    public static final String API_ChannelRecommend = "channel/recommend";

    //新闻推荐
    public static final String API_NewsPull     = "article/pull_up";
    //新闻非推荐,频道ID：channel_id
    public static final String API_NewsOther    = "article/pagelist";
    //搜索新闻 kw=王思聪&page=1&source_style=6
    public static final String API_NewsSearch   = "article/search";
    /**
     * 读文章，后30s请求
     * aid=132323, type: arc，成功提示一下
     */
    public static final String API_NewsReading  = "article/reading_article";

    //视频内容列表；category=video,subv_movie
    public static final String API_VideoList        = "video/lists";
    //视频类型列表
    public static final String API_VideoTypeList    = "video/category_lists";
    //视频详情 vid=424406
    public static final String API_VideoDetail      = "video/detail";
    //视频推荐列表
    public static final String API_VideoRecommend   = "video/recommend";
    //视频评论列表
    public static final String API_VideoCommentList = "video/comment_list";
    /**
     * 评论视频
     * vid:598401, content:谢谢！
     */
    public static final String API_VideoComment     = "video/comment";
    //评论回复列表cid=17129270
    public static final String API_VideoReplyList   = "video/reply_list";
    /**
     * 回复视频评论
     * aid: 477536 文章id
     * 或
     * vid: 477536 视频id
     * cid: 17130234 评估id
     * content: ewr
     */
    public static final String API_VideoReply       = "video/reply";
    /**
     * 视频点赞和评论点赞
     * vid: 4168444
     * 评论的加一个参，用post
     * cid: 1688984
     */
    public static final String API_VideoFabulous    = "video/fabulous";


    public static String getUrlByParams(Map<String, String> param, String api) {
        String getUrl = BaseUrl + "/" + API_Prefix + api + "?";
        if (param != null){
            for (String key : param.keySet()) {
                getUrl += key + "=" + param.get(key);
                getUrl += "&";
            }
        }

        getUrl += getCommonParams();
        Log.d("NetConfig", "getUrlByParams: "+getUrl);
        return getUrl;
    }

    public static String getUrlByAPI(String api) {
        String getUrl = BaseUrl + "/" + API_Prefix + api +"?" + getCommonParams();
        Log.d("NetConfig", "getUrlByAPI: "+getUrl);
        return getUrl;
    }

    //公共参数
    private static String getCommonParams(){
        String commParam = "source_style=" + DEVICE_TYPE;
        String ut = UserModel.getUserToken();
        L.d("NetConfig", "getCommonParams: ut="+ut);
        if (ut != null){
            commParam += "&"+PK_UserToken+"="+ut;
        }
        return commParam;
    }

}