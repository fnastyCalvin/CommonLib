package com.example.calvin.myapplication.http;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.example.calvin.myapplication.common.base.MyApplication;
import com.example.calvin.myapplication.common.log.Log;
import com.example.calvin.myapplication.common.util.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class OKHttpRequest {

    public static final String TAG = OKHttpRequest.class.getSimpleName();

    private static OKHttpRequest instance;

    private final static OkHttpClient client = new OkHttpClient();

    private final static Gson gson = new Gson();

    /****************** Content-Type ******************/

    public static final MediaType MediaType_JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 二进制流
     * 文件后缀名为.*
     *
     */
    public static final MediaType MediaType_Stream = MediaType.parse("application/octet-stream; charset=utf-8");
    public static final MediaType MediaType_Multipart = MediaType.parse("multipart/form-data; charset=utf-8");

    /**************************************************/

    //todo 处理缓存的问题，如果没有网络；通过request header里面添加Cache-Control: max-stale=3600，OKHttp会进行缓存
    //todo 服务器端通过request header里面添加Cache-Control: max-age=9600，确定缓存的时间

    private static Cache cache = null;

    /**
     * 进度提示框
     */
    private static Dialog progressDialog;

    private OKHttpRequest() {
        // throw new RuntimeException("error to i");
    }

    public static OKHttpRequest getInstance() {
        if (instance == null) {
            instance = new OKHttpRequest();
        }
        return instance;
    }

    static{
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setWriteTimeout(5, TimeUnit.SECONDS);
        client.setReadTimeout(5, TimeUnit.SECONDS);

        //设置缓存
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        try {
            if(cache == null)
            {
                cache = new Cache(new File(MyApplication.getAppContext().getCacheDir(),"okHttp"), cacheSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.setCache(cache);
    }

    /**
     * 自定义请求回调
     * @param <T>
     */
    public interface HttpRequestCallback<T> {

        void onStart(Context context);
        void onResult(T result,Response response);
        void onFailure(Request request,String msg);
    }

    /**
     * 显示进度框
     *
     * @param context
     * @param dialogMsg     进度提示文字
     * @param tag           请求的唯一识别标记
     */
    public void showProgress(final Context context, String dialogMsg, final String tag) {
        dismissProgress(context);

        if(TextUtils.isEmpty(dialogMsg))
        {
            //dialogMsg = context.getResources().getString(R.string.http_request_load_msg);
        }

        /*progressDialog = NewToast.makeDialog(context, dialogMsg, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (TextUtils.isEmpty(tag)) {
                    cancelPendingRequests(TAG);
                } else {
                    cancelPendingRequests(tag);
                }
            }
        });*/

        if (context != null && !((Activity) context).isFinishing()) {
            progressDialog.show();
        }

    }

    /**
     * 关闭进度框
     * @param context
     */
    public void dismissProgress(Context context) {
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return;
		}
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * 开启同步线程访问网络
     * 记住不要在UI线程执行
     * Synchronous
     * @param request
     * @return Response
     * @throws java.io.IOException
     */
    public static Response enqueueSync(Request request) throws IOException{
        return client.newCall(request).execute();
    }

    /**
     * 开启异步线程访问网络
     * Asynchronous
     * @param request
     * @param responseCallback
     */
    public static void enqueueAsync(Request request, Callback responseCallback){
        client.newCall(request).enqueue(responseCallback);
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果（实现空callback）
     * <b>Asynchronous</b>
     * @param request
     */
    public static void enqueueAsync(Request request){
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });
    }

    /**
     * 同步Get请求，记住不要在UI线程执行
     */
    public <T> void getSync(final Context context, final String url,final Type responseType,final Map<String,String> headerMap,
                            final boolean isShow,final String dialogMsg,final String tag,final HttpRequestCallback<T> callback){
        if(!beforeStart(context,url,responseType,isShow,dialogMsg,tag)){
            return;
        }
        callback.onStart(context);
        Request request = newRequest(url,headerMap);
        try {
            Response response = enqueueSync(request);
            if(!response.isSuccessful()){
               callback.onFailure(response.request(),null);
            }
            String resultJson = response.body().string();
            Log.d(tag, "okhttp 请求成功! \r\n 返回的json-->" + resultJson);
            T result = gson.fromJson(resultJson,responseType);
            callback.onResult(result,response);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(tag, "okhttp 请求失败..."+e.getMessage());
            callback.onFailure(request,e.getMessage());
        }
        finally {
            if(isShow)
            {
                dismissProgress(context);
            }
        }
    }

    /**
     * 同步Get请求，记住不要在UI线程执行
     */
    public <T> void getSync(final Context context, final String url,final Type responseType,final boolean isShow,final String tag,final HttpRequestCallback<T> callback){
        getSync(context,url,responseType,null,isShow,null,tag,callback);
    }

    /**
     * 异步Get请求(完全体)
     */
    public <T> void getAsync(final Context context, final String url,final Type responseType,final Map<String,String> paramMap,final Map<String,String> headerMap,
                            final boolean isShow,final String dialogMsg,final String tag,final HttpRequestCallback<T> callback){
        if(!beforeStart(context,url,responseType,isShow,dialogMsg,tag)){
            return;
        }
        callback.onStart(context);
        Request request = newRequest(url,headerMap,paramMap);
        try {
            enqueueAsync(request, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    //NewToast.makeText(context,context.getResources().getString(R.string.http_request_network_not_enabled));
                    Log.d(tag, "okhttp 请求失败..."+e.getMessage());
                    callback.onFailure(request,e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if(!response.isSuccessful()){
                        callback.onFailure(response.request(),response.message());
                    }
                    String resultJson = response.body().string();
                    Log.d(tag, "okhttp 请求成功! \r\n 返回的json-->" + resultJson);
                    T result = gson.fromJson(resultJson,responseType);
                    callback.onResult(result,response);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tag, "okhttp 请求失败..."+e.getMessage());
            callback.onFailure(request,e.getMessage());
        }
        finally {
            if(isShow)
            {
                dismissProgress(context);
            }
        }
    }

    /**
     * 异步Get请求
     */
    public <T> void getAsync(final Context context, final String url,final Type responseType,final Map<String,String> paramMap,final boolean isShow,final String tag,final HttpRequestCallback<T> callback){
        getAsync(context,url,responseType,paramMap,null,isShow,null,tag,callback);
    }

    /**
     * 异步Get请求
     */
    public <T> void getAsync(final Context context, final String url,final Type responseType,final boolean isShow,final String tag,final HttpRequestCallback<T> callback){
        getAsync(context,url,responseType,null,null,isShow,null,tag,callback);
    }

    private boolean beforeStart(Context context,String url,Type responseType,boolean isShow,String dialogMsg,String tag){
        if(!NetworkUtil.isConnected(context))
        {
            //NewToast.makeText(context,context.getResources().getString(R.string.http_request_network_not_enabled));
            return false;
        }
        if (isShow) {
            showProgress(context,dialogMsg, tag);
        }
        if (responseType == null) {
            Log.e(TAG,"error....context is null");
            return false;
        }
        if (context == null) {
            Log.e(TAG,"error....context is null");
            return false;
        }
        if(TextUtils.isEmpty(url)){
            Log.e(TAG, "error....url is null");
            return false;
        }
        return true;
    }

    /**
     * 异步Post请求(完全体)
     */
    public <T> void postAsync(final Context context, final String url,final Type responseType,final Map<String,String> paramMap,final Map<String,String> headerMap,
                             final boolean isShow,final String dialogMsg,final String tag,final HttpRequestCallback<T> callback){
        if(!beforeStart(context,url,responseType,isShow,dialogMsg,tag)){
            return;
        }
        callback.onStart(context);
        Request request = newRequest(url,headerMap,paramMap,null);
        try {
            enqueueAsync(request, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    dismissProgress(context);
                    //NewToast.makeText(context,context.getResources().getString(R.string.http_request_network_not_enabled));
                    Log.d(tag, "okhttp 请求失败..."+e.getMessage());
                    callback.onFailure(request,e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if(!response.isSuccessful()){
                        callback.onFailure(response.request(),response.message());
                    }
                    String resultJson = response.body().string();
                    Log.d(tag, "okhttp 请求成功! \n 返回的json-->" + resultJson);
                    T result = gson.fromJson(resultJson,responseType);
                    callback.onResult(result,response);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tag, "okhttp 请求失败..."+e.getMessage());
            callback.onFailure(request,e.getMessage());
        }
        finally {
            if(isShow)
            {
                dismissProgress(context);
            }
        }
    }

    public <T> void postAsync(final Context context, final String url,final Type responseType,final Map<String,String> paramMap,
                              final boolean isShow,final String dialogMsg,final String tag,final HttpRequestCallback<T> callback){
        postAsync(context,url,responseType,paramMap,null,isShow,dialogMsg,tag,callback);
    }

    /**
     * Post请求，只显示Loading，无文字
     */
    public <T> void postAsync(final Context context, final String url,final Type responseType,final Map<String,String> paramMap,
                              final String tag,final HttpRequestCallback<T> callback){
        postAsync(context,url,responseType,paramMap,null,true,null,tag,callback);
    }

    /**
     * get 请求时用到<br/>
     * 根据参数paramMap自动拼接url
     */
    private Request newRequest(String api,Map<String,String> headerMap,Map<String,String> paramMap) {
        Request request = null;
        //StringBuffer sb = new StringBuffer(Constant.API);
        StringBuffer sb = new StringBuffer("");
        if (!TextUtils.isEmpty(api)) {
            sb.append(api);
        }
        if (!api.contains("?") && paramMap != null && !paramMap.isEmpty()){
            sb.append("?");
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                sb.append(entry.getKey())
                  .append("="+entry.getValue());
            }
        }
        String newUrl = sb.toString();
        Log.d(TAG, "正在查询，请稍后..." + newUrl + "\n" );
        Request.Builder builder = new Request.Builder().url(newUrl);

        //add & replace headers
        builder = addHeaders(headerMap,builder);
        //builder.cacheControl(cacheControl);
        request = builder.build();
        return request;
    }

    /**
     * get 请求时用到<br/>
     * http//:www.xxx.com?a=xx&b=xx 或者 http//:www.xxx.com
     * @param api
     * @param headerMap
     * @return
     */
    private Request newRequest(String api,Map<String,String> headerMap){
        Request request = null;
        //StringBuffer sb = new StringBuffer(Constant.API);
        StringBuffer sb = new StringBuffer("");
        if(!TextUtils.isEmpty(api))
        {
            sb.append(api);
        }
        String newUrl = sb.toString();
        Log.d(TAG, "正在查询，请稍后..." + newUrl + "\n" );
        Request.Builder builder = new Request.Builder().url(newUrl);

        //add & replace headers
        builder = addHeaders(headerMap,builder);
        //builder.cacheControl(cacheControl);
        request = builder.build();
        return request;
    }

    //当写请求头的时候，使用header(name, value)可以设置唯一的name、value。如果已经有值，旧的将被移除，然后添加新的。使用addHeader(name, value)可以添加多值（添加，不移除已有的）。
    //当读取响应头时，使用header(name)返回最后出现的name、value。通常情况这也是唯一的name、value。如果没有值，那么header(name)将返回null。如果想读取字段对应的所有值，使用headers(name)会返回一个list。

    /**
     * Post 请求时用到<br/>
     */
    private Request newRequest(String api,Map<String,String> headerMap,Map<String,String> paramMap,CacheControl cacheControl){
        Request request = null;
        //StringBuffer sb = new StringBuffer(Constant.API);
        StringBuffer sb = new StringBuffer("");
        if(!TextUtils.isEmpty(api))
        {
            sb.append(api);
        }
        String newUrl = sb.toString();
        final Gson gson = new GsonBuilder().create();
        String json = gson.toJson(paramMap);
        Log.d(TAG, "正在查询，请稍后..." + newUrl + "\n" + json);
        Request.Builder builder = new Request.Builder().url(newUrl);

        //add & replace headers
        builder = addHeaders(headerMap,builder);

        //add params json
        if(paramMap != null && !paramMap.isEmpty())
        {
            builder.post(RequestBody.create(MediaType_JSON, json));
        }
//        builder.cacheControl(cacheControl);
        request = builder.build();
        return request;
    }

    /**
     * Multipart/form-data提交<br/>
     * 支持多个文件，多个参数的post
     * @param api
     * @param headerMap
     * @param file
     * @return
     */
    private Request newRequestMultipart(String api,Map<String,String> headerMap,Map<String,String> paramMap,File... file){
        Request request = null;
        //StringBuffer sb = new StringBuffer(Constant.API);
        StringBuffer sb = new StringBuffer("");
        if(!TextUtils.isEmpty(api))
        {
            sb.append(api);
        }
        String newUrl = sb.toString();
        final Gson gson = new GsonBuilder().create();
        String json = gson.toJson(paramMap);
        Log.d(TAG, "正在查询，请稍后..." + newUrl + "\n" + json);
        Request.Builder builder = new Request.Builder().url(newUrl);

        //add & replace headers
        builder = addHeaders(headerMap,builder);

        RequestBody requestBody = null;
        MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);

        //add params
        if(paramMap != null && !paramMap.isEmpty()){
            for(Map.Entry<String,String> entry : paramMap.entrySet()){
                if(entry != null && !TextUtils.isEmpty(entry.getKey()) && !TextUtils.isEmpty(entry.getValue())) {
                    multipartBuilder.addFormDataPart(entry.getKey(),entry.getValue());
                }
            }
        }

        //add file
        if(file != null && file.length > 0){
            for(int i=0;i< file.length;i++){
                if(file[i] != null && file[i].exists()) {
                    //todo name为image这里是写死的，根据项目自己改
                    /**
                     * 这里重点注意： name里面的值为服务端需要key 只有这个key 才可以得到对应的文件
                     * filename是文件的名字，包含后缀名的 比如:abc.png
                     */
                    multipartBuilder.addPart( Headers.of("Content-Disposition", "form-data; name=\"image\" ;filename=" + file[i].getName()),
                            RequestBody.create(MediaType_Stream, file[i]));
                }
            }
        }
        requestBody = multipartBuilder.build();
        builder.post(requestBody);
        request = builder.build();
        return request;
    }

    /**
     * 异步Post file请求, multipart/form提交(完全体)
     */
    public <T> void postMultipartAsync(final Context context, final String url,final Type responseType,final List<File> file,final Map<String,String> headerMap,final Map<String,String> paramMap,
                                  final boolean isShow,final String dialogMsg,final String tag,final HttpRequestCallback<T> callback){
        if(!beforeStart(context,url,responseType,isShow,dialogMsg,tag)){
            return;
        }
        /*if(file == null || file.isEmpty()){
            return;
        }*/
        callback.onStart(context);
        Request request = newRequestMultipart(url,headerMap,paramMap, file.toArray(new File[file.size()]));
        try {
            enqueueAsync(request, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    dismissProgress(context);
                    //NewToast.makeText(context,context.getResources().getString(R.string.http_request_network_not_enabled));
                    Log.d(tag, "okhttp 请求失败..."+e.getMessage());
                    callback.onFailure(request,e.getMessage());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if(!response.isSuccessful()){
                        callback.onFailure(response.request(),response.message());
                    }
                    String resultJson = response.body().string();
                    Log.d(tag, "okhttp 请求成功! \n 返回的json-->" + resultJson);
                    T result = gson.fromJson(resultJson,responseType);
                    callback.onResult(result,response);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tag, "okhttp 请求失败..."+e.getMessage());
            callback.onFailure(request,e.getMessage());
        }
        finally {
            if(isShow)
            {
                dismissProgress(context);
            }
        }
    }

    /**
     * Multipart/form-data提交<br/>
     * 支持多个文件，多个参数的post<br/>
     */
    public <T> void postMultipartAsync(final Context context, final String url,final Type responseType,final List<File> file,final Map<String,String> paramMap,
                                       final boolean isShow,final String dialogMsg,final String tag,final HttpRequestCallback<T> callback) {
        postMultipartAsync(context,url,responseType,file,null,paramMap,isShow,dialogMsg,tag,callback);
    }

    /**
     * Multipart/form-data提交<br/>
     * 支持多个文件，多个参数的post<br/>
     * <b>只显示Loading，无文字</b>
     */
    public <T> void postMultipartAsync(final Context context, final String url,final Type responseType,final List<File> file,final Map<String,String> paramMap,
                                       final String tag,final HttpRequestCallback<T> callback) {
        postMultipartAsync(context,url,responseType,file,null,paramMap,true,null,tag,callback);
    }

    /**
     * 文件上传，支持多个
     */
    public <T> void postFileAsync(final Context context, final String url,final Type responseType,final List<File> file,
                                       final boolean isShow,final String dialogMsg,final String tag,final HttpRequestCallback<T> callback) {
        postMultipartAsync(context,url,responseType,file,null,null,isShow,dialogMsg,tag,callback);
    }



    /**
     * 单个文件上传,没有测试过,请使用{@link newRequestMultipart}
     * @param api
     * @param headerMap
     * @param file
     * @return
     */
    @Deprecated
    private Request newRequestFile(String api,Map<String,String> headerMap,File file){
        Request request = null;
        //StringBuffer sb = new StringBuffer(Constant.API);
        StringBuffer sb = new StringBuffer("");
        if(!TextUtils.isEmpty(api))
        {
            sb.append(api);
        }
        String newUrl = sb.toString();
        Log.d(TAG, "正在上传文件，请稍后..." + newUrl + "\n" + file.getAbsolutePath());
        Request.Builder builder = new Request.Builder().url(newUrl);

        //add & replace headers
        builder = addHeaders(headerMap,builder);

        if(file != null){
            builder.post(RequestBody.create(MediaType_Stream, file));
        }
        request = builder.build();
        return request;
    }

    /**
     * returns a shallow copy OkHttpClient (浅复制) that you can customize independently<br/>
     * 用来自定义OkHttpClient的配置
     */
    public OkHttpClient cloneHttpClinet() {
          return  client.clone();
    }


    /**
     * Cancels all pending requests by the specified TAG, it is important to
     * specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (client != null && tag != null) {
            client.cancel(tag);
        }
    }

    private Request.Builder addHeaders(Map<String , String> headerMap,Request.Builder builder){
        if(headerMap != null && !headerMap.isEmpty()){
            Iterator<Map.Entry<String, String>> iterator = headerMap.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,String> entity = iterator.next();
                if(entity != null && !TextUtils.isEmpty(entity.getKey()) && !TextUtils.isEmpty(entity.getValue())) {
                    try {
                        builder.header(URLEncoder.encode(entity.getKey(), "UTF-8"), URLEncoder.encode(entity.getValue(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        throw new IllegalStateException("error");
                    }
                }
            }
        }
        return builder;
    }
}
