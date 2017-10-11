package com.jooksu.kobusp.pocketweather2.models.Weather;

/**
 * Created by kobusp on 2017/10/06.
 * Model that will hold location info
 */

public class LocationInformation{
    private int type;
    private int id;
    private float message;
    private String country;
    private long sunrise;
    private long sunset;

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public float getMessage() {
        return message;
    }

    public String getCountry() {
        return country;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }
}
