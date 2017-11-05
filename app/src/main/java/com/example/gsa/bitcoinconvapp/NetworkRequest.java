package com.example.gsa.bitcoinconvapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by GSA on 10/21/2017.
 */
public class NetworkRequest {
    private static NetworkRequest mInstance;
    private RequestQueue mRequestQueue;
    private Context mCtx;

    private NetworkRequest (Context context) {
        mCtx =context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized NetworkRequest getInstance(Context context){
        if (mInstance == null) {
            mInstance = new NetworkRequest(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> reg) {
        getRequestQueue().add(reg);
    }

}
