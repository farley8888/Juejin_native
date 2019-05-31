package razerdp.basepopup;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by 大灯泡 on 2019/5/13
 * <p>
 * Description：一个BasePopup的provider注入木马嘿嘿~用来初始化依赖的插件
 */
public final class BasePopupRuntimeTrojanProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        BasePopupSupporterManager.getInstance().init(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
