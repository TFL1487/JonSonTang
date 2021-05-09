package com.example.weatherforecast.utils;

import android.widget.EditText;

import com.example.weatherforecast.activity.RegisterActivity;

/**
 * @Description
 * @Author tang
 * @Date2021-04-11 12:16
 */
public class Check_UserUtils {
    public static Boolean LoginCheck(String l_usernameStr ,String l_passwordStr){
        if(!(l_usernameStr.equals("")) && !(l_passwordStr.equals(""))){
            return true;
        }
        return false;
    }

    public static Boolean RegisterCheck(String R_usernameStr,String R_passwordStr,String R_passwordStr_again){
        if (!(R_usernameStr.equals("")) || !(R_passwordStr.equals("")) || !(R_passwordStr_again.equals(""))){
            return true;
        }
        return false;
    }
    public static boolean ReRegisterCheck(String R_passwordStr,String R_passwordStr_again){
        if (R_passwordStr.equals(R_passwordStr_again)){
            return true;
        }
        return false;
    }
}
