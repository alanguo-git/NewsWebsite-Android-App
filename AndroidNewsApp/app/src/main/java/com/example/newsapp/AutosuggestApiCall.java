package com.example.newsapp;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AutosuggestApiCall {
    private static AutosuggestApiCall singleInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    public AutosuggestApiCall(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized AutosuggestApiCall getInstance(Context context){
        if(singleInstance == null){
            singleInstance = new AutosuggestApiCall(context);
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
        String url = "https://api.cognitive.microsoft.com/bing/v7.0/suggestions?q=" + query;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                //put Bing Autosuggest API key into headers
                headers.put("Ocp-Apim-Subscription-Key", "Your API key");
                return headers;
            }
        };
        AutosuggestApiCall.getInstance(ctx).addToRequestQueue(stringRequest);
    }
}
