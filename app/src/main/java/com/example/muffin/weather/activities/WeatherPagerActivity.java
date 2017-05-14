package com.example.muffin.weather.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;


import android.widget.Toolbar;

import com.example.muffin.weather.GsonModels.DayForecast;
import com.example.muffin.weather.GsonModels.WeatherForecast;
import com.example.muffin.weather.R;
import com.example.muffin.weather.fragments.WeatherFragment;


import java.util.ArrayList;
import java.util.List;



public class WeatherPagerActivity extends FragmentActivity {

    public static final String EXTRA_FORECAST = "com.example.muffin.weather.extra_forecast";
    public static final String EXTRA_POSITION = "com.example.muffin.weather.extra_position";

    public static Intent newIntent(Context context,WeatherForecast forecast,int position){
        Intent intent = new Intent(context,WeatherPagerActivity.class);
        intent.putExtra(EXTRA_FORECAST,forecast);
        intent.putExtra(EXTRA_POSITION,position);
        return intent;
    }

    List<DayForecast> forecasts = new ArrayList<>();
    ViewPager weatherPager;
    FragmentManager fm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.weatherPagerToolbar);
        toolbar.setNavigationIcon(getDrawable(R.mipmap.ic_launcher));
        setActionBar(toolbar);

        weatherPager = (ViewPager) findViewById(R.id.weatherViewPager);
        fm = getSupportFragmentManager();
        forecasts = ((WeatherForecast)getIntent().getSerializableExtra(EXTRA_FORECAST)).list;
        weatherPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                return WeatherFragment.newInstance(forecasts.get(position));
            }

            @Override
            public int getCount() {
                return forecasts.size();
            }
        });
        weatherPager.setCurrentItem(getIntent().getIntExtra(EXTRA_POSITION,0));
    }




}
