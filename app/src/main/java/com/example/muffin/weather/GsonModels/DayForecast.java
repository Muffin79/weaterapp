package com.example.muffin.weather.GsonModels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class DayForecast implements Serializable{

    private long dt;
    public double humidity;
    public Temperature temp;
    public List<Weather> weather;
    private double pressure;
    @SerializedName("speed")
    private double windSpeed;
    private double deg;


    public String getIconUrl(){
        String iconUrl = "http://openweathermap.org/img/w/" + weather.get(0).getIcon() + ".png";
        return iconUrl;
    }

    public String getDayOfWeek(){
        return convertTimeStampToDay(dt);
    }

    private String convertTimeStampToDay(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp * 1000);
        TimeZone timeZone = TimeZone.getDefault();

        //Задаем поправку на часовой пояс
        calendar.add(Calendar.MILLISECOND,
                timeZone.getOffset(calendar.getTimeInMillis()));

        //Будет форматировать дату до названия дня недели
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM");

        return dateFormat.format(calendar.getTime());
    }
   /* private String getWindDirection(){
        switch ((int)deg){
            default:
                return "";
        }
    }*/
    public String getWindSpeed(){
        return windSpeed + " m/s";
    }

    public String getPressure(){
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        return numberFormat.format(pressure * 0.75) + " mm";
    }



}

