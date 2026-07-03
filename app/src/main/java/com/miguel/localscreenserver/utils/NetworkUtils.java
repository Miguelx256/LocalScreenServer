package com.miguel.localscreenserver.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

public class NetworkUtils {

    public static String getIPAddress(Context context) {

        WifiManager wifiManager =
                (WifiManager) context.getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE);

        if (wifiManager == null)
            return "0.0.0.0";

        return Formatter.formatIpAddress(
                wifiManager.getConnectionInfo().getIpAddress()
        );
    }

}