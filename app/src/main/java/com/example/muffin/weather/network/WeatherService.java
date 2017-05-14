package com.example.muffin.weather.network;


import android.support.annotation.NonNull;

import com.example.muffin.weather.GsonModels.WeatherForecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {


    @GET("data/2.5/forecast/daily?")
    Call<WeatherForecast> getWeatherForecast(@NonNull @Query("q") String city,
                                             @Query("units") String units,
                                             @Query("cnt") String countDays);

    @GET("data/2.5/forecast/daily?")
    Call<WeatherForecast> getWeatherForecastByLocation(@NonNull @Query("lat") String lat,
                                                       @NonNull @Query("lon") String lon,
                                                       @Query("units") String units,
                                                       @Query("cnt") String countDays);



}
