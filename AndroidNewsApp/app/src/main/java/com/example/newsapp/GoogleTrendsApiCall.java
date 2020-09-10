package com.example.newsapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class GoogleTrendsApiCall {
    private static GoogleTrendsApiCall singleInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;

    public GoogleTrendsApiCall(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized GoogleTrendsApiCall getInstance(Context context){
        if(singleInstance == null){
            singleInstance = new GoogleTrendsApiCall(context);
        }
        return singleInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static void make(Context ctx, String query, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {
        String url = "Your backend url/google-trends?term=" + query;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        OpenWeatherApiCall.getInstance(ctx).addToRequestQueue(stringRequest);
    }
}
