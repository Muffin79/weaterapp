package com.example.muffin.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.content.Loader;


import com.example.muffin.weather.GsonModels.WeatherForecast;
import com.example.muffin.weather.network.ApiFactory;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WeatherLoader extends Loader<WeatherForecast>{


    private Call<WeatherForecast> mCall;
    private WeatherForecast mForecast;

    private String mTempUnits;
    private String mCountOfDays;


    public WeatherLoader(Context context,String city){
        super(context);
        getPrefs(context);
        mCall = ApiFactory.getWeatherService().getWeatherForecast(city,mTempUnits,mCountOfDays);
    }

    public WeatherLoader(Context context,Location location){
        super(context);
        getPrefs(context);
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());
        mCall = ApiFactory.getWeatherService()
                .getWeatherForecastByLocation(lat,lon,mTempUnits,mCountOfDays);
    }

    private void getPrefs(Context context){
        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mTempUnits = defaultPreferences
                .getString(context.getString(R.string.pref_temp_units), "");
        mCountOfDays = defaultPreferences
                .getString(context.getString(R.string.pref_countOfDays), "");
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if(mForecast != null){
            deliverResult(mForecast);
        }else{
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        mCall.cancel();
        super.onStopLoading();
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
        mCall.enqueue(new Callback<WeatherForecast>() {
            @Override
            public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
                mForecast = response.body();
                deliverResult(mForecast);
            }

            @Override
            public void onFailure(Call<WeatherForecast> call, Throwable t) {
                deliverResult(null);
            }
        });
    }

}
