package com.jooksu.kobusp.pocketweather.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jooksu.kobusp.pocketweather.models.Weather.WeatherModel;

/**
 * Created by kobusp on 2017/10/06.
 * RestClient to get weather data
 */

public class RestClient {

    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?units=metric&appid=fff4bb02b667131f38d43b6a81c9c1aa";
    private static final String TAG = "RestClient";

    public static void getWeather(
            double longitude,
            double latitude,
            final Context context,
            final OnSuccessCallback<WeatherModel> successCallback,
            final OnErrorCallback errorCallback) {
        String url = BASE_URL + "&lat=" + latitude + "&lon=" + longitude;
        Log.d(TAG, "URL: " + url);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "Response: " + response);
                    Gson gson = new Gson();
                    WeatherModel weatherModelData = gson.fromJson(response, WeatherModel.class);

                    successCallback.onSuccess(weatherModelData);
                } catch (Exception e) {
                    errorCallback.onError("Unable to get weather");
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorCallback.onError("Unable to get weather");
                    }
                });

        RequestHelper.doCall(context, request, url);
    }
}
