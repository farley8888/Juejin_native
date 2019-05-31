package com.juejinchain.android.ddshare;

import android.util.Log;

import com.android.dingtalk.share.ddsharemodule.IDDShareApi;
import com.android.dingtalk.share.ddsharemodule.message.DDImageMessage;
import com.android.dingtalk.share.ddsharemodule.message.DDMediaMessage;
import com.android.dingtalk.share.ddsharemodule.message.DDWebpageMessage;
import com.android.dingtalk.share.ddsharemodule.message.SendMessageToDD;

import org.json.JSONObject;

/**
 * 钉钉分享UTIL
 */
public class DDShareUtil {

    private IDDShareApi iddShareApi ;

    public DDShareUtil(IDDShareApi api) {
        this.iddShareApi = api;

    }

    /**
     * 分享网页消息
     */
    public void sendWebPageMessage(boolean isSendDing, JSONObject json) {
        //初始化一个DDWebpageMessage并填充网页链接地址
        DDWebpageMessage webPageObject = new DDWebpageMessage();
        webPageObject.mUrl = "http://www.sina.com";
//        webPageObject.mUrl = json.optString("url");

        //构造一个DDMediaMessage对象
        DDMediaMessage webMessage = new DDMediaMessage();
        webMessage.mMediaObject = webPageObject;

        //填充网页分享必需参数，开发者需按照自己的数据进行填充
        webMessage.mTitle = json.optString("title");
        webMessage.mContent = json.optString("content");
//        webMessage.mThumbUrl = json.optString("thumb");
        webMessage.mThumbUrl = "http://static.dingtalk.com/media/lAHPBY0V4shLSVDMlszw_240_150.gif";

        // 网页分享的缩略图也可以使用bitmap形式传输
//         webMessage.setThumbImage(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        //构造一个Req
        SendMessageToDD.Req webReq = new SendMessageToDD.Req();
        webReq.mMediaMessage = webMessage;
//        webReq.transaction = buildTransaction("webpage");

        //调用api接口发送消息到支付宝
        if(isSendDing){
            iddShareApi.sendReqToDing(webReq);
        } else {

            iddShareApi.sendReq(webReq);
        }
    }

    /**
     * 通过图片链接方式分享图片消息
     */
    private void sendOnlineImage(boolean isSendDing) {
        //图片Url，开发者需要根据自身数据替换该数据
//        String picUrl = "http://upfile.asqql.com/2009pasdfasdfic2009s305985-ts/2017-12/201712617475697622.gif";
        String picUrl = "https://img-ic_car_and_money.pchome.net/ic_car_and_money/1k1/ut/5a/ouzdgm-1dzc.jpg";
//        String picUrl = "http://static.dingtalk.com/media/lAHPBY0V4shLSVDMlszw_240_150.gif";

        //初始化一个DDImageMessage
        DDImageMessage imageObject = new DDImageMessage();
        imageObject.mImageUrl = picUrl;

        //构造一个mMediaObject对象
        DDMediaMessage mediaMessage = new DDMediaMessage();
        mediaMessage.mMediaObject = imageObject;

        //构造一个Req
        SendMessageToDD.Req req = new SendMessageToDD.Req();
        req.mMediaMessage = mediaMessage;
//        req.transaction = buildTransaction("image");

        //调用api接口发送消息到支付宝
        if(isSendDing){
            iddShareApi.sendReqToDing(req);
        } else {

            iddShareApi.sendReq(req);
        }
    }


}
