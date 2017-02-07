package com.calvin.commonlib;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.calvin.commonlib.common.util.AppUtil;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.analytics.MobclickAgent;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by calvin on 2016/7/7 15:14.
 */
public class App extends Application {

    private static Context mContext;
    private static int mScreenWidth; //
    private static int mScreenHeight; //
    private static float mDensity; //

    private static App mApp;

//    private static User user;


    @Override
    public void onCreate() {
        initStrictMode();
        super.onCreate();
        mContext = getApplicationContext();
        mApp = this;
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return AppUtil.getProcessName();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        initApp(s);
                    }
                });
//        LoadingAndRetryManager.BASE_RETRY_LAYOUT_ID = R.layout.base_retry;
//        LoadingAndRetryManager.BASE_LOADING_LAYOUT_ID = R.layout.base_loading;
//        LoadingAndRetryManager.BASE_EMPTY_LAYOUT_ID = R.layout.base_empty;
    }

    private void initApp(String processName) {
        if (!TextUtils.isEmpty(processName) && getPackageName().equals(processName)) {
            RefWatcher refWatcher = LeakCanary.install(this);
            MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(getApplicationContext(), "5524912efd98c5de7f0011b4", "snail");
            MobclickAgent.startWithConfigure(config);

            if (!BuildConfig.DEBUG) {
//            BlockCanary.install(this, new BlockCanaryContext()).start();
//            MobclickAgent.setDebugMode(true);
//            MobclickAgent.setCatchUncaughtExceptions(false);
//                initTalkingData();
            }

//        initXiaoMiPush();

            initStetho();
//        initCookie();

            CookieSyncManager.createInstance(this);
            CookieManager.getInstance().setAcceptCookie(true);
            CookieManager.getInstance().removeAllCookie();

            DisplayMetrics dm = new DisplayMetrics();
            WindowManager manager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
            manager.getDefaultDisplay().getMetrics(dm);
            mScreenWidth = dm.widthPixels;
            mScreenHeight = dm.heightPixels;
//            mDensityDpi = dm.densityDpi;
            mDensity = dm.density;
            Log.d("screentag", dm.widthPixels + ":" + dm.heightPixels + "," + dm.densityDpi + ":" + dm.density);
        }
    }

   /* private void initTalkingData() {
        TCAgent.LOG_ON = true;
        // App ID: 在TalkingData创建应用后，进入数据报表页中，在“系统设置”-“编辑应用”页面里查看App ID。
        // 渠道 ID: 是渠道标识符，可通过不同渠道单独追踪数据。
        TCAgent.init(this, "C96B16F9CF17EFB1EC68267A96A2BB9B", "snail");
        TCAgent.setReportUncaughtExceptions(true);
    }
*/
   /* private void initXiaoMiPush() {
        Schedulers.io().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                //初始化push推送服务
                if (shouldInit()) {
                    MiPushClient.registerPush(getContext(), APP_ID, APP_KEY);
                }
                //打开Log
                LoggerInterface newLogger = new LoggerInterface() {

                    @Override
                    public void setTag(String tag) {
                        // ignore
                    }

                    @Override
                    public void log(String content, Throwable t) {
                        Log.d(TAG, content, t);
                    }

                    @Override
                    public void log(String content) {
                        Log.d(TAG, content);
                    }
                };
                Logger.setLogger(getContext(), newLogger);
                //end
            }
        },500, TimeUnit.MILLISECONDS);

    }*/

    private void initStetho() {
        if (BuildConfig.DEBUG) {
            Schedulers.io().createWorker().schedule(new Action0() {
                @Override
                public void call() {
                    Stetho.initializeWithDefaults(getAppContext());
                }
            }, 500, TimeUnit.MILLISECONDS);
        }
    }

    private void initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }
    }

    public static App getInstance() {
        return mApp;
    }

    public static Context getAppContext() {
        return mContext.getApplicationContext();
    }

   /* private void initCookie() {
        CookieSyncManager.createInstance(this);
        if(SecurePreferences.getInstance().getBoolean(Constants.IS_LOGIN)) {
            RetrofitUtils.setCookies(CookieUtils.getCookies(this));
        }
    }*/

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public int getScreenWidth() {
        return mScreenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    /**
     * dip 转为 px
     *
     * @param dipValue
     * @return
     */
    public int dip2px(float dipValue) {
        return (int) (dipValue * mDensity + 0.5f);
    }


    /**
     * 退出APP
     */
    public void exitApp() {
        MobclickAgent.onKillProcess(this);
//        AppManager.getAppManager().AppExit(this);
//        System.exit(0);
//        android.os.Process.killProcess(Process.myPid());
    }

  /*  public static void setUser(User u) {
        if (u != null) {
            if (user == null) user = new User();
            user = u;
            user.isLogin = true;
        } else {
            //test
            user = null;
        }
    }*/

    /*public static  void clearUserData(){
        SecurePreferences preferences = SecurePreferences.getInstance();
        preferences.put(Constants.IS_LOGIN, false);
        preferences.put(Constants.AUTO_LOGIN, false);
        preferences.put(Constants.USER_PSW, "");
    }*/

   /* public static void clearUser() {
        user = new User();
    }

    public static User getUser() {
        if (user == null || !user.isLogin) {
            user = new User();
            SecurePreferences preferences = SecurePreferences.getInstance();
            boolean isAutoLogin = preferences.getBoolean(Constants.AUTO_LOGIN);
            boolean isLogin = preferences.getBoolean(Constants.IS_LOGIN);
            Gson gson = new Gson();
            if (isAutoLogin && isLogin) {
                user.isLogin = true;
                user = gson.fromJson(preferences.getString(Constants.USER_INFO), User.class);
            }
        }
        return user;
    }
*/

}
