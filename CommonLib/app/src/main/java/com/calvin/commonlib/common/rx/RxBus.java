package com.calvin.commonlib.common.rx;

import android.support.annotation.NonNull;

import com.calvin.commonlib.common.log.Log;

import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by calvin on 2016/10/13 14:31.
 */

public final class RxBus {
    private static RxBus instance;
    private final Subject<Object, Object> busSubject = new SerializedSubject<>(PublishSubject.create());

    private RxBus(){}

    public static RxBus getInstance() {
        if (instance == null) {
            instance = new RxBus();
        }

        return instance;
    }

    /**
     * 消息发送者调用
     *
     * @param o
     */
    public void send(Object o) {
        if (busSubject.hasObservers()) {
            busSubject.onNext(o);
        }
    }

    /**
     * 订阅
     *
     * @param subscription new CompositeSubscription()
     * @param eventLisener
     */
    public void subscribe(@NonNull CompositeSubscription subscription, @NonNull final EventLisener eventLisener) {
        subscription.add(busSubject
                .asObservable()
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {
                        Log.e("Rxbus", "dealRxEvent called with: " + "event = [" + event + "]");
                        eventLisener.dealRxEvent(event);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("Rxbus", "call: "+throwable);
                    }
                }));
    }

    public interface EventLisener {
        /**
         * 处理总线事件
         *
         * @param event
         */
        void dealRxEvent(Object event);
    }
}
