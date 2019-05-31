package com.juejinchain.android.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 图片SD_Card本地缓存类
 */
public class LocalCacheUtils {

    private static final String TAG = LocalCacheUtils.class.getClass().getName();

    private final int FREE_SD_SPACE_NEEDED_TO_CACHE = 200 * 1024 * 1000; //200MB
    private final int mTimeDiff = 7 * 24 * 60 * 60 * 1000; //7天，过期时间

    private final int maxFiles = 20; //最多缓存图片张数

    private static final String LOCAL_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/JueJinBao_cache";

    // 写本地缓存
    public static void setLocalCache(String url, Bitmap bitmap) {
        //先清理下过期或过多的
        new LocalCacheUtils().removeCache(LOCAL_CACHE_PATH);

        File dir = new File(LOCAL_CACHE_PATH);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();  // 创建文件夹
        }
        try {
            String fileName = Md5Util.md5(url);
//            String fileName = url;
            File cacheFile = new File(dir, fileName);

            boolean bl = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(cacheFile));
//            Log.d(TAG, "图片保存"+ (bl?"成功":"失败") + "="+url);
            // 参1:图片格式;参2:压缩比例0-100; 参3:输出流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 读本地缓存
    public static Bitmap getLocalCache(String url) {
        try {
            //MD5Encoder.encode(url)
            File cacheFile = new File(LOCAL_CACHE_PATH, Md5Util.md5(url));

            if (cacheFile.exists()) {
//                Log.d(TAG, "getLocalCache: 图片存在="+url);
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(cacheFile));
                return bitmap;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 计算sdcard上的剩余空间
     * @return
     */
    private int freeSpaceOnSd() {
        double MB = 1024 * 1000d;
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory() .getPath());
        double sdFreeMB = ((double)stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
        return (int) sdFreeMB;
    }

    private void saveBmpToSd(Bitmap bm, String url) {
        if (bm == null) {
            Log.w(TAG, " trying to savenull bitmap");
            return;
        }
        //判断sdcard上的空间
        if (FREE_SD_SPACE_NEEDED_TO_CACHE >freeSpaceOnSd()) {
            Log.w(TAG, "Low free space onsd, do not cache");
            return;
        }
//        String filename =convertUrlToFileName(url);
//        String dir = getDirectory(filename);
//        File file = new File(dir +"/" + filename);
//        try {
//            file.createNewFile();
//            OutputStream outStream = newFileOutputStream(file);
//            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
//            outStream.flush();
//            outStream.close();
//            Log.i(TAG, "Image saved tosd");
//        } catch (FileNotFoundException e) {
//            Log.w(TAG,"FileNotFoundException");
//        } catch (IOException e) {
//            Log.w(TAG,"IOException");
//        }
    }

    /**
     *计算存储目录下的文件大小，当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定
     * 那么删除60%最近没有被使用的文件
     * @param dirPath
     */
    private void removeCache(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
//        int dirSize = 0;
//        for (int i = 0; i < files.length;i++) {
//            if(files[i].getName().contains(WHOLESALE_CONV)) {
//                dirSize += files[i].length();
//            }
//        }
        //dirSize > CACHE_SIZE * MB ||
//        if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
        if (files.length > maxFiles){
            int removeFactor = (int) ((0.6 *files.length) + 1);

            Arrays.sort(files, new FileLastModifSort());

            Log.i(TAG, "Clear some expiredcache files ");

            for (int i = 0; i <removeFactor; i++) {
//                if(files[i].getName().contains(WHOLESALE_CONV)) {
                    files[i].delete();
//                }
            }

        }
    }

    /**
     * 删除过期文件
     * @param dirPath
     * @param filename
     */
    private void removeExpiredCache(String dirPath, String filename) {

        File file = new File(dirPath,filename);

        if (System.currentTimeMillis() -file.lastModified() > mTimeDiff) {

            Log.i(TAG, "Clear some expiredcache files ");

            file.delete();

        }

    }

    /**
     * TODO 根据文件的最后修改时间进行排序 *
     */
    class FileLastModifSort implements Comparator<File> {
        public int compare(File arg0, File arg1) {
            if (arg0.lastModified() >arg1.lastModified()) {
                return 1;
            } else if (arg0.lastModified() ==arg1.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }
    }

}
