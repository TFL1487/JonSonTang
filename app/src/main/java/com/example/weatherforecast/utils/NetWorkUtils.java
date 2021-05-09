package com.example.weatherforecast.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * @Description
 * @Author tang
 * @Date2021-04-26 23:18
 */
public class NetWorkUtils {
    /**
     * 判断网络连接是否可用
     *
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkCapabilities networkCapabilities = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        networkCapabilities = connectivity.getNetworkCapabilities(connectivity.getActiveNetwork());
                    }
                    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
                }
            }
        }
        return false;
    }
}
