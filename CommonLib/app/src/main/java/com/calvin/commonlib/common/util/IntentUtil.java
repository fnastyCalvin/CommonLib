package com.calvin.commonlib.common.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by jiangtao on 2016/4/6 11:12.
 */
public class IntentUtil {
    private static final String TAG  = IntentUtil.class.getSimpleName();
    private IntentUtil() {
        //no instance
    }

    /**
     * 从其他浏览器打开
     * @param context
     * @param url
     */
    public static void startOutsideBrowser(@NonNull Context context, @NonNull String url){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        if(intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        }
    }

    /**
     * 返回桌面，类似手动点了Home键
     * @param context
     */
    public static void goHome(@NonNull Context context){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }

    /**
     * 拨打电话
     * @param context
     * @param phoneNumber 电话号码
     */
    @PermissionChecker.PermissionResult
    public static void callPhone(@NonNull Context context, @NonNull String phoneNumber) {
        if(PermissionChecker.PERMISSION_GRANTED == PermissionChecker.checkSelfPermission(context,"android.permission.CALL_PHONE")){
            context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
        }
        else {
            Toast.makeText(context, "请开启拨打电话的权限", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 跳转至拨号界面
     * @param context
     * @param phoneNumber 电话号码电话号码
     */
    public static void callDial(@NonNull Context context, @NonNull String phoneNumber) {
        context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
    }

    /**
     * 发送短信
     * @param context
     * @param phoneNumber
     * @param content
     */
    public static void sendSms(@NonNull Context context, @NonNull String phoneNumber, String content) {
        Uri uri = Uri.parse("smsto:"+ (TextUtils.isEmpty(phoneNumber) ? "" : phoneNumber));
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", TextUtils.isEmpty(content) ? "" : content);
        context.startActivity(intent);
    }
}
