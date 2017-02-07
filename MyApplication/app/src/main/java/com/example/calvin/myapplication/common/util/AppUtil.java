package com.example.calvin.myapplication.common.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.widget.Toast;


import com.example.calvin.myapplication.common.log.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class AppUtil {
    private static final String TAG = AppUtil.class.getSimpleName();

    /**
     * whether app is installed
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null || packageName == null || packageName.length() == 0) {
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
            Toast.makeText(context.getApplicationContext(), "启动失败",
                    Toast.LENGTH_LONG).show();
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
    private boolean isServiceRunning(Context context,String servieName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (servieName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static String getMacAddress(Context context) {
        //wifi mac地址
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String mac = info.getMacAddress();
        Log.i("system", " MAC：" + mac);
        return mac;
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
}
