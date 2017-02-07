package com.example.calvin.myapplication.common.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MyApplication extends Application {

//    private RequestQueue requestQueue;

    private static MyApplication mContext;

    public static  File cacheDirectory = null;

    private RefWatcher refWatcher;

//    public static User user;

    private static int mScreenWidth; //
    private static int mScreenHeight; //
    private static int mDensityDpi; //
    private static float mDensity; //

    private List<Activity> activityList = new LinkedList<Activity>();

    private static MyApplication instance;

    public static boolean isLogout;//注销账号

    public MyApplication() {
    }

    public static MyApplication getInstance() {
        if (instance == null)
            instance = new MyApplication();
        return instance;
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .delayBeforeLoading(0)
//                .showImageOnLoading(R.drawable.ic_launcher)
//                .showImageForEmptyUri(R.drawable.ic_launcher)
//                .showImageOnFail(R.drawable.ic_launcher)
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                        // .displayer(new RoundedBitmapDisplayer(20))
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).defaultDisplayImageOptions(options)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .threadPoolSize(4) // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().handleSlowNetwork(true);
    }

    public static MyApplication getAppContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());


//        user=new User();

        mContext = this;

        initImageLoader(this);

        //开启内存泄露检测
        refWatcher = LeakCanary.install(this);


//        MobclickAgent.setDebugMode(true);
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        mDensityDpi = dm.densityDpi;
        mDensity = dm.density;

        Log.d("screentag", mScreenWidth+":"+mScreenHeight+","+mDensityDpi+":"+mDensity);
    }



    /*public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;
    }
*/
  /*  public void saveUser(User user) {
        try {
            PreferencesUtils.putString(getAppContext(),"userId", user.userId);
            PreferencesUtils.putString(getAppContext(),"mobile", user.mobile);
            PreferencesUtils.putString(getAppContext(),"password", user.password);
            PreferencesUtils.putString(getAppContext(),"token", user.token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUserFromPref() {
        try {
            user.userId = PreferencesUtils.getString(getAppContext(),"userId");
            user.mobile = PreferencesUtils.getString(getAppContext(),"mobile","0");
            user.password = PreferencesUtils.getString(getAppContext(),"password");
            user.token = PreferencesUtils.getString(getAppContext(),"token","-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
//        if (user == null) {
//            user = getUserBySharedPreferences();
//        }
//        if (user == null) {
//            user = new User();
//        }
        return user;
    }

    public void clearUser() {
        try {
            SharedPreferences sf = getAppContext().getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sf.edit();
            editor.clear();
            editor.commit();
            user = new User();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
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

}
