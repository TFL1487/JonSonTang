package com.example.weatherforecast.utils;

import android.util.Log;

import com.example.weatherforecast.base.ReqCallBack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.security.auth.callback.Callback;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @Description 封装网络请求工具类
 * @Author tang
 * @Date2021-04-12 13:08
 */
public class HttpUtils {

    /*Post请求*/
    public static void sendPost(final String requestUrl, final String requestbody, final ReqCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    //建立连接
                    URL url = new URL(requestUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    //设置连接属性
                    connection.setDoOutput(true);//使用URL连接进行输出
                    connection.setDoInput(true);//使用URL连接进行输入
                    connection.setUseCaches(false);//忽略缓存
                    connection.setRequestMethod("POST");//设置URL请求方法
                    String requestString = requestbody;
                    //设置请求属性
                    byte[] requestStringBytes = requestString.getBytes();//获取数据字节数据
                    connection.setRequestProperty("Content-length",""+requestStringBytes.length);
                    connection.setRequestProperty("Content-Type","application/octet-stream");
                    connection.setRequestProperty("Content","Keep-Alive");//维持长连接
                    connection.setRequestProperty("Charset","UTF-8");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    //建立输出流，并写入数据
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(requestStringBytes);
                    outputStream.close();
                    //获取响应状态
                    int responseCode = connection.getResponseCode();
                    if (HttpURLConnection.HTTP_OK == responseCode){//连接成功
                        //当正确响应时处理数据
                        StringBuffer buffer = new StringBuffer();
                        String readLine;
                        BufferedReader responseReader;
                        //处理响应流
                        responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((readLine = responseReader.readLine()) != null) {
                            buffer.append(readLine).append("\n");
                        }
                        responseReader.close();
                        Log.d("HttpPOST",buffer.toString());
                        if (callBack != null){
                            callBack.onReSuccess(buffer.toString());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    if (callBack != null){
                        callBack.onReqFailed(e.toString());
                    }
                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }


    /*Get服务请求*/
    public static void sendGet(final String requestUrl,final ReqCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    //建立连接
                    URL url = new URL(requestUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    //设置连接属性
                    connection.setDoOutput(false);//使用URL连接进行输出
                    connection.setDoInput(true);//使用URL连接进行输入
                    connection.setRequestMethod("GET");//设置URL请求方法
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (HttpURLConnection.HTTP_OK == responseCode){//连接成功
                        //当正确响应时处理数据
                        StringBuffer buffer = new StringBuffer();
                        String readLine;
                        BufferedReader responseReader;
                        //处理响应流
                        responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((readLine = responseReader.readLine()) != null) {
                            buffer.append(readLine).append("\n");
                        }
                        responseReader.close();
                        Log.d("HttpPOST",buffer.toString());
                        if (callBack != null){
                            callBack.onReSuccess(buffer.toString());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    if (callBack != null){
                        callBack.onReqFailed(e.toString());
                    }
                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

   /*使用OkHttp进行数据请求*/
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
