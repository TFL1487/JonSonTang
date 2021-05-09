package com.example.weatherforecast.base;

/**
 * @Description
 * @Author tang
 * @Date2021-04-12 13:32
 */
public interface ReqCallBack<T> {
    /*响应成功*/
    void onReSuccess(T result);

    /*响应失败*/
    void onReqFailed(String errorMsg);

}
