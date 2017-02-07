package com.calvin.commonlib.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by calvin on 2016/10/13 17:14.
 */

public final class NavigatorUtil {
    private static final String PARCELABLE_EXTRA_KEY = "key";

    public static <T> T getParcelableExtra(@NonNull Activity activity) {
        Parcelable parcelable = activity.getIntent().getParcelableExtra(NavigatorUtil.PARCELABLE_EXTRA_KEY);
        activity = null;
        return (T)parcelable;
    }

    private static void overlay(@NonNull Context context, Class<? extends Activity> targetClazz, int flags, Parcelable parcelable) {
        Intent intent = new Intent(context, targetClazz);
        setFlags(intent, flags);
        putParcelableExtra(intent, parcelable);
        context.startActivity(intent);
        context = null;
    }

    private static void overlay(@NonNull Context context, Class<? extends Activity> targetClazz, Parcelable parcelable) {
        Intent intent = new Intent(context, targetClazz);
        putParcelableExtra(intent, parcelable);
        context.startActivity(intent);
        context = null;
    }

    private static void overlay(@NonNull Context context, Class<? extends Activity> targetClazz, Serializable serializable) {
        Intent intent = new Intent(context, targetClazz);
        putSerializableExtra(intent, serializable);
        context.startActivity(intent);
        context = null;
    }

    private static void overlay(@NonNull Context context, Class<? extends Activity> targetClazz) {
        Intent intent = new Intent(context, targetClazz);
        context.startActivity(intent);
        context = null;
    }

    private static void forward(@NonNull Context context, Class<? extends Activity> targetClazz, int flags, Parcelable parcelable) {
        Intent intent = new Intent(context, targetClazz);
        setFlags(intent, flags);
        intent.putExtra(PARCELABLE_EXTRA_KEY, parcelable);
        context.startActivity(intent);
        if (isActivity(context)) return;
        ((Activity)context).finish();
        context = null;
    }

    private static void forward(@NonNull Context context, Class<? extends Activity> targetClazz, Parcelable parcelable) {
        Intent intent = new Intent(context, targetClazz);
        putParcelableExtra(intent, parcelable);
        context.startActivity(intent);
        if (isActivity(context)) return;
        ((Activity)context).finish();
        context = null;
    }

    private static void forward(@NonNull Context context, Class<? extends Activity> targetClazz, Serializable serializable) {
        Intent intent = new Intent(context, targetClazz);
        putSerializableExtra(intent, serializable);
        context.startActivity(intent);
        if (isActivity(context)) return;
        ((Activity)context).finish();
        context = null;
    }

    private static void forward(@NonNull Context context, Class<? extends Activity> targetClazz) {
        Intent intent = new Intent(context, targetClazz);
        context.startActivity(intent);
        if (isActivity(context)) return;
        ((Activity)context).finish();
        context = null;
    }

    private static void startForResult(Context context, Class<? extends Activity> targetClazz, int flags) {
        Intent intent = new Intent(context, targetClazz);
        if (isActivity(context)) return;
        ((Activity)context).startActivityForResult(intent, flags);
        context = null;
    }

    private static void startForResult(Context context, Class<? extends Activity> targetClazz, int flags, Parcelable parcelable) {
        Intent intent = new Intent(context, targetClazz);
        if (isActivity(context)) return;
        putParcelableExtra(intent, parcelable);
        ((Activity)context).startActivityForResult(intent, flags);
        context = null;
    }

    private static void setResult(Context context, Class<? extends Activity> targetClazz, int flags, Parcelable parcelable) {
        Intent intent = new Intent(context, targetClazz);
        setFlags(intent, flags);
        putParcelableExtra(intent, parcelable);
        if (isActivity(context)) return;
        ((Activity)context).setResult(flags, intent);
        ((Activity)context).finish();
    }

    private static boolean isActivity(Context context) {
        if (!(context instanceof Activity)) return true;
        return false;
    }

    private static void setFlags(Intent intent, int flags) {
        if (flags < 0) return;
        intent.setFlags(flags);
    }

    private static void putParcelableExtra(Intent intent, Parcelable parcelable) {
        if (parcelable == null) return;
        intent.putExtra(PARCELABLE_EXTRA_KEY, parcelable);
    }

    private static void putSerializableExtra(Intent intent, Serializable serializable) {
        if (serializable == null) return;
        intent.putExtra(PARCELABLE_EXTRA_KEY, serializable);
    }
}
