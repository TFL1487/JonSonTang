package com.example.weatherforecast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.bean.DailyWeatherBean;
import com.example.weatherforecast.utils.DateUtils;
import com.example.weatherforecast.utils.WeatherUtil;

import java.util.List;

/**
 * @Description
 * @Author tang
 * @Date2021-04-27 13:31
 */
public class Daily_15D_Adapter extends RecyclerView.Adapter<Daily_15D_Adapter.MyViewHoler> {
    private Context mContext;
    private List<DailyWeatherBean.DailyBean> dailyList_15d;
    private int resoureId;

    public Daily_15D_Adapter(Context mContext, int resoureId, List<DailyWeatherBean.DailyBean> dailyList_15d) {
        this.mContext = mContext;
        this.dailyList_15d = dailyList_15d;
        this.resoureId = resoureId;
    }

    @Override
    public void onBindViewHolder(MyViewHoler holder, int position) {
        //相当于listview的adapter中的getview方法
        //负责将数据绑定到视图上
        DailyWeatherBean.DailyBean dailyBean = dailyList_15d.get(position);

        holder.temp_max.setText(dailyBean.getTempMax() + "°");
        holder.temp_min.setText(dailyBean.getTempMin() + "°");
        holder.date_info.setText(DateUtils.Week(dailyBean.getFxDate()));
        holder.date.setText(DateUtils.dateSplit(dailyBean.getFxDate()));
        holder.weather_state_d.setText(dailyBean.getTextDay());
        holder.weather_state_n.setText(dailyBean.getTextNight());
        holder.wind_360_d.setText(dailyBean.getWind360Day() + "°");
        holder.wind_dir_d.setText(dailyBean.getWindDirDay());
        holder.wind_scale_d.setText(dailyBean.getWindScaleDay() + "级");
        holder.wind_speed_d.setText(dailyBean.getWindSpeedDay() + "km/h");
        holder.wind_360_n.setText(dailyBean.getWind360Night() + "°");
        holder.wind_dir_n.setText(dailyBean.getWindDirNight());
        holder.wind_scale_n.setText(dailyBean.getWindScaleNight() + "级");
        holder.wind_speed_n.setText(dailyBean.getWindSpeedNight() + "km/h");
        holder.cloud.setText(dailyBean.getCloud() + "%");
        holder.uvIndex.setText(uvIndexToString(dailyBean.getUvIndex()));
        holder.vis.setText(dailyBean.getVis() + "km");
        holder.precip.setText(dailyBean.getPrecip() + "mm");
        holder.humidity.setText(dailyBean.getHumidity() + "%");
        holder.pressure.setText(dailyBean.getPressure() + "hPa");

        //白天天气状态图片描述
        WeatherUtil.changeIcon(holder.iv_weather_state_d, Integer.parseInt(dailyBean.getIconDay()));
        //晚上天气状态图片描述
        WeatherUtil.changeIcon(holder.iv_weather_state_n, Integer.parseInt(dailyBean.getIconNight()));
    }

    @Override
    public MyViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        //负责创建视图
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_more_daily_list, parent, false);

        return new MyViewHoler(view);
    }

    @Override
    public int getItemCount() {
        return dailyList_15d.size();
    }


    class MyViewHoler extends RecyclerView.ViewHolder {
        private final TextView temp_max;
        private final TextView temp_min;
        private final TextView date_info;
        private final TextView date;
        private final ImageView iv_weather_state_d;
        private final ImageView iv_weather_state_n;
        private final TextView weather_state_d;
        private final TextView weather_state_n;
        private final TextView wind_360_d;
        private final TextView wind_dir_d;
        private final TextView wind_scale_d;
        private final TextView wind_speed_d;
        private final TextView wind_360_n;
        private final TextView wind_dir_n;
        private final TextView wind_scale_n;
        private final TextView wind_speed_n;
        private final TextView cloud;
        private final TextView uvIndex;
        private final TextView vis;
        private final TextView precip;
        private final TextView humidity;
        private final TextView pressure;


        public MyViewHoler(View itemView) {
            super(itemView);
            temp_max = itemView.findViewById(R.id.tv_temp_max);//最高温
            temp_min = itemView.findViewById(R.id.tv_temp_min);//最低温
            date_info = itemView.findViewById(R.id.tv_date_info);//日期描述
            date = itemView.findViewById(R.id.tv_date);//日期
            iv_weather_state_d = itemView.findViewById(R.id.iv_weather_state_d);//白天天气状况文字描述
            iv_weather_state_n = itemView.findViewById(R.id.iv_weather_state_n);//晚间天气状况文字描述
            weather_state_d = itemView.findViewById(R.id.tv_weather_state_d);//白天天气状况文字描述
            weather_state_n = itemView.findViewById(R.id.tv_weather_state_n);//晚间天气状况文字描述
            //白天风力信息
            wind_360_d = itemView.findViewById(R.id.tv_wind_360_d);
            wind_dir_d = itemView.findViewById(R.id.tv_wind_dir_d);
            wind_scale_d = itemView.findViewById(R.id.tv_wind_scale_d);
            wind_speed_d = itemView.findViewById(R.id.tv_wind_speed_d);
            //晚上风力信息
            wind_360_n = itemView.findViewById(R.id.tv_wind_360_n);
            wind_dir_n = itemView.findViewById(R.id.tv_wind_dir_n);
            wind_scale_n = itemView.findViewById(R.id.tv_wind_scale_n);
            wind_speed_n = itemView.findViewById(R.id.tv_wind_speed_n);
            cloud = itemView.findViewById(R.id.tv_cloud);//云量
            uvIndex = itemView.findViewById(R.id.tv_uvIndex);//紫外线
            vis = itemView.findViewById(R.id.tv_vis);//能见度
            precip = itemView.findViewById(R.id.tv_precip);//降水量
            humidity = itemView.findViewById(R.id.tv_humidity);//相对湿度
            pressure = itemView.findViewById(R.id.tv_pressure);//大气压强

        }
    }
    public String uvIndexToString(String code) {//最弱(1)、弱(2)、中等(3)、强(4)、很强(5)
        String result = null;
        switch (code) {
            case "1":
                result = "最弱";
                break;
            case "2":
                result = "弱";
                break;
            case "3":
                result = "中等";
                break;
            case "4":
                result = "强";
                break;
            case "5":
                result = "很强";
                break;
            default:
                result = "无紫外线";
                break;
        }
        return result;
    }
}


