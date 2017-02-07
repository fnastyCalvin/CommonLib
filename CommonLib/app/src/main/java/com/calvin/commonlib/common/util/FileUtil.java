package com.calvin.commonlib.common.util;

import android.content.Context;

import java.io.File;

/**
 * Created by jiangtao on 2016/4/7 16:01.
 */
public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();
    private FileUtil() {
        //no instance
    }

    /**
     * 删除应用数据： cache, file, share prefs, databases
     * @param context
     */
    public static void clearAllCache(Context context) {
        clearCache(context);
        clearFiles(context);
        clearSharedPreference(context);
        clearDatabase(context);
    }

    /**
     * 删除应用缓存目录
     * @param context
     */
    public static void clearCache(Context context) {
        deleteFile(context.getCacheDir(), true);
        deleteFile(context.getExternalCacheDir(), true);
    }

    /**
     * 删除应用文件目录
     * @param context
     */
    public static void clearFiles(Context context) {
        deleteFile(context.getFilesDir(), true);
    }

    /**
     * 删除应用Shared Prefrence目录
     * @param context
     */
    public static void clearSharedPreference(Context context) {
        deleteFile(new File("/data/data/" + context.getPackageName() + "/shared_prefs"), true);
    }

    /**
     * 删除应用数据库目录
     * @param context
     */
    public static void clearDatabase(Context context) {
        deleteFile(new File("/data/data/" + context.getPackageName() + "/databases"), true);
    }

    /**
     * 删除文件或者文件夹，默认保留根目录
     * @param directory
     */
    public static void deleteDir(File directory) {
        deleteFile(directory, false);
    }

    /**
     * 删除文件或者文件夹
     * @param directory
     */
    public static void deleteFile(File directory, boolean keepRoot) {
        if (directory != null && directory.exists()) {
            if (directory.isDirectory()) {
                for (File subDirectory : directory.listFiles()) {
                    deleteFile(subDirectory, false);
                }
            }

            if (!keepRoot) {
                directory.delete();
            }
        }
    }
}
