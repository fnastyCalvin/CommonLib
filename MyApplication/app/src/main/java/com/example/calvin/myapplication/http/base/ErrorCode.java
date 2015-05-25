package com.example.calvin.myapplication.http.base;

/**
 * 接口返回的错误代码
 * Created by calvin on 2015/1/26.
 */
public enum ErrorCode
{
    SUCCESS("1", "请求成功"),

    FAILURE("0", "请求失败"),

    NODATA("9", "暂无数据");

    public String value;
    public String message;

    ErrorCode(String value, String message) {
        this.value = value;
        this.message = message;
    }

    @Override
    public String toString() {
        return SUCCESS.message;
    }


}
