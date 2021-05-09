package com.example.weatherforecast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherforecast.R;
import com.example.weatherforecast.bean.HourlyWeatherBean;
import com.example.weatherforecast.utils.DateUtils;
import com.example.weatherforecast.utils.WeatherUtil;

import java.util.List;

/**
 * @Description 逐小时预报数据列表适配器
 * @Author tang
 * @Date2021-04-21 13:01
 */
public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.MyViewHoler> {
    private Context mContext;
    private List<HourlyWeatherBean.HourlyBean> hourlyList;
    private int resoureId;

    public HourlyAdapter(Context mContext,int resoureId,List<HourlyWeatherBean.HourlyBean> hourlyList) {
        this.mContext = mContext;
        this.hourlyList = hourlyList;
        this.resoureId = resoureId;
    }

    @Override
    public void onBindViewHolder(MyViewHoler holder, int position) {

        //相当于listview的adapter中的getview方法
        //负责将数据绑定到视图上
        HourlyWeatherBean.HourlyBean hourlyBean = hourlyList.get(position);
        String time = DateUtils.updateTime(hourlyBean.getFxTime());

        holder.Time.setText(WeatherUtil.showTimeInfo(time) + time);
        holder.Temperature.setText(hourlyBean.getTemp() + "℃");

        //获取天气状态码，根据状态码来显示图标
        int code = Integer.parseInt(hourlyBean.getIcon());

        WeatherUtil.changeIcon(holder.weatherStateIcon, code);
    }

    @Override
    public MyViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        //负责创建视图
        View view= LayoutInflater.from(mContext).inflate(resoureId,null);

        return new MyViewHoler(view);
    }

    @Override
    public int getItemCount() {
        return hourlyList.size();
    }


    class MyViewHoler extends RecyclerView.ViewHolder
    {
        private final TextView Time;
        private final TextView Temperature;
        private final ImageView weatherStateIcon;

        public MyViewHoler(View itemView) {
            super(itemView);
            //时间
             Time = itemView.findViewById(R.id.tv_time);
            //温度
             Temperature = itemView.findViewById(R.id.tv_temperature);
            //天气状态图片
             weatherStateIcon = itemView.findViewById(R.id.iv_weather_state);

        }
    }


}