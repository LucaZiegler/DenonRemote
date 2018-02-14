package com.brin.denonremotefree.BrinObj;

import android.os.StrictMode;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by Luca on 03.09.2016.
 */

public class BrinMulticast
{
    private final int multicastPort = 12345;
    private final String multicastIp = "230.1.1.1";
    private DatagramSocket udpSocket;
    private InetAddress multicastInetAddress;
    private final String TAG = "BRIN.MULTICAST";
    private boolean multicastAlive = false;

    public interface BrinMulticastListener
    {
        void onMessageReceived(BrinMulticast context, String msg);
        void onServerError();
        void onClientError();
    }

    public BrinMulticast(BrinMulticastListener listener)
    {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        openSocketBroadcast(listener);
    }

    private void openSocketBroadcast(final BrinMulticastListener listener)
    {
        try
        {
            udpSocket = new DatagramSocket();
            multicastInetAddress = InetAddress.getByName(multicastIp);
        }catch (Exception e)
        {
            Log.e(TAG, "openSocketBroadcast: MULTICAST SERVER ERROR", e);
            listener.onClientError();
        }
        new Thread(new Runnable()
        {
            public void run()
            {
                multicastAlive = true;
                MulticastSocket mcSocket = null;
                try
                {
                    if (multicastInetAddress == null)
                        multicastInetAddress = InetAddress.getByName(multicastIp);
                    mcSocket = new MulticastSocket(multicastPort);
                    mcSocket.joinGroup(multicastInetAddress);
                    DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                    Log.d(TAG, "openSocketBroadcast: OPEN");
                    while (multicastAlive)
                    {
                        mcSocket.receive(packet);
                        String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
                        if (!msg.equals(lastMulticastSent))
                        {
                            listener.onMessageReceived(BrinMulticast.this, msg);
                        }
                        if (packet == null)
                            mcSocket.close();
                    }
                } catch (Exception e)
                {
                    Log.e(TAG, "openSocketBroadcast: MULTICAST CLIENT ERROR", e);
                    e.printStackTrace();
                    multicastAlive = false;
                    listener.onServerError();
                } finally
                {
                    Log.e(TAG, "openSocketBroadcast: MULTICAST CLIENT CLOSED");
                    multicastAlive = false;
                    if (mcSocket != null)
                    {
                        try
                        {
                            mcSocket.close();
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    if (udpSocket != null)
                    {
                        udpSocket.close();
                    }
                }
            }
        }).start();
    }

    private String lastMulticastSent = "";
    public void sendMsg(String l)
    {
        try {
            lastMulticastSent = l;
            byte[] msg = l.getBytes();
            DatagramPacket packet = new DatagramPacket(msg, msg.length);
            packet.setAddress(multicastInetAddress);
            packet.setPort(multicastPort);
            udpSocket.send(packet);
            Log.d(TAG, "sendMulticastMessage: "+l);
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "sendMulticastMessage: MULTICAST NOT SENT", e);
        }
    }
}
