package com.example.weatherforecast.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.weatherforecast.R;
import com.example.weatherforecast.bean.Login_ReturnData;
import com.example.weatherforecast.utils.Check_UserUtils;
import com.example.weatherforecast.utils.ToastUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText R_username;
    private EditText R_password;
    private EditText R_password_again;
    private String R_usernameStr;
    private String R_passwordStr;
    private String R_passwordStr_again;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        R_username = findViewById(R.id.user_name);
        R_password = findViewById(R.id.et_pwd);
        R_password_again = findViewById(R.id.et_pwd_again);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_return:
                Intent returnIntent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(returnIntent);
                break;
            case R.id.btn_register:
                R_usernameStr = R_username.getText().toString().trim();
                R_passwordStr = R_password.getText().toString().trim();
                R_passwordStr_again = R_password_again.getText().toString().trim();
                if (!Check_UserUtils.RegisterCheck(R_usernameStr,R_passwordStr,R_passwordStr_again)){
                    ToastUtils.showShortToast(RegisterActivity.this,"请完善账号密码");
                }else{
                    register();
                }
                break;
        }
    }

    private void register() {
        if (Check_UserUtils.ReRegisterCheck(R_passwordStr,R_passwordStr_again)) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                 MediaType.parse("application/json;charset=utf-8");
                MediaType mediaType = MediaType.parse("application/json");
                HashMap<String,String> paramsMap = new HashMap<>();
                paramsMap.put("username",R_username.getText().toString());
                paramsMap.put("password",R_password.getText().toString());

                Gson gson = new Gson();
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create (mediaType,gson.toJson(paramsMap));

                String requestUrl = "http://192.168.137.1:8080/user/register";

                Request request = new Request.Builder().url(requestUrl).post(body).build();

                Response response = null;
                String result = null;
                try{
                    response = client.newCall(request).execute();
                    result = response.body().string();

                }catch (IOException e){
                    e.printStackTrace();
                }
                showResponse(result);
            }
        }).start();
        }else {
            ToastUtils.showShortToast(RegisterActivity.this,"两次密码不一致");
        }
    }

    private void showResponse(String response) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    Login_ReturnData returnData = null;
                    if (response != null && !response.isEmpty()) {
                        returnData = gson.fromJson(response, Login_ReturnData.class);
                    }
                    if (returnData != null && returnData.getSuccess()) {
                        ToastUtils.showShortToast(RegisterActivity.this, "注册成功");
                    }
                    if (returnData != null && !returnData.getSuccess()) {
                        String reason = returnData.getMsg();
                        ToastUtils.showShortToast(RegisterActivity.this, reason);
                    }

                    if (returnData == null) {
                        ToastUtils.showShortToast(RegisterActivity.this, "连接服务器失败");
                    }
                }
            });

    }

}