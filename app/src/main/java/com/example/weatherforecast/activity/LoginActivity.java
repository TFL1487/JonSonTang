package com.example.weatherforecast.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListPopupWindow;

import androidx.annotation.Nullable;

import com.example.weatherforecast.R;
import com.example.weatherforecast.bean.Login_ReturnData;
import com.example.weatherforecast.bean.User;
import com.example.weatherforecast.utils.Check_UserUtils;
import com.example.weatherforecast.utils.ToastUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @Description
 * @Author tang
 * @Date2021-03-30 8:55
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText L_username;
    private EditText L_password;
    private ListPopupWindow lpw;
    private String L_usernameStr;
    private String L_passwordStr;

    private SharedPreferences spf;
    private SharedPreferences.Editor editor;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        L_username = findViewById(R.id.name);
        L_password = findViewById(R.id.password);

        spf = PreferenceManager.getDefaultSharedPreferences(this);
        editor = spf.edit();
        String usernames = spf.getString("username","");
        String passwords = spf.getString("password","");//
        final String[] U_List = usernames.split(";");
        final String[] P_List = passwords.split(";");
        if (U_List != null && U_List.length > 0 && P_List != null && P_List.length > 0){//
            L_username.setText(U_List[U_List.length-1]);
            L_password.setText(P_List[P_List.length-1]);//
        }
        lpw = new ListPopupWindow(this);
        lpw.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,U_List));
        lpw.setAnchorView(L_username);
        lpw.setModal(false);
        lpw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                L_username.setText(U_List[position]);
                L_password.setText(P_List[position]);//
                lpw.dismiss();
            }
        });
        L_username.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if (event.getX() >= (L_username.getWidth() - L_username.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())){
                        lpw.show();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit: //登录
                L_usernameStr = L_username.getText().toString().trim();
                L_passwordStr = L_password.getText().toString().trim();
                if ( !Check_UserUtils.LoginCheck(L_usernameStr,L_passwordStr)){
                    ToastUtils.showShortToast(LoginActivity.this,"用户名密码不能为空");
                }else {
                    login();
                }

                break;
            case R.id.register://注册
                Intent registerIntent1 = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerIntent1);
                break;
            case R.id.login_return://返回
                Intent returnIntent = new Intent(LoginActivity.this,MainActivity.class);
                returnIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(returnIntent);
                break;
            default:
                break;

        }

    }

    private void login() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json");
                HashMap<String,String> paramsMap = new HashMap<>();
                paramsMap.put("username",L_username.getText().toString());
                paramsMap.put("password",L_password.getText().toString());

                Gson gson = new Gson();
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create (mediaType,gson.toJson(paramsMap));

                String requestUrl = "http://192.168.137.1:8080/user/login";

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
    }

    public void login1(){
        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        User user = new User();
        user.setUsername("sd");
        user.setPassword("123456");
        //使用Gson 添加 依赖 compile 'com.google.code.gson:gson:2.8.1'
        Gson gson = new Gson();
        //使用Gson将对象转换为json字符串
        String json = gson.toJson(user);

        //MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8")
                , json);

        Request request = new Request.Builder()
                .url("http://10.11.59.47:8080/user/login")//请求的url
                .post(requestBody)
                .build();

        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println(response.body().string());
            }
        });

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
                if (returnData != null && returnData.getSuccess()){
                    //登录历史
                    String usernames = spf.getString("username","");
                    String passwords = spf.getString("password","");//
                    if (usernames.isEmpty() || passwords.isEmpty()){//
                        usernames = L_username.getText().toString();
                        passwords = L_password.getText().toString();//

                    }else{
                        if(! usernames.contains(L_username.getText().toString())){
                            usernames += ";" + L_username.getText().toString();
                            if (! passwords.contains(L_password.getText().toString())){
                                passwords += ";" + L_password.getText().toString();//
                            }
                        }
                    }
                    editor.putString("username",usernames);
                    editor.putString("password",passwords);
                    editor.commit();

                    String TruthName = returnData.getDetail().getUsername();
                    ToastUtils.showShortToast(LoginActivity.this,"登录成功，欢迎用户"+TruthName);
                }
                if (returnData != null && !returnData.getSuccess()){
                    String reason = returnData.getMsg();
                    ToastUtils.showShortToast(LoginActivity.this,reason);
                }

                if (returnData == null){
                    ToastUtils.showShortToast(LoginActivity.this,"连接服务器失败");
                }
            }
        });
    }
}
