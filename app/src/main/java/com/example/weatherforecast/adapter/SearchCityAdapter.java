package com.example.weatherforecast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.bean.DailyWeatherBean;
import com.example.weatherforecast.bean.SearchCityBean;
import com.example.weatherforecast.utils.DateUtils;
import com.example.weatherforecast.utils.WeatherUtil;

import java.util.List;

/**
 * @Description
 * @Author tang
 * @Date2021-04-30 21:30
 */
public class SearchCityAdapter extends RecyclerView.Adapter<SearchCityAdapter.MyViewHoler> {
    private Context mContext;
    private List<SearchCityBean.LocationBean> locationBeans;
    private int resoureId;
    private OnRecycleViewItemClickListener mOnItemClickListener;

    public SearchCityAdapter(Context mContext, int resoureId, List<SearchCityBean.LocationBean> locationBean) {
        this.mContext = mContext;
        this.locationBeans = locationBean;
        this.resoureId = resoureId;
    }


    @NonNull
    @Override
    public MyViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //负责创建视图
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_city_list,parent,false);
        return new SearchCityAdapter.MyViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoler holder, int position) {
        SearchCityBean.LocationBean locationBean = locationBeans.get(position);
        holder.city_name.setText(locationBean.getName());
        // 如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.OnItemClick(holder.itemView, pos);
                }
            });
        }

    }



    @Override
    public int getItemCount() {
        return locationBeans.size();
    }


    public void setOnItemClickListener(OnRecycleViewItemClickListener listener)
    {
        this.mOnItemClickListener=listener;
    }

    public static interface OnRecycleViewItemClickListener{
        void OnItemClick(View view,int position);
    }



    class MyViewHoler extends RecyclerView.ViewHolder
    {
        private final TextView city_name;


        public MyViewHoler(View itemView) {
            super(itemView);
            city_name = itemView.findViewById(R.id.tv_city_name);

        }
    }



}
