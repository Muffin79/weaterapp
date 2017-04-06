package com.example.muffin.weather.GsonModels;


import java.io.Serializable;

public class City implements Serializable {

    private String name;
    private String country;

    public String getCityName(){
        return name + "," + country;
    }
}
