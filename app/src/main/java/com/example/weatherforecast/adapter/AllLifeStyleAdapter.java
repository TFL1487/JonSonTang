package com.example.weatherforecast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.bean.LifeStyleBean;

import java.util.List;

/**
 * @Description
 * @Author tang
 * @Date2021-05-01 0:38
 */
public class AllLifeStyleAdapter extends RecyclerView.Adapter<AllLifeStyleAdapter.MyViewHoler>{
    private Context mContext;
    private List<LifeStyleBean.DailyBean> dailyBeans;
    private int resoureId;

    public AllLifeStyleAdapter(Context mContext, int resoureId, List<LifeStyleBean.DailyBean> dailyBeans) {
        this.mContext = mContext;
        this.dailyBeans = dailyBeans;
        this.resoureId = resoureId;
    }

    @NonNull
    @Override
    public AllLifeStyleAdapter.MyViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //负责创建视图
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_more_lifestyle_list,parent,false);

        return new AllLifeStyleAdapter.MyViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllLifeStyleAdapter.MyViewHoler holder, int position) {
        LifeStyleBean.DailyBean dailyBean = dailyBeans.get(position);
        holder.name.setText(dailyBean.getName());
        holder.content.setText("生活建议：" + dailyBean.getText());
        String type = dailyBean.getType();
        int level = Integer.parseInt(dailyBean.getLevel());
        //根据不同的类型设置不同的最大进度
        switch (type) {
            //运动指数	1	适宜(1)、较适宜(2)、较不宜(3)
            case "1":
                //钓鱼指数	4	适宜(1)、较适宜(2)、不宜(3)
            case "4":
                holder.progressBar.setMax(3);
                break;
            //洗车指数	2	适宜(1)、较适宜(2)、较不宜(3)、不宜(4)
            case "2":
                //感冒指数	9	少发(1)、较易发(2)、易发(3)、极易发(4)
            case "9":
                //空调开启指数	11	长时间开启(1)、部分时间开启(2)、较少开启(3)、开启制暖空调(4)
            case "11":
                holder.progressBar.setMax(4);
                break;
            //紫外线指数	5	最弱(1)、弱(2)、中等(3)、强(4)、很强(5)
            case "5":
                //旅游指数	6	适宜(1)、较适宜(2)、一般(3)、较不宜(4)、不适宜(5)
            case "6":
                //花粉过敏指数	7	极不易发(1)、不易发(2)、较易发(3)、易发(4)、极易发(5)
            case "7":
                //空气污染扩散条件指数	10	优(1)、良(2)、中(3)、较差(4)、很差(5)
            case "10":
                //太阳镜指数	12	不需要(1)、需要(2)、必要(3)、很必要(4)、非常必要(5)
            case "12":
                //交通指数	15	良好(1)、较好(2)、一般(3)、较差(4)、很差(5)
            case "15":
                //防晒指数	16	弱(1)、较弱(2)、中等(3)、强(4)、极强(5)
            case "16":
                holder.progressBar.setMax(5);
                break;
            //晾晒指数	14	极适宜(1)、适宜(2)、基本适宜(3)、不太适宜(4)、不宜(5)、不适宜(6)
            case "14":
                holder.progressBar.setMax(6);
                break;
            //穿衣指数	3	寒冷(1)、冷(2)、较冷(3)、较舒适(4)、舒适(5)、热(6)、炎热(7)
            case "3":
                //舒适度指数	8	舒适(1)、较舒适(2)、较不舒适(3)、很不舒适(4)、极不舒适(5)、不舒适(6)、非常不舒适(7)
            case "8":
                holder.progressBar.setMax(7);
                break;
            //化妆指数	13	保湿(1)、保湿防晒(2)、去油防晒(3)、防脱水防晒(4)、去油(5)、防脱水(6)、防晒(7)、滋润保湿(8)
            case "13":
                holder.progressBar.setMax(8);
                break;
            default:
                holder.progressBar.setMax(0);
                break;
        }
        //当前等级，设置进度
        holder.progressBar.setProgress(level);
    }

    @Override
    public int getItemCount() {
        return dailyBeans.size();
    }

    class MyViewHoler extends RecyclerView.ViewHolder
    {
        private final TextView name;
        private final TextView content;
        private final ProgressBar progressBar;

        public MyViewHoler(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            content = itemView.findViewById(R.id.tv_content);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
