package com.jooksu.kobusp.pocketweather.api;

/**
 * Created by kobusp on 2017/10/06.
 * Interface for network calls success
 */
public interface OnSuccessCallback<T> {
    void onSuccess(T t);
}
