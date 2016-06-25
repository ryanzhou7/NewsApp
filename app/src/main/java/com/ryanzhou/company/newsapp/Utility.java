package com.ryanzhou.company.newsapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ryanzhou on 6/24/16.
 */
public class Utility {

    public static boolean isNetworkAvailable(Context c) {
        /* snippet from
            https://github.com/udacity/Advanced_Android_Development/blob/2.02_Are_We_Offline/
         */
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}