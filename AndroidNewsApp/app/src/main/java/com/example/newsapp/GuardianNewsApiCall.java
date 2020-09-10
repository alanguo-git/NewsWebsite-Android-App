package com.example.newsapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class GuardianNewsApiCall {
    private static GuardianNewsApiCall singleInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;

    public GuardianNewsApiCall(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized GuardianNewsApiCall getInstance(Context context){
        if(singleInstance == null){
            singleInstance = new GuardianNewsApiCall(context);
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
        String url = "Your backend url/Home";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        GuardianNewsApiCall.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    public static void getDetail(Context ctx, String query, Response.Listener<String>
            listener, Response.ErrorListener errorListener){
        String url = "Your backend url/Article?id=" + query;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        GuardianNewsApiCall.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    public static void getSectionNews(Context ctx, String query, Response.Listener<String>
            listener, Response.ErrorListener errorListener){
        String url = "Your backend url/" + query;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        GuardianNewsApiCall.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    public static void getSearchNews(Context ctx, String query, Response.Listener<String>
            listener, Response.ErrorListener errorListener){
        String url = "Your backend url/Search?keyword=" + query;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        GuardianNewsApiCall.getInstance(ctx).addToRequestQueue(stringRequest);
    }
}
