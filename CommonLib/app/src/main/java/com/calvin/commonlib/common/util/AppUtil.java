package com.calvin.commonlib.common.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class AppUtil {
    private static final String TAG = AppUtil.class.getSimpleName();

    private AppUtil() {
        //no instance
    }
    
    /**
     * whether app is installed
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isInstalled(@NonNull Context context, @NonNull String packageName) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            return false;
        }
        try {
            List<PackageInfo> apps = packageManager.getInstalledPackages(0);
            for(PackageInfo app : apps){
                if(app.packageName.contains(packageName)){
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * whether packageName is system application
     */
    public static boolean isSystemApplication(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null || packageName == null || packageName.length() == 0) {
            return false;
        }
        try {
            ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
            return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断包名所对应的应用是否安装在SD卡上
     * @return  true if installation on SD card
     */
    public static boolean isInstallOnSDCard(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo appInfo;
        try {
            appInfo = pm.getApplicationInfo(packageName, 0);
            if ((appInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 启动应用
     */
    public static boolean startAppByPackageName(Context context, String packageName) {
        return startAppByPackageName(context, packageName, null);
    }

    /**
     * 启动应用
     */
    public static boolean startAppByPackageName(Context context, String packageName, Map<String, String> param) {
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                resolveIntent.setPackage(pi.packageName);
            }

            List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);

            ResolveInfo ri = apps.iterator().next();
            if (ri != null) {
                String packageName1 = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);

                ComponentName cn = new ComponentName(packageName1, className);

                intent.setComponent(cn);
                if (param != null) {
                    for (Map.Entry<String, String> en : param.entrySet()) {
                        intent.putExtra(en.getKey(), en.getValue());
                    }
                }
                context.startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context.getApplicationContext(), "启动失败", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    /**
     * <p>获取当前进程名，返回包名</p>
     * <p>类似于{@link #isMainProcess}</p>
     */
    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <p>是否是主线程</p>
     * <p>类似于{@link #getProcessName}</p>
     * @param context
     * @return
     */
    public boolean isMainProcess(Context context) {
        ActivityManager am = ((ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getApplicationContext().getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * need < uses-permission android:name =“android.permission.GET_TASKS” />
     * 判断是否前台运行
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName componentName = taskList.get(0).topActivity;
            if (componentName != null && componentName.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断Service是否在后台运行
     * @param context
     * @param servieName  eg:   com.example.MyService
     * @return
     */
    private boolean isServiceRunning(@NonNull Context context, @NonNull String servieName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (servieName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取设备相对唯一标示
     * @param context
     * @return
     */
    public static String getDeviceId(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String device_uuid = sharedPreferences.getString("device_uuid", null);
        if(TextUtils.isEmpty(device_uuid)){
            final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            try {
                //http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
                if (!"9774d56d682e549c".equals(androidId)) {
                    device_uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
                } else {
                    final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                    device_uuid = !TextUtils.isEmpty(deviceId) ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")).toString() : UUID.randomUUID().toString();
                    if (device_uuid.contains("-")){
                        device_uuid = device_uuid.replace("-","");
                    }
                }
                sharedPreferences.edit().putString("device_uuid",device_uuid).apply();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return device_uuid;
    }


    /**
     * 获取版本号
     * 如：1.0.0
     * @param context
     * @return
     */
    public static String getVersion(Context context)
    {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        String version = "";
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * 获取版本代号
     * 如：1232
     * @param context
     * @return
     */
    public static int getVersionCode(Context context)
    {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        int version = 0;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            version = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * 判断应用是否已经启动
     * @param context 一个context
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isAppRunning(@NonNull Context context, @NonNull String packageName){
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        if (processInfos == null || processInfos.isEmpty()) {
            return false;
        }
        for(int i = 0; i < processInfos.size(); i++){
            if(processInfos.get(i).processName.equals(packageName)){
                return true;
            }
        }
        return false;
    }
	
	/**
	 * Compares two version strings, using Semantic Versioning convention. See <a href="http://semver.org/">http://semver.org/</a>.
	 * 
	 * @param  v1 first version string.
	 * @param  v2 second version string.
	 * @return 0 if the versions are equal, 1 if version v1 is before version v2, -1 if version v1 is after version v2, -2 if version format is invalid.
	 */
	public static int compareVersions(String v1, String v2)
	{
		if(v1==null || v2==null || v1.trim().equals("") || v2.trim().equals("")) return -2;
		else if(v1.equals(v2)) return 0;
		else
		{
			boolean valid1 = v1.matches("\\d+\\.\\d+\\.\\d+");
			boolean valid2 = v2.matches("\\d+\\.\\d+\\.\\d+");
			
			if(valid1 && valid2)
			{
				int[] nums1;
				int[] nums2;
				
				try
				{
					nums1 = convertStringArrayToIntArray(v1.split("\\."));
					nums2 = convertStringArrayToIntArray(v2.split("\\."));
				}
				catch(NumberFormatException e)
				{
					return -2;
				}
				
				if(nums1.length==3 && nums2.length==3)
				{
					if(nums1[0]<nums2[0]) return 1;
					else if(nums1[0]>nums2[0]) return -1;
					else
					{
						if(nums1[1]<nums2[1]) return 1;
						else if(nums1[1]>nums2[1]) return -1;
						else
						{
							if(nums1[2]<nums2[2]) return 1;
							else if(nums1[2]>nums2[2]) return -1;
							else
							{
								return 0;
							}
						}
					}
				}
				else
				{
					return -2;
				}
			}
			else
			{
				return -2;
			}
		}
	}
	
	
	private static int[] convertStringArrayToIntArray(String[] stringArray) throws NumberFormatException
	{
		if(stringArray!=null)
		{
			int intArray[] = new int[stringArray.length];
			for(int i=0; i<stringArray.length; i++)
			{
				intArray[i] = Integer.parseInt(stringArray[i]);
			}
			return intArray;
		}
		return null;
	}

    /**
     * 打印安卓系统信息
     * @return
     */
    public static String printSystemInfo() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(date);
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------  系统信息  " + time + "  --------------------");
        sb.append("\nBOARD        :" + Build.BOARD);
        sb.append("\nDEVICE       :" + Build.DEVICE);
        sb.append("\nPRODUCT      :" + Build.PRODUCT);
        sb.append("\nMANUFACTURER :" + Build.MANUFACTURER);
        sb.append("\nCODENAME     :" + Build.VERSION.CODENAME);
        sb.append("\nRELEASE      :" + Build.VERSION.RELEASE);
        //sb.append("\nSDK          :" + android.os.Build.VERSION.SDK);
        return sb.toString();
    }

    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        Log.i(TAG, " IMEI：" + imei);
        return imei;
    }

    /**
     * 获取手机信息
     */
    public static String printTelephoneInfo(Context context) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(date);
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------  手机信息  " + time + "  --------------------");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = tm.getSubscriberId();
        //IMSI前面三位460是国家号码，其次的两位是运营商代号，00、02是中国移动，01是联通，03是电信。
        String providerName = null;
        if (IMSI != null) {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                providerName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                providerName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                providerName = "中国电信";
            }
        }
        sb.append(providerName + "  手机号：" + tm.getLine1Number() + " IMSI是：" + IMSI);
        sb.append("\nDeviceID(IMEI)       :" + tm.getDeviceId());
        sb.append("\nDeviceSoftwareVersion:" + tm.getDeviceSoftwareVersion());
        sb.append("\ngetLine1Number       :" + tm.getLine1Number());
        sb.append("\nNetworkCountryIso    :" + tm.getNetworkCountryIso());
        sb.append("\nNetworkOperator      :" + tm.getNetworkOperator());
        sb.append("\nNetworkOperatorName  :" + tm.getNetworkOperatorName());
        sb.append("\nNetworkType          :" + tm.getNetworkType());
        sb.append("\nPhoneType            :" + tm.getPhoneType());
        sb.append("\nSimCountryIso        :" + tm.getSimCountryIso());
        sb.append("\nSimOperator          :" + tm.getSimOperator());
        sb.append("\nSimOperatorName      :" + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber      :" + tm.getSimSerialNumber());
        sb.append("\ngetSimState          :" + tm.getSimState());
        sb.append("\nSubscriberId         :" + tm.getSubscriberId());
        sb.append("\nVoiceMailNumber      :" + tm.getVoiceMailNumber());
        return sb.toString();

    }

    private static Signature getPackageSignature(Context context) {
        final PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (Exception ignored) {
        }

        Signature signature = null;
        if (info != null) {
            Signature[] signatures = info.signatures;
            if (signatures != null && signatures.length > 0) {
                signature = signatures[0];
            }
        }

            Log.v(TAG, "getSignature() " + signature);
        return signature;
    }

    public static boolean checkPermission(Context context, @NonNull String permName) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager != null && packageManager.checkPermission(permName, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }
}
