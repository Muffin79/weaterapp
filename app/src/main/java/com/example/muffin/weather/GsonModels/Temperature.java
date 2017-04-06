package com.example.muffin.weather.GsonModels;

import java.io.Serializable;
import java.text.NumberFormat;



public class Temperature implements Serializable{
    private double min;
    private double max;
    private double morn;
    private double eve;
    private double night;

    public String getMin() {
        return initNumberFormat().format(min) + "\u00B0";
    }

    public String getMax() {
        return initNumberFormat().format(max) + "\u00B0";
    }

    public String getMorn() {
        return initNumberFormat().format(morn) + "\u00B0";
    }

    public String getEve() {
        return initNumberFormat().format(eve) + "\u00B0";
    }

    public String getNight() {
        return initNumberFormat().format(night) + "\u00B0";
    }

    private NumberFormat initNumberFormat(){
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        return numberFormat;
    }
}
