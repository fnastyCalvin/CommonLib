package com.example.calvin.myapplication.http.base;

import com.google.gson.annotations.SerializedName;

/**
 * Created by calvin
 */
public class ResultData<T> extends BaseData {
    @SerializedName("info")
	public T info;

    public AcmpInfoDto acmpInfoDto;

    public static class AcmpInfoDto  {
        public String acmpId;
        public String acmpTitle;
        public String acmpContent;
        public String acmpIcon;
    }
}
