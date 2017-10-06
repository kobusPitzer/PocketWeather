package com.jooksu.kobusp.pocketweather.models.Weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kobusp on 2017/10/06.
 * Model that will hold weather data
 */

public class WeatherModel{

    private String base;
    private String name;

    private int id;
    @SerializedName(value="datetime", alternate={"dt"})
    private long datetime;
    @SerializedName(value="responseCode", alternate={"cod"})
    private int responseCode;

    @SerializedName(value="coordinates", alternate={"coord"})
    Coordinates coordinates;

    @SerializedName(value="weatherInformation", alternate={"weather"})
    WeatherInformation[] weatherInformation;

    @SerializedName(value="locationWeatherDetails", alternate={"main"})
    Details locationWeatherDetails;

    @SerializedName(value="locationInformation", alternate={"sys"})
    LocationInformation locationInformation;

    public String getBase() {
        return base;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public long getDatetime() {
        return datetime;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public WeatherInformation[] getWeatherInformation() {
        return weatherInformation;
    }

    public Details getLocationWeatherDetails() {
        return locationWeatherDetails;
    }

    public LocationInformation getLocationInformation() {
        return locationInformation;
    }
}
