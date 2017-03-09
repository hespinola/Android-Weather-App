package com.example.isomi.weatherapp.Model;

import android.widget.ImageView;

/**
 * Created by Isomi on 3/6/17.
 */

public class Report {
    private int tempMin;
    private int tempMax;
    private String weather;
    private String city;
    private String country;
    private int date;


    public Report(int tempMin, int tempMax, String weather, String city, String country, int date) {
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.weather = weather;
        this.city = city;
        this.country = country;
        this.date = date;
    }

    public String getTempMin() {
        return String.valueOf(tempMin) + "°";
    }

    public String getTempMax() {
        return String.valueOf(tempMax) + "°";
    }

    public String getWeather() {
        return weather;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public int getDate() {
        return date;
    }
}
