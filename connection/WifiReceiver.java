package com.brin.denonremotefree.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.brin.denonremotefree.binding.ReceiverListActivity;
import com.brin.denonremotefree.service.ReceiverConnectionService;

/**
 * Created by Luca on 13.10.2016.
 */

public class WifiReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("WifiReceiver", "onReceive: ");
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI)
        {
            ReceiverListActivity.onNetworkChanged(context, netInfo.isConnected());
        } else
        {
            ReceiverListActivity.onNetworkChanged(context, false);
            ReceiverConnectionService.disconnect();
        }
    }
}