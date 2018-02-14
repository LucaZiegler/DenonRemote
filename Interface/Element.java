package com.brin.denonremotefree.Interface;

/**
 * Created by Luca on 09.06.2016.
 */
public interface Element
{
    void onPause();
    void onResume();
    void updateStatus(int s);
    void showProgress(boolean v);
    void openBroadcast();
    void closeBroadcast();
    void onTelnetResult(String m);
    void onConnectionError(int err,String ip);
    void onBroadcastOpen(boolean isConn);
    void sendTelnetCom(String c);
}
