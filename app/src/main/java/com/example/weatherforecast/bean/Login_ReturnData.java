package com.example.weatherforecast.bean;

/**
 * @Description
 * @Author tang
 * @Date2021-04-11 11:04
 */
public class Login_ReturnData {

    private String msg;

    private boolean success;

    private Detail_ReturnData detail;

    public void setMsg(String msg){
        this.msg = msg;
    }
    public String getMsg(){
        return this.msg;
    }
    public void setSuccess(boolean success){
        this.success = success;
    }
    public boolean getSuccess(){
        return this.success;
    }
    public void setDetail(Detail_ReturnData detail){
        this.detail = detail;
    }
    public Detail_ReturnData getDetail() {
        return this.detail;
    }

}
