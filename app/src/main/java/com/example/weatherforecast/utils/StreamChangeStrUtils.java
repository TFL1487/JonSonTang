package com.example.weatherforecast.utils;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Description 将流转化为字符串
 * @Author tang
 * @Date2021-04-09 21:07
 */
public class StreamChangeStrUtils {
    public static String readDataFromAsserts(Context context, String fileName) {
        InputStream myInput = null;
        String content = "";
        try {
            myInput = context.getAssets().open(fileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                content += new String(buffer, 0, length, "utf-8");
            }
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

}
