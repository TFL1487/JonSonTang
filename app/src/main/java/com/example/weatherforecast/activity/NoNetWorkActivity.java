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
import com.example.weatherforecast.bean.DailyWeatherBean;
import com.example.weatherforecast.db.DBHelper;
import com.example.weatherforecast.db.DBManager;
import com.example.weatherforecast.utils.DateUtils;
import com.example.weatherforecast.utils.RecyclerViewScrollHelper;
import com.example.weatherforecast.utils.StatusBarUtil;
import com.example.weatherforecast.utils.ToastUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author tang
 * @Date2021-04-28 13:30
 */
public class NoNetWorkActivity extends Activity {
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
        StatusBarUtil.transparencyBar(NoNetWorkActivity.this);//设置状态栏透明

        showSQLData();
        Back(toolbar);

    }

    //设置返回点击事件
    protected void Back(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }
    //手机自己的返回
    private long timeMillis;//几点时间
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - timeMillis) > 2000) {
                ToastUtils.showShortToast(this, "再按一次退出GoodWeather");
                timeMillis = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void showSQLData(){
        List<String> cityList = DBManager.queryAllCityName(new DBHelper(this, "WeatherAll.db", null, 1));
        String city = cityList.get(cityList.size()-1);
        String Data = DBManager.queryInfoByCity(city, new DBHelper(this, "WeatherAll.db", null, 1));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTitle.setText(city);
                Gson gson = new Gson();
                DailyWeatherBean dailyWeatherBean = null;
                if (Data != null && !Data.isEmpty()) {
                    dailyWeatherBean = gson.fromJson(Data, DailyWeatherBean.class);
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
                        Daily_15D_Adapter daily_15D_adapter = new Daily_15D_Adapter(NoNetWorkActivity.this, R.layout.item_more_daily_list, mList);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NoNetWorkActivity.this);
                        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        rv_15d.setLayoutManager(linearLayoutManager);
                        PagerSnapHelper snapHelper = new PagerSnapHelper();
                        rv_15d.setOnFlingListener(null);//避免抛异常
                        //滚动对齐，使RecyclerView像ViewPage一样，一次滑动一项,居中
                        snapHelper.attachToRecyclerView(rv_15d);
                        rv_15d.setAdapter(daily_15D_adapter);
                    }
                } else {
                    ToastUtils.showShortToast(NoNetWorkActivity.this, "错误");
                }
            }
        });
    }

}
