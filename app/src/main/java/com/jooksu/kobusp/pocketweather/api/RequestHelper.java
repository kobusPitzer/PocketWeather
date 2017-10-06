package com.jooksu.kobusp.pocketweather.api;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.jooksu.kobusp.pocketweather.constants.NETWORK;

class RequestHelper {
    static void doCall(Context context, Request request, String tag) {

        request.setRetryPolicy(getRetryPolicy());
        request.setTag(tag);
        VolleySingleton.getInstance().getRequestQueue(context).cancelAll(tag);
        VolleySingleton.getInstance().addToRequestQueue(context, request);
    }


    static DefaultRetryPolicy getRetryPolicy() {
        return new DefaultRetryPolicy(
                NETWORK.RETRY_TIMEOUT_MS,
                NETWORK.RETRY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }
}
