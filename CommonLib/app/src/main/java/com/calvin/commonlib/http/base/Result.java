package com.calvin.commonlib.http.base;

import com.google.gson.annotations.SerializedName;

/**
 * Created by calvin
 */
public class Result<T> extends BaseData {
    @SerializedName("data")
	public T value;

}
