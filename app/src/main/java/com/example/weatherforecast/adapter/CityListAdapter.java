package com.example.weatherforecast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.bean.CitySearchBean;

import java.util.List;

/**
 * @Description
 * @Author tang
 * @Date2021-04-17 1:25
 */
public class CityListAdapter extends BaseAdapter {
    private Context mContext;
    private List<CitySearchBean> CityList;
    private int resoureId;

    public CityListAdapter(Context mContext,int resoureId,List<CitySearchBean> CityList) {
        this.mContext = mContext;
        this.CityList = CityList;
        this.resoureId = resoureId;
    }
    @Override
    public int getCount() {
        return CityList.size();
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
        return null;
    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(resoureId,parent,false);

        TextView CityName = view.findViewById(R.id.c_name);
        TextView CityId = view.findViewById(R.id.c_id);

        City_All_Bean cityBean = CityList.get(position);

        CityName.setText(cityBean.getName());
        CityId.setText("id:"+cityBean.getCityCode());

        return view;
    }*/
}
