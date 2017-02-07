package com.calvin.commonlib.http.rx;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.calvin.commonlib.App;
import com.calvin.commonlib.R;
import com.calvin.commonlib.common.util.LoginUtil;
import com.calvin.commonlib.common.util.ToastUtils;
import com.calvin.commonlib.http.base.ErrorCode;
import com.calvin.commonlib.http.base.Result;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.UnknownFormatConversionException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by jiangtao on 2016/2/29 13:53.
 */
public abstract class RetrofitResultSubscriber<T> extends Subscriber<Result<T>>{

    public static final String TAG = "RetrofitResult";

    private WeakReference<Context> contextRef;

    public RetrofitResultSubscriber() {}

    public RetrofitResultSubscriber(Context context) {
        contextRef = new WeakReference<Context>(context);
    }

    public Context getContext() {
        if (contextRef != null) {
            return contextRef.get();
        }
        return null;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        /*Throwable rootThrowable = e;
        while(rootThrowable.getCause() != null){
            e = rootThrowable;
            rootThrowable = rootThrowable.getCause();
        }*/
        e.printStackTrace();
        RetrofitResultException resultException;

        if(e instanceof HttpException){//non-200 error
            HttpException httpError =(HttpException) e;
            resultException = new RetrofitResultException(httpError.code(),"non-200 HTTP ERROR",httpError);
            Log.e(TAG,"Retrofit Http Error");
            onFailure(resultException);
        }
        else if(e instanceof IOException){//network error
            resultException = new RetrofitResultException(0,"NETWORK ERROR",e);
            Log.e(TAG,"Retrofit Network Error");
            ToastUtils.showShort(App.getAppContext().getString(R.string.str_network_not_connected));
            onFailure(resultException);
        }
        //json parse error
        else if (e instanceof UnknownFormatConversionException || e instanceof JsonIOException
                || e instanceof JSONException || e instanceof JsonParseException || e instanceof JsonSyntaxException){
            resultException = new RetrofitResultException(RetrofitResultException.PARSE_ERROR,"PARSE ERROR",e);
            Log.e(TAG, "Retrofit Parse Error");
//                ToastUtils.showShort("Retrofit Parse Error");
            onFailure(resultException);
        }
        //unknown error
        else {
            returnUnKnow(e);
        }
    }

    private void returnUnKnow(Throwable e) {
        RetrofitResultException resultException = new RetrofitResultException(RetrofitResultException.UNKNOWN,"UNKNOWN ERROR",e);
        ToastUtils.showShort("未知错误");
        onFailure(resultException);
    }

    @Override
    public void onNext(Result<T> t) {
        if(t == null)return;
        Result result = (Result)t;
        /*PublishSubject<Result> subject = PublishSubject.create();
        subject.map(new Func1<Result, T>() {
            @Override
            public T call(Result result) {
                if(MsgCode.SUCESS.equals(result.getCode() +"")) {
                    try {
                        return (T)result.value;
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        onError(new RetrofitResultException(Integer.parseInt(result.getCode()), result.getMsg()));
                    }
                }
                else {
                    ToastUtils.showLong(result.getMsg());
                    onError(new RetrofitResultException(Integer.parseInt(result.getCode()), result.getMsg()));
                }
                return null;
            }
        }).subscribe(new Action1<T>() {
            @Override
            public void call(T t) {
                onSuccess(t);
            }
        });
        subject.onNext(result);*/
        if(ErrorCode.SUCCESS == result.code) {
            try {
                onSuccess((T)result.value);
            }
            catch (Exception e){
                e.printStackTrace();
                onError(new RetrofitResultException(Integer.parseInt(result.code+""), result.msg));
            }
        }
        else {
            if (!TextUtils.isEmpty(result.msg)) {
                ToastUtils.showLong(result.msg);
            }
            if (1013 == result.code || 1006 == result.code) {
                if (getContext() != null) {
                    LoginUtil.getInstance().showLoginPage(getContext());
                }
            }
            else {
                onResultCode(result.code );
            }
        }
    }

    /**
     * call this when getting non SUCCESS code
     */
    protected void onResultCode(int resultCode){

    }

    public void onFailure(RetrofitResultException e){
        e.printStackTrace();
        Log.e(TAG, "onError: "+ e.toString());
    }

    public abstract void onSuccess(T  data);


    public static class RetrofitResultException extends RuntimeException{
        public int code;
        public String msg;

        public static final int UNKNOWN = 1000;
        public static final int PARSE_ERROR = 1001;

        public RetrofitResultException(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public RetrofitResultException(int code,String msg,Throwable e){
            this.code = code;
            this.msg = msg;
        }

        @Override
        public String toString() {
            return "http code = "+code+"\n cause = "+msg +"\n"+super.toString();
        }
    }
}
