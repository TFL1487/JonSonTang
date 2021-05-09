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
 * @Date2021-04-24 16:54
 */
public class CityAdapter extends BaseAdapter {
    private Context mContext;
    private List<CityListBean.CityBean> cityBean;
    private int resoureId;

    public CityAdapter(Context mContext,int resoureId,List<CityListBean.CityBean> cityBean) {
        this.mContext = mContext;
        this.cityBean = cityBean;
        this.resoureId = resoureId;
    }
    @Override
    public int getCount() {
        return cityBean.size();
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
        CityListBean.CityBean single_cityBean = cityBean.get(position);
        CityName.setText(single_cityBean.getName());

        return view;
    }
}
