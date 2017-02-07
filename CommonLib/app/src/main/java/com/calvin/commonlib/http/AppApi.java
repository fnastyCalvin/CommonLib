package com.calvin.commonlib.http;

import com.calvin.commonlib.http.base.Result;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by calvin on 2016/7/7 15:27.
 */
public interface AppApi {
    String SERVER = "";

    @POST("mendian/account/app/login")
    @FormUrlEncoded
    Observable<Result<Void>> login(@Field("account") String username,
                                   @Field("password") String password,
                                   @Field("imei") String imei,
                                   @Field("source") String source);


    @GET("mendian/account/app/userinfo/get")
    Observable<Result<Void>> getUserInfo();
}
