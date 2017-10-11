package com.jooksu.kobusp.pocketweather2.api;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.jooksu.kobusp.pocketweather2.constants.NETWORK;

/**
 * Created by kobusp on 2017/10/06.
 * Create a request to help Volley request queues.
 */

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
