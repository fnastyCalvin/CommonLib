package com.calvin.commonlib.common.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.calvin.commonlib.App;


public class ToastUtils {
    private ToastUtils() {
    }

    private static Toast toast;
    private static Handler handler = new Handler(Looper.getMainLooper());

    private static void show(Context context, int resId, int duration) {
        Toast.makeText(context, resId, duration).show();
    }

    private static void show(Context context, String message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static void showShort(final int resId) {
        if (toast == null) {
            toast = Toast.makeText(App.getAppContext(), resId, Toast.LENGTH_SHORT);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                toast.setText(App.getAppContext().getString(resId));
            }
        });
        toast.show();
    }

    public static void showShort(final String message) {
        if (toast == null) {
            toast = Toast.makeText(App.getAppContext(), message, Toast.LENGTH_SHORT);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                toast.setText(message);
            }
        });
        toast.show();
    }

    public static void showLong(final int resId) {
        if (toast == null) {
            toast = Toast.makeText(App.getAppContext(), resId, Toast.LENGTH_LONG);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                toast.setText(App.getAppContext().getString(resId));
            }
        });
        toast.show();
    }

    public static void showLong(final String message) {
        if (toast == null) {
            toast = Toast.makeText(App.getAppContext(), message, Toast.LENGTH_LONG);
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                toast.setText(message);
            }
        });
        toast.show();
    }
}
