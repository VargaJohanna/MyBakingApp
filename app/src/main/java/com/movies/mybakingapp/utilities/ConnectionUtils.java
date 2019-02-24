package com.movies.mybakingapp.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.movies.mybakingapp.R;

public class ConnectionUtils {
    public static boolean isNetworkAvailable(Context context) {
        boolean isWifiUsed = false;
        boolean isMobileDataUsed = false;

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] activeNetworkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo ni : activeNetworkInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    isWifiUsed = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    isMobileDataUsed = true;
        }
        if(isMobileDataUsed && !isWifiUsed) {
            Toast.makeText(context, context.getString(R.string.mobile_data_used_message), Toast.LENGTH_SHORT).show();
        }
        return isWifiUsed || isMobileDataUsed;
    }
}
