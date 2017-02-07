package com.calvin.commonlib.common.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

/**
 * 监听APP是否在后台运行
 * <p>usage: this.registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());</p>
 * Created by jiangtao on 2015/5/29.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks{

    private static final String TAG = "ActivityLifecycle";

    /**在前台的Activity数量*/
    private int foregroundActivities;
    private boolean hasSeenFirstActivity;
    private boolean isChangingConfiguration;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        foregroundActivities++;
        if (hasSeenFirstActivity && foregroundActivities == 1 && !isChangingConfiguration) {
            //applicationDidEnterForeground(activity);
            Log.d(TAG,"....in Foreground....");
        }
        hasSeenFirstActivity = true;
        isChangingConfiguration = false;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        foregroundActivities--;
        if (foregroundActivities == 0) {
            //applicationDidEnterBackground(activity);
            Log.d(TAG,"....in Background....");
        }
        isChangingConfiguration = activity.isChangingConfigurations();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
