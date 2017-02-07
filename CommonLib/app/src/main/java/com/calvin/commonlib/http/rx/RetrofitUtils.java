package com.calvin.commonlib.http.rx;

import android.os.Build;

import com.calvin.commonlib.App;
import com.calvin.commonlib.BuildConfig;
import com.calvin.commonlib.common.util.AppUtil;
import com.calvin.commonlib.common.util.NetworkUtil;
import com.calvin.commonlib.common.util.SecurePreferences;
import com.calvin.commonlib.http.AppApi;
import com.calvin.commonlib.http.base.Result;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitUtils {
    private static Retrofit instance;

    private static String cookies;

    private static OkHttpClient okHttpClient = new OkHttpClient();

    public static String getCookies() {
        StringBuilder cookies = new StringBuilder();
        SecurePreferences preferences = SecurePreferences.getInstance();
        /*cookies.append("mendian_user_id=" + preferences.getString(Constants.USER_ID))
                .append("; ")
                .append("mendian_user_account=" + preferences.getString(Constants.ACCOUNT))
                .append("; ")
                .append("mendian_cIdentity=" + preferences.getString(Constants.IDENTITY))
                .append("; ")
                .append("mendian_user_source=" + Constants.SOURCE)
                .append("; ");*/
        return cookies.toString();
    }

    private static AppApi client;

//    private static SnailApi snailApiClient;

    private static Retrofit.Builder builder;

    static GsonBuilder  gsonBuilder;

    static {
        gsonBuilder = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .serializeNulls();
//        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(Result.class, new Deserializer());
        /*
        //修改请求头
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder requestBuilder = originalRequest.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .method(originalRequest.method(), originalRequest.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };*/
        OkHttpClient.Builder okBuilder = new OkHttpClient().newBuilder()
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .cache(new Cache(App.getAppContext().getCacheDir(),10*10*1024))
                .addNetworkInterceptor(new UserAgentInterceptor())
                .addNetworkInterceptor(new AddCookieInterceptor())
//              .addNetworkInterceptor(new GzipRequestInterceptor())
                .addNetworkInterceptor(new HttpCacheInterceptor());

        OkHttpClient.Builder okBuilderSnail = new OkHttpClient().newBuilder()
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .cache(new Cache(App.getAppContext().getCacheDir(),10*10*1024))
                .addNetworkInterceptor(new UserAgentInterceptor())
//                .addNetworkInterceptor(new GzipRequestInterceptor())
                .addNetworkInterceptor(new HttpCacheInterceptor());

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okBuilder.addInterceptor(loggingInterceptor).addNetworkInterceptor(new StethoInterceptor());
            okBuilderSnail.addInterceptor(loggingInterceptor).addNetworkInterceptor(new StethoInterceptor());
        }

        okHttpClient = okBuilder.build();

        builder = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

    }

    public static void setCookies(String cookies) {
        RetrofitUtils.cookies = cookies;
    }

    public static <T> T createRest(Class<T> clazz) {
        /*if (instance == null) {
            synchronized (RetrofitUtils.class) {
                if (instance == null) {
                    instance = builder.build();
                }
            }
        }*/
        return instance.create(clazz);
    }

    public static void changeApiBaseUrl(String newApiBaseUrl) {
        builder.baseUrl(newApiBaseUrl);
    }

    /**
     * 网厅的API
     * @return
     */
    /*public static SnailApi getSnailClient() {
        if (snailApiClient == null) {
            synchronized (RetrofitUtils.class) {
                changeApiBaseUrl(SnailApi.SERVER);
                snailApiClient = builder.build().create(SnailApi.class);
            }
        }
        return snailApiClient;
    }*/


    public static AppApi getClient() {
        if (client == null) {
            synchronized (RetrofitUtils.class) {
                changeApiBaseUrl(AppApi.SERVER);
                client = builder.build().create(AppApi.class);
            }
        }
        return client;
    }

    public static class Deserializer<T> implements JsonDeserializer<Result<T>> {
        @Override
        public Result<T> deserialize(JsonElement jElement, Type type, JsonDeserializationContext jdContext) throws JsonParseException {
            return new Gson().fromJson(jElement, type);
        }
    }

    public static final class UserAgentInterceptor implements Interceptor {
        private static final String USER_AGENT_HEADER_NAME = "User-Agent";
        private final String userAgentHeaderValue;

        public UserAgentInterceptor() {
            this.userAgentHeaderValue = getUserAgent();
        }

        private String getUserAgent() {
            StringBuilder sb = new StringBuilder("Android ").append(Build.VERSION.RELEASE);
            String version = AppUtil.getVersion(App.getAppContext());
            int versionCode = AppUtil.getVersionCode(App.getAppContext());
            sb.append("app version").append(version).append(" code").append(versionCode).append(" ");
            sb.append(Build.MANUFACTURER).append(" ").append(Build.PRODUCT);
            return sb.toString();
        }

        public UserAgentInterceptor(String userAgentHeaderValue) {
            this.userAgentHeaderValue = userAgentHeaderValue;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request originalRequest = chain.request();
            final Request requestWithUserAgent = originalRequest.newBuilder()
                    .removeHeader(USER_AGENT_HEADER_NAME)
                    .addHeader(USER_AGENT_HEADER_NAME, userAgentHeaderValue)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }

    public static final class HttpCacheInterceptor implements Interceptor{

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetworkUtil.isConnected(App.getAppContext())) {
                request = request.newBuilder()
//                        .cacheControl(CacheControl.FORCE_CACHE)
                        .cacheControl(new CacheControl.Builder().maxAge(600, TimeUnit.SECONDS).maxStale(2419200,TimeUnit.SECONDS).build())
                        .build();
//                Log.d("Cache-Control", "isNetworkConnected..request.."+request.headers().get("Cache-Control"));
            }

            Response response = chain.proceed(request);
            Response newResponse = null;
            if (NetworkUtil.isConnected(App.getAppContext())) {
                newResponse = response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, max-age=0")
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 4;
                newResponse = response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale="+maxStale)
                        .build();
            }
            return newResponse;
        }
    }

    public static final class GzipRequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
                return chain.proceed(originalRequest);
            }

            Request compressedRequest = originalRequest.newBuilder()
                    .header("Content-Encoding", "gzip")
                    .method(originalRequest.method(), forceContentLength(gzip(originalRequest.body())))
                    .build();
            return chain.proceed(compressedRequest);
        }

        /** https://github.com/square/okhttp/issues/350 */
        private RequestBody forceContentLength(final RequestBody requestBody) throws IOException {
            final Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            return new RequestBody() {
                @Override
                public MediaType contentType() {
                    return requestBody.contentType();
                }

                @Override
                public long contentLength() {
                    return buffer.size();
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    sink.write(buffer.snapshot());
                }
            };
        }

        private RequestBody gzip(final RequestBody body) {
            return new RequestBody() {
                @Override public MediaType contentType() {
                    return body.contentType();
                }

                @Override public long contentLength() {
                    return -1; // We don't know the compressed length in advance!
                }

                @Override public void writeTo(BufferedSink sink) throws IOException {
                    BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                    body.writeTo(gzipSink);
                    gzipSink.close();
                }
            };
        }
    }

    public static final class AddCookieInterceptor implements Interceptor{
        private static final String HEADER_COOKIE = "Cookie";
        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request originalRequest = chain.request();
            final Request requestNew = originalRequest.newBuilder()
                    .removeHeader(HEADER_COOKIE)
                    .addHeader(HEADER_COOKIE, getCookies())
                    .build();
            return chain.proceed(requestNew);
        }
    }
}
