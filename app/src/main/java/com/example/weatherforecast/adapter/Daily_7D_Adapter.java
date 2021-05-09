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
import com.example.weatherforecast.bean.HourlyWeatherBean;
import com.example.weatherforecast.utils.DateUtils;
import com.example.weatherforecast.utils.WeatherUtil;

import java.util.List;

/**
 * @Description 逐小时预报数据列表适配器
 * @Author tang
 * @Date2021-04-21 13:01
 */
public class Daily_7D_Adapter extends RecyclerView.Adapter<Daily_7D_Adapter.MyViewHoler> {
    private Context mContext;
    private List<DailyWeatherBean.DailyBean> dailyList_7d;
    private int resoureId;

    public Daily_7D_Adapter(Context mContext, int resoureId, List<DailyWeatherBean.DailyBean> dailyList_7d) {
        this.mContext = mContext;
        this.dailyList_7d = dailyList_7d;
        this.resoureId = resoureId;
    }

    @Override
    public void onBindViewHolder(MyViewHoler holder, int position) {

        //相当于listview的adapter中的getview方法
        //负责将数据绑定到视图上
        DailyWeatherBean.DailyBean dailyBean = dailyList_7d.get(position);

        holder.Time.setText(DateUtils.dateSplitPlus(dailyBean.getFxDate()) + DateUtils.Week(dailyBean.getFxDate()));

        holder.Height_Temperature.setText(dailyBean.getTempMax() + "℃");

        holder.Low_Temperature.setText(" / " + dailyBean.getTempMin() + "℃");

        //获取天气状态码，根据状态码来显示图标
        int code = Integer.parseInt(dailyBean.getIconDay());

        WeatherUtil.changeIcon(holder.weatherStateIcon, code);
    }

    @Override
    public MyViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        //负责创建视图
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_7d_weather_list,parent,false);

        return new MyViewHoler(view);
    }

    @Override
    public int getItemCount() {
        return dailyList_7d.size();
    }


    class MyViewHoler extends RecyclerView.ViewHolder
    {
        private final TextView Time;
        private final  TextView Height_Temperature;
        private final  TextView Low_Temperature;
        private final ImageView weatherStateIcon;

        public MyViewHoler(View itemView) {
            super(itemView);
            //时间
            Time = itemView.findViewById(R.id.tv_date);
            //最高温度
            Height_Temperature = itemView.findViewById(R.id.tv_temp_height);
            //最低温度
            Low_Temperature = itemView.findViewById(R.id.tv_temp_low);
            //天气状态图片
            weatherStateIcon = itemView.findViewById(R.id.iv_weather_state);

        }
    }


}