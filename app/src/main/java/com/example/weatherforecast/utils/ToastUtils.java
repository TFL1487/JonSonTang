package com.example.weatherforecast.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @Description 消息提示工具类
 * @Author tang
 * @Date2021-03-30 13:21
 */
    public class ToastUtils {
        public static void showLongToast(Context context, CharSequence contain) {
            Toast.makeText(context.getApplicationContext(), contain, Toast.LENGTH_LONG).show();
        }

        public static void showShortToast(Context context, CharSequence contain) {
            Toast.makeText(context.getApplicationContext(), contain, Toast.LENGTH_SHORT).show();
        }
    }

