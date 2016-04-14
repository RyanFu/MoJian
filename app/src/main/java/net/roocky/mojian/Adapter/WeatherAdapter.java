package net.roocky.mojian.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import net.roocky.mojian.R;

/**
 * Created by roocky on 04/11.
 * 编辑日记时选择天气Spinner的适配器
 */
public class WeatherAdapter extends android.widget.BaseAdapter {
    private Context context;
    private int[] idWeather = {
            R.drawable.weather_sun,
            R.drawable.weather_clouds,
            R.drawable.weather_clouds_with_rain,
            R.drawable.weather_clouds_with_snow};

    public WeatherAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        }
        ((ImageView)view.findViewById(R.id.iv_weather)).setImageResource(idWeather[position]);
        return view;
    }
}
