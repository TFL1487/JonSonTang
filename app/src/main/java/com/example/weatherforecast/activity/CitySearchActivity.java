package com.example.weatherforecast.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.SearchCityAdapter;
import com.example.weatherforecast.bean.NowWeatherBean;
import com.example.weatherforecast.bean.SearchCityBean;
import com.example.weatherforecast.utils.Constant;
import com.example.weatherforecast.utils.DateUtils;
import com.example.weatherforecast.utils.HttpUtils;
import com.example.weatherforecast.utils.StatusBarUtil;
import com.example.weatherforecast.utils.ToastUtils;
import com.example.weatherforecast.utils.WeatherUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @Description 城市搜索
 * @Author tang
 * @Date2021-04-30 16:24
 */
public class CitySearchActivity extends Activity implements View.OnClickListener {
    private AutoCompleteTextView editQuery; //输入框
    private ImageView ivClearSearch;//清空输入的内容图标
    private Toolbar toolbar;
    private RecyclerView rv; //数据显示列表

    List<SearchCityBean.LocationBean> mList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        //黑色字体
        StatusBarUtil.StatusBarLightMode(this);
        initView();
    }
    private void initView(){

        editQuery = findViewById(R.id.edit_query);
        ivClearSearch = findViewById(R.id.iv_clear_search);
        toolbar = findViewById(R.id.search_city_toolbar);
        rv = findViewById(R.id.result_rv);
        editQuery.addTextChangedListener(textWatcher);
        editQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String location = editQuery.getText().toString();
                    if (!TextUtils.isEmpty(location)) {
                        searchCity(location);
                    } else {
                        ToastUtils.showShortToast(CitySearchActivity.this, "请输入搜索关键词");
                    }
                }
                return false;
            }
        });
        Back(toolbar);

    }

    //设置返回点击事件
    protected void Back(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(CitySearchActivity.this, MainActivity.class);
                startActivity(backIntent);
                CitySearchActivity.this.finish();
            }
        });
    }
    /**
     * 输入监听
     */
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!"".equals(s.toString())) {//输入后，显示清除按钮
                ivClearSearch.setVisibility(View.VISIBLE);
            } else {//隐藏按钮
                ivClearSearch.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //清空输入的内容
            case R.id.iv_clear_search:
                ivClearSearch.setVisibility(View.GONE);
                editQuery.setText("");
                break;
            default:
                break;
        }
    }
    public void searchCity(String CityName){
        String url = Constant.CITY_SEARCH_URL + "location=" + CityName + "&key=" + Constant.API_KEY;

        HttpUtils.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showShortToast(CitySearchActivity.this, "错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        SearchCityBean searchCityBean = null;
                        if (responseData != null && !responseData.isEmpty()) {
                            searchCityBean = gson.fromJson(responseData, SearchCityBean.class);
                            mList.clear();
                            mList.addAll(searchCityBean.getLocation());
                            SearchCityAdapter searchCityAdapter = new SearchCityAdapter(CitySearchActivity.this,R.layout.item_search_city_list, mList);
                            rv.setLayoutManager(new LinearLayoutManager(CitySearchActivity.this));
                            rv.setAdapter(searchCityAdapter);
                            //为每个item设置各自的点击事件
                            searchCityAdapter.setOnItemClickListener(new SearchCityAdapter.OnRecycleViewItemClickListener() {
                                @Override
                                public void OnItemClick(View view, int position) {
                                    String district =  mList.get(position).getName();
                                    Intent change_cityIntent = new Intent(CitySearchActivity.this,MainActivity.class);
                                    change_cityIntent.putExtra("cityname",district);
                                    startActivity(change_cityIntent);
                                    finish();
                                }
                            });
                        } else {
                            ToastUtils.showShortToast(CitySearchActivity.this, "错误");
                        }
                    }
                });
            }
        });
    }

}
