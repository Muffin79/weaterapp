package com.example.muffin.weather;


import com.example.muffin.weather.GsonModels.WeatherForecast;

public interface PostExecuteCallback {

    void postExecute(WeatherForecast forecast);
}
