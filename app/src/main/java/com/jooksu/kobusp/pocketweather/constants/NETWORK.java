package com.jooksu.kobusp.pocketweather.constants;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Constants used by network functions
 */
public class NETWORK {
    public static final int RETRY_TIMEOUT_MS =  5000;
    public static final int RETRY_MAX_RETRIES = 1;


    @StringDef({NETWORK_EXCEPTION_TYPE.success,
            NETWORK_EXCEPTION_TYPE.error,
    }
    )
    @Retention(RetentionPolicy.SOURCE)
    public @interface NETWORK_EXCEPTION_TYPE {
        String success ="success";
        String error = "error";
    }
}
