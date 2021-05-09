package com.example.weatherforecast.bean;

/**
 * @Description
 * @Author tang
 * @Date2021-04-11 11:07
 */
public class Detail_ReturnData {

    private int id;

    private String username;

    private String password;

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return this.password;
    }

}
