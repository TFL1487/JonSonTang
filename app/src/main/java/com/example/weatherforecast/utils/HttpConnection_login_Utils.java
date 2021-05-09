package com.example.weatherforecast.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Description
 * @Author tang
 * @Date2021-04-09 21:09
 */
public class HttpConnection_login_Utils {
    public static HttpURLConnection getConnection(String data) throws Exception {

        //通过URL对象获取联网对象
        URL url = new URL("http://10.11.59.47:8080/web/loginServlet?");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");//设置post请求
        connection.setReadTimeout(5000);//设置5s的响应时间
        connection.setDoOutput(true);//允许输出
        connection.setDoInput(true);//允许输入
        //设置请求头，以键值对的方式传输（以下这两点在GET请求中不用设置）
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded ");
        connection.setRequestProperty("Content-Length", data.length() + "");//设置请求体的长度
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(data.getBytes());//进行传输操作
        //判断服务端返回的响应码，这里是http协议的内容
        return connection;
                //Android_login_register?serverTimezone=GMT%2B8
    }
}