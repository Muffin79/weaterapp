package com.example.muffin.weather;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.example.muffin.weather.GsonModels.WeatherForecast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;





public class GetWeatherTask extends AsyncTask<URL,Void,WeatherForecast> {
    private View parentView;

    public GetWeatherTask(View parentView){
        this.parentView = parentView;
    }


    private final String TAG = "GetWeatherTask";

    @Override
    protected WeatherForecast doInBackground(URL... params) {
        HttpURLConnection connection = null;
        Gson gson = new GsonBuilder().create();

        try{
            connection = (HttpURLConnection) params[0].openConnection();
            int response = connection.getResponseCode();

            if(response == HttpURLConnection.HTTP_OK){
                StringBuilder builder = new StringBuilder();

                try(BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))){
                    String line;

                    while((line = reader.readLine()) != null){
                        builder.append(line);
                    }

                }catch (IOException e) {
                    Snackbar.make(parentView,
                            R.string.read_error,Snackbar.LENGTH_SHORT).show();
                }
                WeatherForecast forecast = gson.fromJson(builder.toString(),
                        WeatherForecast.class);
                Log.d(TAG,"JSON String: "+ builder.toString());
                return forecast;
            }else{
                Snackbar.make(parentView,
                        R.string.connect_error,Snackbar.LENGTH_SHORT).show();
            }

        }catch(Exception e){
            Log.e(TAG,Log.getStackTraceString(e));
            Snackbar.make(parentView,
                    R.string.connect_error,Snackbar.LENGTH_SHORT).show();
        }finally {
            connection.disconnect();
        }

        return null;
    }


}
