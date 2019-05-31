package com.juejinchain.android.hook;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.juejinchain.android.network.SpUtils;

import java.util.UUID;

public class DeviceUtil {

    private static final String PREF_KEY_UUID = "myKeyUUID";

    /**
     * 将下面的获取方法封装到一个方法中
     * @param ct
     * @return
     */
    public static String getUUID(Context ct) {
        String uuid = getDeviceUUid(ct);
        if (TextUtils.isEmpty(uuid)) {
            uuid = getAppUUid(ct);
        }
//        Log.d("DeviceUtil", "getUUID: "+uuid);
        return uuid;
    }

    /**
     * 获取ANDROID_ID
     * @param ct
     * @return
     */
    private static String getAndroidID(Context ct) {
        String id = Settings.Secure.getString(ct.getContentResolver(), Settings.Secure.ANDROID_ID);
//        Log.d("DeviceUtil", "getAndroidID: "+id);
        return id == null ? "" : id;
    }

    /**
     * 构造UUID，防止直接暴露ANDROID_ID
     * @param ct
     * @return
     */
    private static String getDeviceUUid(Context ct)
    {
        String androidId = getAndroidID(ct);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)androidId.hashCode() << 24)); //32
        return deviceUuid.toString();
    }

    /**
     * 考虑极端情况，我们自己生成一个应用级别的UUID
     * 这种情况我们需要将生成的UUID保存到SharedPreference中，只要应用不被卸载或者清除数据，这个值就不会变。
     * @param ct
     * @return
     */
    private static String getAppUUid(Context ct) {
        String uuid = (String) SpUtils.get(ct , PREF_KEY_UUID, "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            //这里需要保存到SharedPreference中
            SpUtils.put(ct, PREF_KEY_UUID, uuid);
        }
        return uuid;
    }

}
