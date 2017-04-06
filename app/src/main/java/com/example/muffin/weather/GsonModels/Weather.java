package com.example.muffin.weather.GsonModels;


import java.io.Serializable;

public class Weather implements Serializable {

    public String description;
    private String icon;

    public String getIcon() {
        return icon;
    }
}
