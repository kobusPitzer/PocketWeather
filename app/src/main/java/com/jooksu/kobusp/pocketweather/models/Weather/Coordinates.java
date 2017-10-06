package com.jooksu.kobusp.pocketweather.models.Weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kobusp on 2017/10/06.
 * Model that will hold coordinates
 */

public class Coordinates{

    @SerializedName(value="longitude", alternate={"lon"})
    private float longitude;
    @SerializedName(value="latitude", alternate={"lat"})
    private float latitude;

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }
}
