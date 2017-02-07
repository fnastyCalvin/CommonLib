package com.calvin.commonlib.common.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.calvin.commonlib.common.base.BaseActivity;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import rx.Subscription;

/**
 * Created by jiangtao on 2016/5/19 10:52.
 */
public class LoginUtil
{
    private static final String TAG = "LoginUtil";

    private LoginUtil() {
        //no instance
    }

    private static LoginUtil instance;

    public static LoginUtil getInstance() {
        if (instance == null) {
            synchronized (LoginUtil.class)
            {
                if (instance == null)
                {
                    instance = new LoginUtil();
                }
            }
        }
        return instance;
    }

    private OnLoginSuccessListener onLoginSuccessListener;

    public interface OnLoginSuccessListener{
        void onLoginSuccess();
    }

    public void setOnLoginSuccessListener(OnLoginSuccessListener listener){
        onLoginSuccessListener = listener;
    }

    public OnLoginSuccessListener getOnLoginSuccessListener() {
        return onLoginSuccessListener;
    }

    public void login(@NonNull final Context context,@NonNull final String email, final  @NonNull String password, @NonNull String deviceId) {
        final ReferenceQueue<Context> refQueue = new ReferenceQueue<Context>();
        final WeakReference<Context> contextRef= new WeakReference<Context>(context,refQueue);
        final BaseActivity activity = (BaseActivity)contextRef.get();
        Subscription loginSub = null;
                /*= RetrofitUtils.getClient().login(email, password, deviceId, Constants.SOURCE)
                .compose(RxSchedulers.<User>applyIoSchedulers())
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        if (activity != null) {
                            activity.dismissProgressDialog();;
                        }
                    }
                })
                .subscribe(new RetrofitResultSubscriber<User>() {
                    @Override
                    public void onSuccess(User data) {
                        if (data != null) {
                            data.isLogin = true;
                            App.setUser(data);
                            SecurePreferences.getInstance().put(Constants.IS_LOGIN,true);
                            Gson gson = new Gson();
                            String json = gson.toJson(data);
                            SecurePreferences.getInstance().put(Constants.ACCOUNT, email);
                            SecurePreferences.getInstance().put(Constants.PASSWORD, password);
                            SecurePreferences.getInstance().put(Constants.PHONE, data.phone);
                            SecurePreferences.getInstance().put(Constants.USER_INFO, json);
                            SecurePreferences.getInstance().put(Constants.EMPNO, data.empNo);
                            SecurePreferences.getInstance().put(Constants.USER_ID, data.userId);
                            SecurePreferences.getInstance().put(Constants.IDENTITY, data.identity);
                            if (getOnLoginSuccessListener() != null){
                                onLoginSuccessListener.onLoginSuccess();
                                setOnLoginSuccessListener(null);
                            }
                            else {
                               *//* if (contextRef.get() != null) {
                                    Intent i = new Intent(contextRef.get(), HomeActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    contextRef.get().startActivity(i);
                                }*//*
                            }
//                            release(contextRef,refQueue);
                        }
                    }

                });*/
        if (activity != null) {
            activity.addSubscription(loginSub);
        }
    }

    private void release(final WeakReference<Context> contextRef,@NonNull ReferenceQueue<Context> refQueue) {
        if (contextRef != null) {
            contextRef.clear();
            contextRef.enqueue();
        }
        refQueue.poll();
        System.gc();
    }

    public void showLoginPage(final Context context) {
        final ReferenceQueue<Context> refQueue = new ReferenceQueue<Context>();
        WeakReference<Context> contextRef= new WeakReference<Context>(context,refQueue);
        if (contextRef.get() != null) {
//            contextRef.get().startActivity(new Intent(contextRef.get(), LoginActivity.class));
//            release(contextRef,refQueue);
        }
    }
}
