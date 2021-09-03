package com.learning.leap.bwb.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ryangunn on 2/18/17.
 */

public class NetworkChecker implements NetworkCheckerInterface {
    private Context context;

    public NetworkChecker(Context context) {
        this.context = context;
    }

    @Override
    public Boolean isConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
