package com.example.weatherforecast.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.Daily_7D_Adapter;
import com.example.weatherforecast.adapter.HourlyAdapter;
import com.example.weatherforecast.bean.Air_qualityBean;
import com.example.weatherforecast.bean.CitySearchBean;
import com.example.weatherforecast.bean.DailyWeatherBean;
import com.example.weatherforecast.bean.HourlyWeatherBean;
import com.example.weatherforecast.bean.LifeStyleBean;
import com.example.weatherforecast.bean.NowWeatherBean;
import com.example.weatherforecast.db.DBHelper;
import com.example.weatherforecast.db.DBManager;
import com.example.weatherforecast.utils.Constant;
import com.example.weatherforecast.utils.DateUtils;
import com.example.weatherforecast.utils.HttpUtils;
import com.example.weatherforecast.utils.NetWorkUtils;
import com.example.weatherforecast.utils.ToastUtils;
import com.example.weatherforecast.utils.WeatherUtil;
import com.example.weatherforecast.view.RoundProgressBar;
import com.example.weatherforecast.view.WhiteWindmills;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity implements View.OnClickListener {

    private RxPermissions rxPermissions;//权限请求框架
    private PopupWindow mPopupWindow;
    private ImageView imageView;
    TextView tvTitle;//标题
    TextView City_view; // 当前城市
    TextView tvWeek; //当前是星期几
    TextView tvTemperature; //当前温度
    TextView tvTempHeight;//最高温
    TextView tvTempLow;//最低温
    TextView tvInfo;//天气状况
    TextView tvOldTime;//最近更新时间
    TextView tvWindDirection;//风向
    TextView tvWindPower;//风力
    RecyclerView rvHourly;//逐小时天气显示列表
    RecyclerView rv;//天气预报显示列表
    WhiteWindmills wwBig;//大风车
    WhiteWindmills wwSmall;//小风车
    RoundProgressBar rpbAqi; ////污染指数圆环
    TextView tvAirInfo;//空气质量
    TextView tvPm10;//PM10
    TextView tvPm25;//PM2.5
    TextView tvNo2;//二氧化氮
    TextView tvSo2;//二氧化硫
    TextView tvO3;//臭氧
    TextView tvCo;//一氧化碳
    SmartRefreshLayout refresh;//刷新布局
    private DBHelper dbHelper;
    List<LifeStyleBean.DailyBean> data = null;
    public static String IntentData = "";
    public static String Id = "";
    public String GPSCity = "";


    private List<DailyWeatherBean.DailyBean> dailyList_7D = new ArrayList<>();//天气预报数据列表

    private List<HourlyWeatherBean.HourlyBean> hourlyList = new ArrayList<>();//逐小时天气预报数据列表

    Handler.Callback mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    private void initData() {
        dbHelper = new DBHelper(this, "WeatherAll.db", null, 1);
        dbHelper.getWritableDatabase();
        NetWorkJudge(this);//判断网络是否可用
        Intent change_Intent = getIntent();
        IntentData = change_Intent.getStringExtra("cityname");

        rxPermissions = new RxPermissions(this);//实例化这个权限请求框架，否则会报错

        permissionVersion();//权限判断
        initView();
        //下拉刷新
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                setContentView(R.layout.activity_main);
                initData();
                refreshlayout.finishRefresh(2000);//传入false表示刷新失败
            }
        });

    }

    /**
     * 定位结果返回
     */
    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f
            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            int errorCode = location.getLocType();//161  表示网络定位结果
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            GPSCity = district;
            String street = location.getStreet();    //获取街道信息
            String locationDescribe = location.getLocationDescribe();    //获取位置描述信息

            if (IntentData == null) {
                ReturnCityID(district);
                String detailAddress = district + street;
                tvTitle.setText(district);
                City_view.setText(detailAddress);//设置文本显示
            } else {
                ReturnCityID(IntentData);
                tvTitle.setText(IntentData);
                City_view.setText(IntentData);//设置文本显示
            }
        }
    }

    private void initView() {
        refresh = findViewById(R.id.refresh);
        imageView = findViewById(R.id.setting_img);
        tvTitle = findViewById(R.id.tv_title);
        City_view = findViewById(R.id.tv_city);
        tvWeek = findViewById(R.id.tv_week);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvTempHeight = findViewById(R.id.tv_temp_height);
        tvTempLow = findViewById(R.id.tv_temp_low);
        tvInfo = findViewById(R.id.tv_info);
        tvOldTime = findViewById(R.id.tv_old_time);
        tvWindDirection = findViewById(R.id.tv_wind_direction);
        tvWindPower = findViewById(R.id.tv_wind_power);
        rvHourly = findViewById(R.id.rv_hourly);
        rv = findViewById(R.id.daily_rv);
        wwBig = findViewById(R.id.ww_big);
        wwSmall = findViewById(R.id.ww_small);
        rpbAqi = findViewById(R.id.rpb_aqi);
        tvAirInfo = findViewById(R.id.tv_air_info);
        tvPm10 = findViewById(R.id.tv_pm10);
        tvPm25 = findViewById(R.id.tv_pm25);
        tvNo2 = findViewById(R.id.tv_no2);
        tvSo2 = findViewById(R.id.tv_so2);
        tvO3 = findViewById(R.id.tv_o3);
        tvCo = findViewById(R.id.tv_co);
        refresh.setEnableLoadMore(false);
        mHandler = new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                NowWeatherFill((String) msg.obj);
                AirQualityFill((String) msg.obj);
                HourlyWeatherFill((String) msg.obj);
                DailyWeather_7D_Fill((String) msg.obj);
                LifeStyle((String) msg.obj);
                return false;
            }
        };

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                Intent loginIntent = new Intent(MainActivity.this, Memo_main_Activity.class);
                startActivity(loginIntent);
                finish();
                break;
            case R.id.city_list_img:
                Intent listIntent = new Intent(MainActivity.this, CityListActivity.class);
                startActivity(listIntent);
                finish();
                break;
            case R.id.tv_more_daily:
                Intent MoreDailyIntent = new Intent(MainActivity.this, MoreDailyActivity.class);
                MoreDailyIntent.putExtra("CityName",tvTitle.getText().toString());
                MoreDailyIntent.putExtra("CityId", Id);
                startActivity(MoreDailyIntent);
                finish();
            case R.id.setting_img:
                showPopupWindow();
                break;
            case R.id.tv_more_lifestyle:
                Intent MoreLifeStyleIntent = new Intent(MainActivity.this,MoreLifestyleActivity.class);
                MoreLifeStyleIntent.putExtra("CityName",tvTitle.getText().toString());
                MoreLifeStyleIntent.putExtra("CityId", Id);
                startActivity(MoreLifeStyleIntent);
                finish();
                break;
            default:
                break;
        }
    }

    public void LifeStyle_onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (v.getId()) {
            case R.id.tv_dress:
                builder.setTitle("穿衣指数");
                String msg = data.get(4).getText();
                builder.setMessage(msg);
                break;
            case R.id.tv_washcar:
                builder.setTitle("洗车指数");
                msg = data.get(2).getText();
                builder.setMessage(msg);
                break;
            case R.id.tv_cold:
                builder.setTitle("感冒指数");
                msg = data.get(5).getText();
                builder.setMessage(msg);
                break;
            case R.id.tv_sport:
                builder.setTitle("运动指数");
                msg = data.get(3).getText();
                builder.setMessage(msg);
                break;
            case R.id.tv_rays:
                builder.setTitle("紫外线指数");
                msg = data.get(0).getText();
                builder.setMessage(msg);
                break;
            case R.id.tv_air:
                builder.setTitle("空调指数");
                msg = data.get(1).getText();
                builder.setMessage(msg);
                break;
            default:
                break;
        }
        builder.create().show();
    }

    //判断网络状态
    public void NetWorkJudge(Context context) {
        if (NetWorkUtils.isNetworkAvailable(MainActivity.this)) {
            ToastUtils.showShortToast(MainActivity.this, "网络状态良好");
        } else {
            ToastUtils.showShortToast(MainActivity.this, "当前无网络状态");
            ToastUtils.showLongToast(MainActivity.this, "5秒后将进入无网模式");
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent NoNetWorkIntent = new Intent(MainActivity.this, NoNetWorkActivity.class);
                    startActivity(NoNetWorkIntent);
                    finish();
                }
            }, 5000);
        }
    }

    //定位器
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();

    //权限判断
    private void permissionVersion() {
        if (Build.VERSION.SDK_INT >= 23) {//6.0或6.0以上
            //动态权限申请
            permissionsRequest();
        } else {//6.0以下
            //发现只要权限在AndroidManifest.xml中注册过，均会认为该权限granted  提示一下即可
            ToastUtils.showShortToast(this, "你的版本在Android6.0以下，不需要动态申请权限。");
        }
    }

    //动态权限申请
    @SuppressLint("CheckResult")
    private void permissionsRequest() {
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe(granted -> {
            if (granted) {//申请成功
                //得到权限之后开始定位
                if (isOpenLocationServiceEnable()) {
                    startLocation();//开始定位
                } else {
                    ToastUtils.showLongToast(this, "请打开GPS");
                }
            } else {//申请失败
                ToastUtils.showShortToast(this, "权限未开启");
            }
        });
    }

    /**
     * 手机是否开启位置服务，如果没有开启那么App将不能使用定位功能
     */
    private boolean isOpenLocationServiceEnable() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }
    //开始定位
    private void startLocation() {
        //声明LocationClient类
        mLocationClient = new LocationClient(this);
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();

        //如果开发者需要获得当前点的地址信息，此处必须为true
        option.setIsNeedAddress(true);
        //可选，设置是否需要最新版本的地址信息。默认不需要，即参数为false
        option.setNeedNewVersionRgc(true);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.setLocOption(option);
        //启动定位
        mLocationClient.start();

    }


    //获取城市ID
    public void ReturnCityID(String CityName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = Constant.CITY_SEARCH_URL + "location=" + CityName + "&key=" + Constant.API_KEY;
                OkHttpClient okHttpClient = new OkHttpClient();
                Gson gson = new Gson();
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                Response response = null;
                String responseData = null;
                CitySearchBean citySearchBean = null;
                try {
                    response = okHttpClient.newCall(request).execute();
                    responseData = response.body().string();
                    if (responseData != null && !responseData.isEmpty()) {

                        citySearchBean = gson.fromJson(responseData, CitySearchBean.class);
                        String id = citySearchBean.getLocation().get(0).getId();
                        System.out.println("idddd" + id);
                        Message message = new Message();
                        message.what = 1;
                        message.obj = citySearchBean.getLocation().get(0).getId();
                        mHandler.handleMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //SQLList处理
    public void SQLDateCope(String city_name,String content){
        String str = DBManager.queryInfoByCity(city_name,dbHelper);
        if (str == null){
            DBManager.addCityInfo(city_name,content,dbHelper);
        }else {
            DBManager.updateInfoByCity(city_name,content,dbHelper);
        }
    }

    //获取当前天气
    public void NowWeatherFill(String cityId) {
        Id = cityId;
        String NowUrl = Constant.NOW_WEATHER_URL + "location=" + cityId + "&key=" + Constant.API_KEY;

        HttpUtils.sendOkHttpRequest(NowUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showShortToast(MainActivity.this, "错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        NowWeatherBean nowWeatherBean = null;
                        if (responseData != null && !responseData.isEmpty()) {
                            nowWeatherBean = gson.fromJson(responseData, NowWeatherBean.class);
                            tvTemperature.setText(nowWeatherBean.getNow().getTemp());//温度

//                                    String tempStr = "当前温度：" + nowWeatherBean.getNow().getTemp() + "度，天气" + nowWeatherBean.getNow().getText() + "。";

                            tvWeek.setText(DateUtils.getWeekOfDate(new Date()));//星期
                            tvInfo.setText(nowWeatherBean.getNow().getText());//天气状况

                            String time = DateUtils.updateTime(nowWeatherBean.getUpdateTime());//截去前面的字符，保留后面所有的字符，就剩下 22:00

                            tvOldTime.setText("最近更新时间：" + WeatherUtil.showTimeInfo(time) + time);
                            tvWindDirection.setText("风向     " + nowWeatherBean.getNow().getWindDir());//风向
                            tvWindPower.setText("风力     " + nowWeatherBean.getNow().getWindScale() + "级");//风力
                            wwBig.startRotate();//大风车开始转动
                            wwSmall.startRotate();//小风车开始转动
                        } else {
                            ToastUtils.showShortToast(MainActivity.this, "错误");
                        }
                    }
                });
            }
        });
    }

    //获取空气质量
    public void AirQualityFill(String cityId) {
        String NowUrl = Constant.AIRQUALITY_URL + "location=" + cityId + "&key=" + Constant.API_KEY;

        HttpUtils.sendOkHttpRequest(NowUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showShortToast(MainActivity.this, "错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                runOnUiThread(new Runnable() {

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        Air_qualityBean air_qualityBean = null;
                        if (responseData != null && !responseData.isEmpty()) {
                            air_qualityBean = gson.fromJson(responseData, Air_qualityBean.class);

                            rpbAqi.setMaxProgress(300);//最大进度，用于计算
                            rpbAqi.setMinText("0");//设置显示最小值
                            rpbAqi.setMinTextSize(32f);
                            rpbAqi.setMaxText("300");//设置显示最大值
                            rpbAqi.setMaxTextSize(32f);
                            rpbAqi.setProgress(Float.valueOf(air_qualityBean.getNow().getAqi()));//当前进度
                            rpbAqi.setArcBgColor(getColor(R.color.arc_bg_color));//圆弧的颜色
                            rpbAqi.setProgressColor(getColor(R.color.arc_progress_color));//进度圆弧的颜色
                            rpbAqi.setFirstText(air_qualityBean.getNow().getCategory());//空气质量描述 取值范围：优，良，轻度污染，中度污染，重度污染，严重污染
                            rpbAqi.setFirstTextSize(44f);//第一行文本的字体大小
                            rpbAqi.setSecondText(air_qualityBean.getNow().getAqi());//空气质量值
                            rpbAqi.setSecondTextSize(64f);//第二行文本的字体大小
                            rpbAqi.setMinText("0");
                            rpbAqi.setMinTextColor(getColor(R.color.arc_progress_color));

                            tvAirInfo.setText("空气" + air_qualityBean.getNow().getCategory());

                            tvPm10.setText(air_qualityBean.getNow().getPm10());//PM10
                            tvPm25.setText(air_qualityBean.getNow().getPm2p5());//PM2.5
                            tvNo2.setText(air_qualityBean.getNow().getNo2());//二氧化氮
                            tvSo2.setText(air_qualityBean.getNow().getSo2());//二氧化硫
                            tvO3.setText(air_qualityBean.getNow().getO3());//臭氧
                            tvCo.setText(air_qualityBean.getNow().getCo());//一氧化碳
                        } else {
                            ToastUtils.showShortToast(MainActivity.this, "错误");
                        }
                    }
                });
            }
        });
    }

    //获取小时天气
    public void HourlyWeatherFill(String cityId) {
        String NowUrl = Constant.HOURLY_URL + "location=" + cityId + "&key=" + Constant.API_KEY;

        HttpUtils.sendOkHttpRequest(NowUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showShortToast(MainActivity.this, "错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        HourlyWeatherBean hourlyWeatherBean = null;
                        if (responseData != null && !responseData.isEmpty()) {
                            hourlyWeatherBean = gson.fromJson(responseData, HourlyWeatherBean.class);
                            List<HourlyWeatherBean.HourlyBean> data = hourlyWeatherBean.getHourly();
                            if (data != null && data.size() > 0) {
                                hourlyList.clear();
                                hourlyList.addAll(data);
                                HourlyAdapter hourlyAdapter = new HourlyAdapter(MainActivity.this, R.layout.item_hourly_list, hourlyList);
                                LinearLayoutManager managerHourly = new LinearLayoutManager(MainActivity.this);
                                rvHourly.setHasFixedSize(true);//设置固定大小
                                rvHourly.setItemAnimator(new DefaultItemAnimator());//设置默认动画
                                managerHourly.setOrientation(RecyclerView.HORIZONTAL);//设置列表为横向
                                rvHourly.setLayoutManager(managerHourly);
                                rvHourly.setAdapter(hourlyAdapter);
                            }
                        } else {
                            ToastUtils.showShortToast(MainActivity.this, "错误");
                        }
                    }
                });
            }
        });
    }

    //获取7天天气预报
    public void DailyWeather_7D_Fill(String cityId) {
        String NowUrl = Constant.DAILY_WEATHER_7D_URL + "location=" + cityId + "&key=" + Constant.API_KEY;

        HttpUtils.sendOkHttpRequest(NowUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showShortToast(MainActivity.this, "错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                SQLDateCope(tvTitle.getText().toString(),responseData);//缓存数据库
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        DailyWeatherBean dailyWeatherBean = null;
                        if (responseData != null && !responseData.isEmpty()) {
                            dailyWeatherBean = gson.fromJson(responseData, DailyWeatherBean.class);
                            List<DailyWeatherBean.DailyBean> data = dailyWeatherBean.getDaily();
                            if (data != null && data.size() > 0) {
                                tvTempHeight.setText(data.get(0).getTempMax() + "℃");
                                tvTempLow.setText(" / " + data.get(0).getTempMin() + "℃");

//                                String tempMaxMin = "今日最高温：" + data.get(0).getTempMax() + "度，" + "最低温：" + data.get(0).getTempMin() + "度。";
                                //添加数据之前先清除
                                dailyList_7D.clear();
                                //添加数据
                                dailyList_7D.addAll(data);
                                Daily_7D_Adapter daily_7D_adapter = new Daily_7D_Adapter(MainActivity.this, R.layout.item_7d_weather_list, dailyList_7D);
                                LinearLayoutManager managerHourly = new LinearLayoutManager(MainActivity.this);
                                rv.setLayoutManager(managerHourly);
                                rv.setAdapter(daily_7D_adapter);
                            }
                        } else {
                            ToastUtils.showShortToast(MainActivity.this, "错误");
                        }
                    }
                });
            }
        });
    }

    //获取生活建议
    public void LifeStyle(String cityId) {
        String NowUrl = Constant.LIFESTYLE_URL + "location=" + cityId + "&key=" + Constant.API_KEY;
        HttpUtils.sendOkHttpRequest(NowUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Gson gson = new Gson();
                LifeStyleBean lifeStyleBean = null;
                if (responseData != null && !responseData.isEmpty()) {
                    lifeStyleBean = gson.fromJson(responseData, LifeStyleBean.class);
                    data = lifeStyleBean.getDaily();
                } else {
                    ToastUtils.showShortToast(MainActivity.this, "错误");
                }
            }
        });
    }


    private void showPopupWindow() {
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popuwidoew_setting, null);
        mPopupWindow = new PopupWindow(contentView);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x0000));// 设置pop透明效果
        mPopupWindow.setFocusable(true);// 设置pop获取焦点，如果为false点击返回按钮会退出当前Activity，如果pop中有Editor的话，focusable必须要为true
        mPopupWindow.setTouchable(true);// 设置pop可点击，为false点击事件无效，默认为true
        mPopupWindow.setOutsideTouchable(true);// 设置点击pop外侧消失，默认为false；在focusable为true时点击外侧始终消失

        TextView aboutUs = (TextView) contentView.findViewById(R.id.tv_about_us);//关于我们
        TextView search = contentView.findViewById(R.id.tv_city_search);//城市搜索

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
                mPopupWindow.dismiss();
                finish();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(MainActivity.this, CitySearchActivity.class);
                startActivity(searchIntent);
                mPopupWindow.dismiss();
                finish();
            }
        });

        mPopupWindow.showAsDropDown(imageView);
    }
    /*
    * 退出应用的提示
    * */
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
}