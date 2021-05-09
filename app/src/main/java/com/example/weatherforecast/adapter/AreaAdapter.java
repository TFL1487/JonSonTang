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
public class AreaAdapter extends BaseAdapter {
    private Context mContext;
    private List<CityListBean.CityBean.AreaBean> areaBean;
    private int resoureId;

    public AreaAdapter(Context mContext,int resoureId,List<CityListBean.CityBean.AreaBean> areaBean) {
        this.mContext = mContext;
        this.areaBean = areaBean;
        this.resoureId = resoureId;
    }
    @Override
    public int getCount() {
        return areaBean.size();
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
        CityListBean.CityBean.AreaBean single_areaBean = areaBean.get(position);
        CityName.setText(single_areaBean.getName());

        return view;
    }
}
