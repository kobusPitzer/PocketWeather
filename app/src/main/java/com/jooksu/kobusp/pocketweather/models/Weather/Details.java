package com.jooksu.kobusp.pocketweather.models.Weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kobusp on 2017/10/06.
 * Model that will hold specific details
 */

public class Details{

    @SerializedName(value="temperature", alternate={"temp"})
    private float temperature;
    private double pressure;
    private double humidity;
    @SerializedName(value="minTemperature", alternate={"temp_min"})
    private float minTemperature;
    @SerializedName(value="maxTemperature", alternate={"temp_max"})
    private float maxTemperature;

    public float getTemperature() {
        return temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public float getMinTemperature() {
        return minTemperature;
    }

    public float getMaxTemperature() {
        return maxTemperature;
    }
}
