package com.brin.denonremotefree.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Luca on 18.01.2016.
 */
public class Helper {
    public static boolean isConnected(Context c) {
        ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo networkInfoVpn = null;
        Boolean connectedVpn = false;
        if (android.os.Build.VERSION.SDK_INT >= 22) {
            networkInfoVpn = connMgr.getNetworkInfo(ConnectivityManager.TYPE_VPN);
            connectedVpn = networkInfo != null && networkInfoVpn.isConnected();
        }
        Boolean connected = networkInfo != null && networkInfo.isConnected();
        if(!connected){
            if(connectedVpn){
                connected = true;
            }
        }
        return connected;
    }
    public static boolean validIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }
            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }
            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            return !ip.endsWith(".");
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
