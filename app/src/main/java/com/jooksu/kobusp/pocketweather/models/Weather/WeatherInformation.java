package com.jooksu.kobusp.pocketweather.models.Weather;
/**
 * Created by kobusp on 2017/10/06.
 * Model that will hold weather type information
 */

public class WeatherInformation {

    private int id;
    private String main;
    private String description;
    private String icon;

    public int getId() {
        return id;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}
