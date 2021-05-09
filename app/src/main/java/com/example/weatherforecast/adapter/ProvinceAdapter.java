package com.example.weatherforecast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.bean.CityListBean;

import java.util.List;

/**
 * @Description
 * @Author tang
 * @Date2021-04-24 16:53
 */
public class ProvinceAdapter extends BaseAdapter {

    private Context mContext;
    private List<CityListBean> cityListBean;
    private int resoureId;

    public ProvinceAdapter(Context mContext,int resoureId,List<CityListBean> cityListBean) {
        this.mContext = mContext;
        this.cityListBean = cityListBean;
        this.resoureId = resoureId;
    }

    @Override
    public int getCount() {
        return cityListBean.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(resoureId,parent,false);
        TextView CityName = view.findViewById(R.id.tv_onecity);
        CityListBean single_cityListBean = cityListBean.get(position);
        CityName.setText(single_cityListBean.getName());

        return view;
    }
}
