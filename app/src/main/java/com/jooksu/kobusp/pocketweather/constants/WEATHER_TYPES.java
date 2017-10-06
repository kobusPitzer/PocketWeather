package com.jooksu.kobusp.pocketweather.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Weather types
 */

@IntDef({WEATHER_TYPES.Thunderstorm,
        WEATHER_TYPES.Drizzle,
        WEATHER_TYPES.Rain,
        WEATHER_TYPES.Snow,
        WEATHER_TYPES.Atmosphere,
        WEATHER_TYPES.Clouds,
        WEATHER_TYPES.Clear}
)

@Retention(RetentionPolicy.SOURCE)
public @interface WEATHER_TYPES{
    int Thunderstorm = 2;
    int Drizzle = 3;
    int Rain = 5;
    int Snow = 6;
    int Atmosphere = 7;
    int Clouds = 8;
    int Clear = 800;
}
