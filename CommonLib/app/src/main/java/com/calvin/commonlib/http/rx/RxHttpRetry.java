package com.calvin.commonlib.http.rx;

import com.calvin.commonlib.App;
import com.calvin.commonlib.common.util.NetworkUtil;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * <p>use {@link Observable#retryWhen} to create http retry connection</p>
 * <p>add<strong> .retryWhen(new RxHttpRetry())</strong> before .subscribe()</p>
 * Created by jiangtao on 2016/3/1 11:30.
 */
public class RxHttpRetry implements Func1<Observable<? extends Throwable>, Observable<?>>
{
    private static final String TAG = RxHttpRetry.class.getSimpleName();
    private int maxRetryCount = 3;
    private int retryDelay = 10;//second
    private int retryCount = 0;

    public RxHttpRetry(){
        this(3,10);
    }

    /**
     *
     * @param maxRetryCount
     * @param retryDelay TimeUnit second
     */
    public RxHttpRetry(int maxRetryCount, int retryDelay) {
        this.maxRetryCount = maxRetryCount;
        this.retryDelay = retryDelay;
    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> observable) {
        return observable.flatMap(new Func1<Throwable, Observable<?>>() {
            @Override
            public Observable<?> call(final Throwable throwable) {
//                Log.d(TAG,"start retry");
                if (throwable instanceof UnknownHostException) {
                    return Observable.error(throwable);
                }
                return Observable.just(throwable)
                        .zipWith(Observable.range(1, maxRetryCount), new Func2<Throwable, Integer, Integer>() {
                            @Override
                            public Integer call(Throwable throwable, Integer attempt) {
                                return attempt;
                            }
                        })
                        .flatMap(new Func1<Integer, Observable<?>>() {
                            @Override
                            public Observable<?> call(Integer i) {
                                retryCount++;
                                if(retryCount >= maxRetryCount ){
                                    return Observable.error(throwable);
                                }
                                if(!NetworkUtil.isConnected(App.getAppContext())){
                                    return Observable.error(throwable);
                                }
                                return Observable.timer((long) retryDelay , TimeUnit.SECONDS);
                            }
                        });
            }
        });
    }

}