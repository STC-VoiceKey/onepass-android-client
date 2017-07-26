package com.speechpro.onepass.framework.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

/**
 * Created by grigal on 22.06.2017.
 */

public class Network {

    private static final String TAG = Network.class.getSimpleName();

    public static boolean isAvailable (Context ctx) {
        boolean status = false;
        try {
            ConnectivityManager cm      = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(TYPE_MOBILE);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(TYPE_WIFI);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    status = true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Connection failed", e);
            return false;
        }
        return status;
    }

}
