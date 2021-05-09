package com.example.weatherforecast.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.AllLifeStyleAdapter;
import com.example.weatherforecast.bean.LifeStyleBean;
import com.example.weatherforecast.utils.Constant;
import com.example.weatherforecast.utils.HttpUtils;
import com.example.weatherforecast.utils.StatusBarUtil;
import com.example.weatherforecast.utils.ToastUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @Description 更多生活指数
 * @Author tang
 * @Date2021-05-01 0:24
 */
public class MoreLifestyleActivity extends Activity {
    TextView tvTitle;
    Toolbar toolbar;
    RecyclerView rv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_lifestyle);
        StatusBarUtil.transparencyBar(this);
        initView();
    }

    private void initView(){
        tvTitle = findViewById(R.id.tv_title);
        toolbar = findViewById(R.id.More_lifestyle_toolbar);
        rv = findViewById(R.id.More_lifestyle_rv);
        tvTitle.setText(getIntent().getStringExtra("CityName"));
        AllLifeStyle(getIntent().getStringExtra("CityId"));
        Back(toolbar);
    }
    //设置返回点击事件
    protected void Back(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(MoreLifestyleActivity.this, MainActivity.class);
                startActivity(backIntent);
                MoreLifestyleActivity.this.finish();
            }
        });
    }
    //手机自己的返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
            Intent backIntent = new Intent(MoreLifestyleActivity.this, MainActivity.class);
            startActivity(backIntent);
            MoreLifestyleActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //获取更多生活指数
    public void AllLifeStyle(String cityId) {
        String NowUrl = Constant.LIFESTYLE_ALL_URL + "location=" + cityId + "&key=" + Constant.API_KEY;
        HttpUtils.sendOkHttpRequest(NowUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        LifeStyleBean lifeStyleBean = null;
                        if (responseData != null && !responseData.isEmpty()) {
                            lifeStyleBean = gson.fromJson(responseData, LifeStyleBean.class);
                            List<LifeStyleBean.DailyBean> data = lifeStyleBean.getDaily();
                            if (data != null && data.size() > 0) {
                                AllLifeStyleAdapter allLifeStyleAdapter = new AllLifeStyleAdapter(MoreLifestyleActivity.this,R.layout.item_more_lifestyle_list, data);
                                rv.setLayoutManager(new LinearLayoutManager(MoreLifestyleActivity.this));
                                rv.setAdapter(allLifeStyleAdapter);
                            }else {
                                ToastUtils.showShortToast(MoreLifestyleActivity.this, "错误");
                            }
                        } else {
                            ToastUtils.showShortToast(MoreLifestyleActivity.this, "错误");
                        }
                    }
                });
            }
        });
    }
}
