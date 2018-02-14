package com.brin.denonremotefree.dev;

/**
 * Created by Luca on 19.02.2016.
 */
public interface TelnetServiceInterface {
    void loadCom(String l);
    void openBroadcast();
    void closeBroadcast();
    android.os.Handler handler = new android.os.Handler();
    String TAG = "";
    void onConnectionLost();
    void onBroadcastOpen();
    void fetchTelnetLine(String l);
    void importGAnalytics();
}
