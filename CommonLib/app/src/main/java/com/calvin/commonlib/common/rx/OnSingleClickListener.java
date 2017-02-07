package com.calvin.commonlib.common.rx;

import android.os.Looper;
import android.view.View;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * 防止多次点击发送多次事件<br/>
 * 默认3秒内只响应一次<br/>
 * Created by jiangtao on 2016/3/22 16:50.
 */
public abstract  class OnSingleClickListener implements View.OnClickListener {
    private static final long TIMEOUT = 3;
    PublishSubject<View> subject = PublishSubject.create();

    public OnSingleClickListener(){
        this(TIMEOUT, TimeUnit.SECONDS);
    }

    public OnSingleClickListener(long timeout, TimeUnit unit){
        subject.throttleFirst(timeout, unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<View>() {
                    @Override
                    public void call(View view) {
                        onClicked(view);
                    }
                });
    }

    @Override
    public void onClick(final View v) {
        checkUiThread();
        subject.onNext(v);
    }

    public abstract void onClicked(View v);

    public static void checkUiThread() {
        if(Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Must be called from the main thread. Was: " + Thread.currentThread());
        }
    }
}
