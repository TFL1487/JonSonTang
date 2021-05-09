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
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.Daily_15D_Adapter;
import com.example.weatherforecast.adapter.Daily_7D_Adapter;
import com.example.weatherforecast.bean.DailyWeatherBean;
import com.example.weatherforecast.utils.Constant;
import com.example.weatherforecast.utils.DateUtils;
import com.example.weatherforecast.utils.HttpUtils;
import com.example.weatherforecast.utils.RecyclerViewScrollHelper;
import com.example.weatherforecast.utils.StatusBarUtil;
import com.example.weatherforecast.utils.ToastUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @Description
 * @Author tang
 * @Date2021-04-27 13:18
 */
public class MoreDailyActivity extends Activity {

    Toolbar toolbar;
    TextView tvTitle;
    RecyclerView rv_15d;

    List<DailyWeatherBean.DailyBean> mList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_15d_weather_list);
        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tv_more_daily_title);
        rv_15d = findViewById(R.id.rv_15d);
        StatusBarUtil.transparencyBar(MoreDailyActivity.this);//设置状态栏透明
        tvTitle.setText(getIntent().getStringExtra("CityName"));
        DailyWeather_15D_Fill(getIntent().getStringExtra("CityId"));
        Back(toolbar);

    }

    //设置返回点击事件
    protected void Back(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(MoreDailyActivity.this, MainActivity.class);
                startActivity(backIntent);
                MoreDailyActivity.this.finish();
            }
        });
    }
    //手机自己的返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
            Intent backIntent = new Intent(MoreDailyActivity.this, MainActivity.class);
            startActivity(backIntent);
            MoreDailyActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //获取15天天气
    public void DailyWeather_15D_Fill(String cityId) {
        String NowUrl = Constant.DAILY_WEATHER_15D_URL + "location=" + cityId + "&key=" + Constant.API_KEY;

        HttpUtils.sendOkHttpRequest(NowUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showShortToast(MoreDailyActivity.this, "错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        DailyWeatherBean dailyWeatherBean = null;
                        if (responseData != null && !responseData.isEmpty()) {
                            dailyWeatherBean = gson.fromJson(responseData, DailyWeatherBean.class);
                            List<DailyWeatherBean.DailyBean> data = dailyWeatherBean.getDaily();
                            if (data != null && data.size() > 0) {

                                mList.clear();
                                //添加数据
                                mList.addAll(data);
                                for (int i = 0; i < data.size(); i++) {
                                    if (data.get(i).getFxDate().equals(DateUtils.getNowDate())) {
                                        //渲染完成后，定位到今天，因为和风天气预报有时候包括了昨天，有时候又不包括，搞得我很被动
                                        RecyclerViewScrollHelper.scrollToPosition(rv_15d, i);
                                    }
                                }
                                Daily_15D_Adapter daily_15D_adapter = new Daily_15D_Adapter(MoreDailyActivity.this, R.layout.item_more_daily_list, mList);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MoreDailyActivity.this);
                                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                                rv_15d.setLayoutManager(linearLayoutManager);
                                PagerSnapHelper snapHelper = new PagerSnapHelper();
                                rv_15d.setOnFlingListener(null);//避免抛异常
                                //滚动对齐，使RecyclerView像ViewPage一样，一次滑动一项,居中
                                snapHelper.attachToRecyclerView(rv_15d);
                                rv_15d.setAdapter(daily_15D_adapter);
                            }
                        } else {
                            ToastUtils.showShortToast(MoreDailyActivity.this, "错误");
                        }
                    }
                });
            }
        });
    }
}
