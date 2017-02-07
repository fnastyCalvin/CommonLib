package com.calvin.commonlib.common.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by jiangtao on 2016/4/1 14:35.
 */
public class StatusBarUtil {
    private StatusBarUtil(){}

    public static final String TAG = StatusBarUtil.class.getName();

    private static void setStatusBarColor(@NonNull Activity activity, int color, boolean isFullScreen){
        Window window = activity.getWindow();
        final ViewGroup contentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        final ViewGroup childView = (ViewGroup) (contentView).getChildAt(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){//5.0
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(color));
//            if(childView != null) {
//                ViewCompat.setFitsSystemWindows(childView, true);
//            }
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) //4.4.4
        {
            int statusBarHeight = getStatusBarHeight(activity);
            View statusBarView = new View(activity);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,statusBarHeight);
            statusBarView.setBackgroundColor(color);
            contentView.addView(statusBarView, lp);
        }
        if(childView != null) {
            if (!isFullScreen) {
                ViewCompat.setFitsSystemWindows(childView, true);
            }
            else {
                ViewCompat.setFitsSystemWindows(childView, false);
            }
            childView.setClipToPadding(false);
        }
    }

    public static void setStatusBarColor(@NonNull Activity activity, int color) {
        setStatusBarColor(activity,color,false);
    }

    public static void setStatusBarColorFullScreen(@NonNull Activity activity, int color) {
        setStatusBarColor(activity,color,true);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            result = context.getResources().getDimensionPixelOffset(resId);
        }
        return result;
    }
}
