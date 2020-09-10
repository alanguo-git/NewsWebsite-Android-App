package com.example.newsapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class OpenWeatherApiCall {
    private static OpenWeatherApiCall singleInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;
    private static final String apiKey = "Your API key";

    public OpenWeatherApiCall(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized OpenWeatherApiCall getInstance(Context context){
        if(singleInstance == null){
            singleInstance = new OpenWeatherApiCall(context);
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
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + query
                +"&units=metric&appid=" + apiKey;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        OpenWeatherApiCall.getInstance(ctx).addToRequestQueue(stringRequest);
    }
}
