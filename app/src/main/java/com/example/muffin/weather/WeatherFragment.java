package com.example.muffin.weather;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muffin.weather.GsonModels.DayForecast;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;


public class WeatherFragment extends Fragment {
    public static final String ARGS_DAY_FORECAST = "ARGS_DAY_FORECAST";

    private ImageView conditionImageView;
    private TextView dayTextView;
    private TextView lowTextView;
    private TextView hiTextView;
    private TextView humidityTextView;
    private TextView mornTextView;
    private TextView eveTextView;
    private TextView nightTextView;
    private TextView windTextView;
    private TextView pressureTextView;

    public static Fragment newInstance(DayForecast forecast){
        Bundle args = new Bundle();
        args.putSerializable(ARGS_DAY_FORECAST,forecast);
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weather,container,false);
        DayForecast forecast = (DayForecast) getArguments().getSerializable(ARGS_DAY_FORECAST);
        if(forecast == null){
            Snackbar.make(v,"Forecast loading error", BaseTransientBottomBar.LENGTH_SHORT);
            return v;
        }
        conditionImageView = (ImageView)
                v.findViewById(R.id.conditionImageView);
       /* Picasso.with(getActivity()).load(forecast.getIconUrl())
                .into(conditionImageView);*/
       loadImage(forecast.weather.get(0).getIcon());
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        dayTextView = (TextView) v.findViewById(R.id.dayTextView);
        dayTextView.setText(getString(R.string.day_description,forecast.getDayOfWeek(),
                forecast.weather.get(0).description));

        lowTextView = (TextView) v.findViewById(R.id.lowTextView);
        lowTextView.setText(getString(R.string.low_temp,forecast.temp.getMin()));

        hiTextView = (TextView) v.findViewById(R.id.hightTextView);
        hiTextView.setText(getString(R.string.high_temp,forecast.temp.getMax()));

        humidityTextView = (TextView) v.findViewById(R.id.humidityTextView);
        humidityTextView.setText(getString(R.string.humidity,
                NumberFormat.getPercentInstance().format(forecast.humidity / 100.0)));

        mornTextView = (TextView) v.findViewById(R.id.mornTextView);
        mornTextView.setText(getString(R.string.morning_temp,forecast.temp.getMorn()));

        eveTextView = (TextView) v.findViewById(R.id.eveningTextView);
        eveTextView.setText(getString(R.string.evening_temp,forecast.temp.getEve()));

        nightTextView = (TextView) v.findViewById(R.id.nightTextView);
        nightTextView.setText(getString(R.string.night_temp,forecast.temp.getNight()));

        windTextView = (TextView) v.findViewById(R.id.windTextView);
        windTextView.setText(getString(R.string.wind,forecast.getWindSpeed()));

        pressureTextView = (TextView) v.findViewById(R.id.pressureTextView);
        pressureTextView.setText(getString(R.string.pressure,forecast.getPressure()));


        return v;
    }

    void loadImage(String iconName){
        AssetManager assets = getActivity().getAssets();

        try(InputStream in = assets.open("forecast/" + iconName + ".png")){
            conditionImageView.setImageDrawable(Drawable.createFromStream(in,iconName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
