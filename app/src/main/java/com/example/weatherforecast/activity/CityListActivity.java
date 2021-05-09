

package com.example.weatherforecast.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.AreaAdapter;
import com.example.weatherforecast.adapter.CityAdapter;
import com.example.weatherforecast.adapter.ProvinceAdapter;
import com.example.weatherforecast.bean.CityListBean;
import com.example.weatherforecast.utils.StreamChangeStrUtils;
import com.example.weatherforecast.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 城市列表数据渲染
 * @Author tang
 * @Date2021-03-30 8:54
 */
public class CityListActivity extends Activity implements View.OnClickListener {
    private List<String> list = new ArrayList<>();//字符串列表
    private List<CityListBean> provinceList = new ArrayList<>();//省列表数据
    private List<CityListBean.CityBean> citylist = new ArrayList<>();//市列表数据
    private List<CityListBean.CityBean.AreaBean> arealist = new ArrayList<>();//区/县列表数据
    private ProvinceAdapter provinceAdapter;//省数据适配器
    private CityAdapter cityAdapter;//市数据适配器
    private AreaAdapter areaAdapter;//县/区数据适配器
    private String provinceTitle;//标题
    CityListBean cityListBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        ListView listView = findViewById(R.id.city_list);
        initCityData(listView);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.city_list_return:
                Intent returnIntent = new Intent(CityListActivity.this,MainActivity.class);
                startActivity(returnIntent);
                finish();
                break;
            default:
                break;
        }
    }
    //手机自己的返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
            Intent returnIntent = new Intent(CityListActivity.this,MainActivity.class);
            startActivity(returnIntent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void initCityData(ListView listView) {

        try {
            final JSONArray Data = new JSONArray(StreamChangeStrUtils.readDataFromAsserts(CityListActivity.this,"City.txt").toString());
            for (int i = 0; i < Data.length(); i++) {
                JSONObject provinceJsonObject = Data.getJSONObject(i);
                String provinceName = provinceJsonObject.getString("name");
                CityListBean response = new CityListBean();
                response.setName(provinceName);
                System.out.println(response.getName());
                provinceList.add(response);
            }
            provinceAdapter = new ProvinceAdapter(CityListActivity.this, R.layout.item_city_list, provinceList);
            listView.setAdapter(provinceAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//第一层点击事件（为省添加点击事件）
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        JSONObject provinceObject = Data.getJSONObject(position);
                        final JSONArray cityArray = provinceObject.getJSONArray("city");
                        //更新列表数据
                        if (citylist != null) {
                            citylist.clear();
                        }
                        for (int i = 0; i < cityArray.length(); i++) {
                            JSONObject cityObj = cityArray.getJSONObject(i);
                            String cityName = cityObj.getString("name");
                            CityListBean.CityBean response = new CityListBean.CityBean();
                            response.setName(cityName);
                            citylist.add(response);
                        }
                        cityAdapter = new CityAdapter(CityListActivity.this, R.layout.item_city_list, citylist);
                        listView.setAdapter(cityAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//第二层点击事件（为市添加点击事件）
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    JSONObject cityJsonObj = cityArray.getJSONObject(position);
                                    JSONArray areaJsonArray = cityJsonObj.getJSONArray("area");
                                    if (arealist != null) {
                                        arealist.clear();
                                    }
                                    if (list != null) {
                                        list.clear();
                                    }
                                    for (int i = 0; i < areaJsonArray.length(); i++) {
                                        list.add(areaJsonArray.getString(i));
                                    }
                                    for (int j = 0; j < list.size(); j++) {
                                        CityListBean.CityBean.AreaBean response = new CityListBean.CityBean.AreaBean();
                                        response.setName(list.get(j).toString());
                                        arealist.add(response);
                                    }
                                    areaAdapter = new AreaAdapter(CityListActivity.this, R.layout.item_city_list, arealist);
                                    listView.setAdapter(areaAdapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//第三层点击事件（为区或县添加点击事件）直接更换天气
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            String district = arealist.get(position).getName();
                                            Intent change_cityIntent = new Intent(CityListActivity.this,MainActivity.class);
                                            change_cityIntent.putExtra("cityname",district);
                                            startActivity(change_cityIntent);
                                            finish();
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        ToastUtils.showShortToast(CityListActivity.this,"错误");
                    }
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
