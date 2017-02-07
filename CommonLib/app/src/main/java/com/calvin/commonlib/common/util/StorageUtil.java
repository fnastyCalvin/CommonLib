package com.calvin.commonlib.common.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.CheckResult;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangtao on 2016/2/14 16:24.
 */
public class StorageUtil {

    private StorageUtil() {
    }

    /**
     * 内部存储
     * 1. 如果没有内部存储,则为外部存储
     * 2. 一般默认说的sdcard就是指这一层
     */
    public static class Sdcard {
        public static boolean hasSdCard() {
            String externalStorageState;
            try {
                externalStorageState = Environment.getExternalStorageState();
            } catch (Exception e) {
                externalStorageState = "";
            }
            return Environment.MEDIA_MOUNTED.equals(externalStorageState) || !Environment.isExternalStorageRemovable();
        }

        @CheckResult
        public static File path() {
            if(hasSdCard()){
                return Environment.getExternalStorageDirectory();
            }
            return null;
        }
    }

    /**
     * 外部存储
     * 第2张SD卡
     */
    public static class ExtSdcard {
        public static File path() {
            List<String> paths = new ArrayList<String>();
            String extFileStatus = Environment.getExternalStorageState();
            File extFile = Environment.getExternalStorageDirectory();
            if (extFileStatus.equals(Environment.MEDIA_MOUNTED)
                    && extFile.exists() && extFile.isDirectory()
                    && extFile.canWrite()) {
                paths.add(extFile.getAbsolutePath());
            }
            try {
                // obtain executed result of command line code of 'mount', to judge
                // whether tfCard exists by the result
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec("mount");
                InputStream is = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                int mountPathIndex = 1;
                while ((line = br.readLine()) != null) {
                    // format of sdcard file system: vfat/fuse
                    if ((!line.contains("fat") && !line.contains("fuse") && !line
                            .contains("storage"))
                            || line.contains("secure")
                            || line.contains("asec")
                            || line.contains("firmware")
                            || line.contains("shell")
                            || line.contains("obb")
                            || line.contains("legacy") || line.contains("data")) {
                        continue;
                    }
                    String[] parts = line.split(" ");
                    int length = parts.length;
                    if (mountPathIndex >= length) {
                        continue;
                    }
                    String mountPath = parts[mountPathIndex];
                    if (!mountPath.contains("/") || mountPath.contains("data")
                            || mountPath.contains("Data")) {
                        continue;
                    }
                    File mountRoot = new File(mountPath);
                    if (!mountRoot.exists() || !mountRoot.isDirectory()
                            || !mountRoot.canWrite()) {
                        continue;
                    }
                    boolean equalsToPrimarySD = mountPath.equals(extFile
                            .getAbsolutePath());
                    if (equalsToPrimarySD) {
                        continue;
                    }
                    paths.add(mountPath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (paths.size() > 1) {
                return new File(paths.get(1));
            }
            return null;
        }
    }

    /**
     * 优先返回位于SD卡中的缓存目录<br/>
     *  /mnt/sdcard/Android/data/com.xx.xxx/cache
     *
     * @param context
     * @return
     */
    public static String getCacheDirectory(Context context){
        String cachePath = null;
        if(Sdcard.hasSdCard() && context.getExternalCacheDir() != null){
            cachePath = context.getExternalCacheDir().getPath();
        }

        if(TextUtils.isEmpty(cachePath)){
            cachePath = context.getCacheDir().getPath();
        }

        if(TextUtils.isEmpty(cachePath)){
            cachePath = context.getFilesDir()+"/data/data/" + context.getPackageName() + "/cache/";
            Log.w("StorageUtil","Can't define system cache directory!"+cachePath+" will be used.");
        }
        File cacheDir = new File(cachePath);
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        return cachePath;
    }


    private static File getExternalDir(Context context, String customDirName) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File customDir = new File(new File(dataDir, context.getPackageName()), customDirName);
        if (!customDir.exists()) {
            if (!customDir.mkdirs()) {
                return null;
            }
            try {
                new File(customDir, ".nomedia").createNewFile();
            } catch (IOException e) {
            }
        }
        return customDir;
    }

    /**
     * create a new Public Directory on SD card
     * @param dirName
     * @return
     */
    public static String setExternalPublicDir(String dirName) {
        File dir = Environment.getExternalStoragePublicDirectory(dirName);
        if (dir == null) {
            throw new IllegalStateException("Failed to get external storage public directory");
        } else if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new IllegalStateException(dir.getAbsolutePath() +" already exists and is not a directory");
            }
        } else {
            if (!dir.mkdirs()) {
                throw new IllegalStateException("Unable to create directory: "+dir.getAbsolutePath());
            }
        }
        return dir.getAbsolutePath();
    }

    public static String totalString(Context context) {
        return Formatter.formatFileSize(context, total(Sdcard.path()));
    }
    public static String usedString(Context context) {
        return Formatter.formatFileSize(context, used(Sdcard.path()));
    }
    public static String freeString(Context context) {
        return Formatter.formatFileSize(context, free(Sdcard.path()));
    }

    private static long total(File path) {
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }

    private static long free(File path) {
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long freeBlocks = stat.getAvailableBlocks();
        return blockSize * freeBlocks;
    }

    private static long used(File path) {
        return total(path) - free(path);
    }
}
