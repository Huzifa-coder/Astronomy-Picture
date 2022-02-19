package com.barmej.astronomypicture.networks;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.barmej.astronomypicture.R;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_URL = "https://api.nasa.gov/planetary/apod?";

    private static final String DATE_PARAM = "date";

    private static final String APP_ID_PARAM = "api_key";

    private static final Object LOCK = new Object();

    private static NetworkUtils sInstance;

    private Context mContext;

    private RequestQueue mRequestQueue;


    private NetworkUtils(Context context) {
        // getApplicationContext() is key, it keeps your application safe from leaking the
        // Activity or BroadcastReceiver if you pass it instead of application context
        mContext = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
    }//end of NetworkUtils

    public static synchronized NetworkUtils getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) sInstance = new NetworkUtils(context);
            }
        }
        return sInstance;
    }//end of NetworkUtils.getInstance

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }//end of getRequestQueue

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }//end of addToRequestQueue

    public void cancelRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }//end of cancelRequests


    public static URL getUrl(Context context, String date) {
        return buildUrl(context, date);
    }//end of getUrl(date)

    public static URL getUrl(Context context) {
        return buildUrl(context);
    }//end of getUrl()


    private static URL buildUrl(Context context) {
        Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
        Uri uri = uriBuilder
                .appendQueryParameter(APP_ID_PARAM, context.getString(R.string.api_key))
                .build();
        try {
            URL url = new URL(uri.toString());
            Log.v(TAG, "URL: " + url);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }//end of buildUrl

    private static URL buildUrl(Context context, String data) {
        Uri.Builder uriBuilder = Uri.parse(BASE_URL).buildUpon();
        Uri uri = uriBuilder
                .appendQueryParameter(APP_ID_PARAM, context.getString(R.string.api_key))
                .appendQueryParameter(DATE_PARAM, String.valueOf(data))
                .build();
        try {
            URL url = new URL(uri.toString());
            Log.v(TAG, "URL: " + url);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }//end of BuildUrl(date)
}
