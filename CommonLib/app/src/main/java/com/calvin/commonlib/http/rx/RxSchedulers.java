package com.calvin.commonlib.http.rx;


import com.calvin.commonlib.http.base.Result;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jiangtao on 2016/3/2 10:07.
 */
public class RxSchedulers {

    final static Observable.Transformer ioTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object responseObservable) {
            return ((Observable)responseObservable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .retryWhen(new RxHttpRetry());
        }
    };

    public static <T> Observable.Transformer<Result<T>, Result<T>> applyIoSchedulers() {
        return (Observable.Transformer<Result<T>, Result<T>>)ioTransformer;
    }

}
