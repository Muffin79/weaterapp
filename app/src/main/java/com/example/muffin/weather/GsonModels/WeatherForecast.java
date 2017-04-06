package com.example.muffin.weather.GsonModels;

import java.io.Serializable;
import java.util.List;

public class WeatherForecast implements Serializable{

    public List<DayForecast> list;
    private City city;

    public City getCity() {
        return city;
    }
}
