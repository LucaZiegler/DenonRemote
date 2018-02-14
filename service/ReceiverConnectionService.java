package com.brin.denonremotefree.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.brin.denonremotefree.Helper.PlayerHelper;
import com.brin.denonremotefree.Helper.Prefs;
import com.brin.denonremotefree.Helper.StopApp;
import com.brin.denonremotefree.HomeControl.PlayerActivity;
import com.brin.denonremotefree.Interface.ReceiverConnectionInterface;
import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.BrinObj.BrinMulticast;
import com.brin.denonremotefree.BrinObj.ReceiverStored;
import com.brin.denonremotefree.BrinObj.ReceiverTools;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.ReturnClass;
import com.brin.denonremotefree.binding.ReceiverListActivity;
import com.brin.denonremotefree.db.coms;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReceiverConnectionService extends Service implements ReceiverConnectionInterface
{
    private static String TAG = "CONNECT.SERVICE.";

    public static Socket socket = null;
    private static PrintWriter printWriter;
    private static Handler handler = new Handler();
    public static boolean socketConnected = false, serviceActive = false, serviceConnecting = false, disconnectRequest = false, wasConnected = false;
    private static ReceiverStored rc = null;
    private static boolean connTest = false, multicastAlive = false;
    public static final int notifyIdConnected = 1, notifyIdPlayer = 2;
    public static boolean prefNotConEnabled = true, prefNotPlayEnabled = false;
    private static Prefs prefs;
    private static boolean nseLoop = false;

    public ReceiverConnectionService() {}

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "START.");
        serviceActive = true;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        boolean error = false;
        try
        {
            connTest = intent.getBooleanExtra("TEST", false);
        }
        catch (Exception e)
        {
            connTest = false;
        }
        disconnectRequest = connTest;
        try
        {
            rc = ReceiverTools.ReceiverStoredFromJson(intent.getStringExtra(BrinActivity.INTENT_RCR_OBJ));
            prefs = new Prefs(this, rc.storedTime);
            prefNotConEnabled = prefs.getGlobBool("switch_preference_4_0", true);
            prefNotPlayEnabled = prefs.getGlobBool("switch_preference_4_1", false) && prefs.isAppActivated();
        }
        catch (Exception e)
        {
            error = true;
        }
        if (rc == null) error = true;

        if (!error)
        {
            new BrinMulticast(new BrinMulticast.BrinMulticastListener()
            {
                @Override
                public void onMessageReceived(BrinMulticast cont, String msg)
                {
                    Log.d(TAG, "onMessageReceived: " + msg);
                    if (msg.startsWith("BRIN_ALL") || msg.startsWith("BRIN_USER_" + ReturnClass.getUserId(getApplicationContext())) || msg.startsWith("BRIN_RCR_" + rc.receiverHostName))
                    {
                        if (msg.endsWith("_DIS"))
                        {
                            cont.sendMsg(MessageFormat.format("BRIN_RCR_{0}_{1}", rc.receiverHostName, "DIS_ANSW"));
                            stopService(getApplicationContext());
                        }
                        if (msg.endsWith("_GET"))
                        {
                            cont.sendMsg(MessageFormat.format("BRIN_USER_{0}_{1}_GET_ANSW", ReturnClass.getUserId(getApplicationContext()), rc.receiverHostName));
                        }
                    }
                }

                @Override
                public void onServerError()
                {

                }

                @Override
                public void onClientError()
                {

                }
            });
            openSocket();
        } else
        {
            stopService(getApplicationContext());
            Log.e(TAG, "onStartCommand: INTENT ERROR!");
        }
        return super.onStartCommand(intent, flags, startId);
    }


    public static ReceiverStored getReceiverStored()
    {
        return rc;
    }

    private static int getExcpType(String s)
    {
        if (s.contains("ETIMEDOUT")) return CONN_EXCP_TIME;
        else if (s.contains("ECONNREFUSED")) return CONN_EXCP_REFUSE;
        else if (s.contains("ENETUNREACH") || s.contains("EHOSTUNREACH") || s.contains("Host is unresolved"))
            return CONN_EXCP_UNREACH;
        else if (s.contains("Socket closed")) return CONN_EXCP_CLOSED;
        else if (s.equals("test")) return CONN_EXCP_TEST;
        else if (s.equals("ECONNRESET")) return CONN_EXCP_PEER;
        else return CONN_EXCP_UNKNOWN;
    }

    /**
     * REGION QUEUE
     */

    private static boolean queueActive = false;
    private static ArrayList<String> alQueue = new ArrayList<>();
    private static Handler queueHandler = new Handler();
    private static final int queueIntervall = 100;

    public static void sendCom(final String c)
    {
        alQueue.add(c);
        if (!queueActive) queueStart();
    }

    private static Runnable queueRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (alQueue.size() > 0)
            {
                pushCom(alQueue.get(0));
                alQueue.remove(0);
            } else
            {
                queueStop();
            }
            if (queueActive)
            {
                queueRestart();
            }
        }
    };

    public static void pushCom(final String c)
    {
        try
        {
            printWriter.print(c + "\r");
            printWriter.flush();
            if (printWriter.checkError()) throw new Exception();
            Log.d(TAG, "SENDED (" + c + ")");
        }
        catch (Exception e)
        {
            disconnect();
            e.printStackTrace();
            Log.e(TAG, "NOT SENDED (" + c + ")");
        }
    }

    public static void queueStop()
    {
        queueActive = false;
        queueHandler.removeCallbacks(queueRunnable);
    }

    public static void queueStart()
    {
        queueActive = true;
        queueHandler.post(queueRunnable);
    }

    public static void queueRestart()
    {
        queueActive = true;
        queueHandler.postDelayed(queueRunnable, queueIntervall);
    }

    /**
     * ENDREGION
     */

    private void sendServiceBroadcast(final Integer id, final String msg)
    {
        Log.d(TAG, "sendServiceBroadcast: " + msg);
        handler.post(new Runnable()
        {
            public void run()
            {
                Intent i1 = new Intent(coms.serviceID);
                i1.putExtra("id", id);
                if (id == BROAD_CON_SUCC || id == BROAD_CON_DIS) i1.putExtra("ip", rc.receiverIp);
                if (id == BROAD_CON_DIS) i1.putExtra("err", getExcpType(msg));
                i1.putExtra("msg", msg);
                sendBroadcast(i1);
                Log.d(TAG, id.toString() + "/" + msg);
            }
        });
    }

    private boolean nseUpdate = true;
    private boolean nseActive = false;
    private boolean nseLastShort = false;
    private boolean nseLastNo = false;
    private int nseLast = -2;
    private String nseLOG = "";
    private String lastLine = "";
    private ArrayList<String> alNSE = new ArrayList<>();

    private void onTelnetResult(String l)
    {
        Log.d(TAG, "onTelnetResult: " + l);
        sendServiceBroadcast(BROAD_MSG, l);

        if (nseUpdate)
        {
            if (PlayerHelper.check(l) && !lastLine.equals(l))
            {
                Log.d(TAG, "EWKJHBGEW: " + l);
                int nseThis = -8;
                boolean nseThisNo = false;
                boolean nseThisShort = false;
                boolean fetchDone = false;
                try
                {
                    if (l.startsWith("NSE0"))
                    {
                        Log.d(TAG, "onTelnetResult: 0");
                        nseLOG = "";
                        nseActive = true;
                        nseLastShort = false;
                        nseLast = -1;
                        nseLastNo = false;
                        nseThis = 0;
                        alNSE.clear();
                    } else
                    {
                        Log.d(TAG, "onTelnetResult: 1");
                        if (!nseActive)
                        {
                            Log.d(TAG, "onTelnetResult: 2");
                            //throw new NotActiveException("Error 1");
                        }
                    }

                    Pattern r = Pattern.compile("NSE[0-8]");
                    Matcher m = r.matcher(l);

                    nseThisNo = !m.find();
                    if (!nseThisNo)
                    {
                        Log.d(TAG, "onTelnetResult: 3");

                        nseThisShort = l.length() <= 4;
                        if (nseThisShort) l += "";
                        if (nseThis != 0) nseThis = PlayerHelper.getNSEFromLine(l, true);
                        if ((nseThis - 1) == nseLast)
                        {
                            Log.d(TAG, "onTelnetResult: 4");
                            if (nseLOG.length() == 0) nseLOG = l;
                            else nseLOG += "§#" + l;
                            if (l.contains("NSE8"))
                            {
                                fetchDone = true;
                            }
                        } else
                        {
                            Log.d(TAG, "onTelnetResult: 5");
                            throw new IndexOutOfBoundsException(MessageFormat.format("ERROR NOT EQUAL: nseThis/{0} nseLast/{1}", (nseThis - 1), nseLast));
                        }
                    } else
                    {
                        nseThisShort = false;
                        nseThis = nseLast;
                        if (/*nseLastShort*/true)
                        {
                            Log.d(TAG, "onTelnetResult: 6");
                            nseLOG += l;
                            if (l.contains("NSE8"))
                            {
                                fetchDone = true;
                            }
                        } else
                        {
                            Log.d(TAG, "onTelnetResult: 7");
                            throw new InterruptedException("Error 2");
                        }
                    }
                    if (fetchDone)
                    {
                        Log.d(TAG, "onTelnetResult: 8");
                        nseLOG = nseLOG.replaceAll("NSE[0-8]", "");
                        String[] extrct = nseLOG.split("§#");

                        alNSE = new ArrayList<>();
                        alNSE.addAll(Arrays.asList(extrct));
                        onTelnetNseDone(alNSE);
                        alNSE.clear();
                        nseActive = false;
                    }
                }
                catch (Exception e)
                {
                    //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.e(TAG, "onTelnetResult: ", e);
                    //clearList("T", true);
                }
                Log.d(TAG, "onTelnetResult: NSELOG: " + nseLOG);
                // AT THE END
                nseLastNo = nseThisNo;
                nseLastShort = nseThisShort;
                nseLast = nseThis;
            }
            lastLine = l;
        }
    }

    private void onTelnetNseDone(ArrayList<String> alNSE)
    {
        if (alNSE.size() > 0)
        {
            Log.d(TAG, "onTelnetNseDone: ");
            showNotifyPlayer(alNSE, getApplicationContext());
            sendServiceBroadcast(BROAD_NSE_RES, new Gson().toJson(alNSE));
        } else
        {
            Log.e(TAG, "onTelnetNseDone: LIST SIZE IS NULL");
        }
    }

    public static void showNotifyPlayer(ArrayList<String> al, Context c)
    {
        final boolean b = al != null && al.get(0).toLowerCase().startsWith("now playing");
        if (!b || prefNotPlayEnabled)
        {
            NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            if (b)
            {
                Intent intentMed1 = new Intent(c, PlayerActivity.class).putExtra("notMed", 1);
                Intent intentMed2 = new Intent(c, PlayerActivity.class).putExtra("notMed", 2);
                //Intent intentMed3 = new Intent(c, PlayerActivity.class).putExtra("notMed", 3);
                Intent intentMed4 = new Intent(c, PlayerActivity.class).putExtra("notMed", 4);

                PendingIntent pendingMed1 = TaskStackBuilder.create(c).addNextIntent(intentMed1).getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent pendingMed2 = TaskStackBuilder.create(c).addNextIntent(intentMed2).getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                //PendingIntent pendingMed3 = TaskStackBuilder.create(c).addNextIntent(intentMed3).getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent pendingMed4 = TaskStackBuilder.create(c).addNextIntent(intentMed4).getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                final String content = MessageFormat.format("{1} - {0}", al.get(1).trim(), al.get(2).trim());
                final String title = MessageFormat.format("{1}", c.getString(R.string.app_name_long), al.get(0).trim().replaceAll("  ", " ")).toUpperCase();

                NotificationCompat.Builder nb = new NotificationCompat.Builder(c);
                nb.setSmallIcon(R.drawable.av_receiver_100_white);
                nb.setContentTitle(title);
                nb.setOngoing(true);
                nb.setStyle(new NotificationCompat.BigTextStyle().bigText(content));
                nb.setContentText(content);
                nb.setContentIntent(pendingMed1);
                nb.setColor(ContextCompat.getColor(c, R.color.primary_color));
                nb.setTicker(title);
                nb.setAutoCancel(true);
                nb.setPriority(1);

                nb.addAction(R.drawable.skip_back_100_white, null, pendingMed1);
                nb.addAction(R.drawable.stop_100_white, null, pendingMed2);
                //mBuilder.addAction(R.drawable.pause_100_white, null, pendingMed3);
                nb.addAction(R.drawable.skip_for_100_white, null, pendingMed4);

                nm.notify(notifyIdPlayer, nb.build());

                if (!nseLoop)
                {
                    nseLoop = true;
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            sendCom("NSE");
                            nseLoop = false;
                        }
                    }, 15000);
                }
            } else
            {
                nm.cancel(notifyIdPlayer);
            }
        }
    }

    private boolean errorHandled = false;

    private void openSocket()
    {
        if (serviceActive)
        {
            try
            {
                if (!socketConnected)
                {
                    new Thread(new Runnable()
                    {
                        public void run()
                        {
                            Exception exception = null;
                            boolean tstDis = false;
                            try
                            {
                                serviceConnecting = true;
                                InetSocketAddress address = new InetSocketAddress(rc.receiverIp, 23);
                                prefs.putDeviceHostname(address.getHostName());
                                Log.d(TAG, "CONNECTING WITH: " + rc.receiverIp);
                                sendServiceBroadcast(BROAD_CON_WAIT, null);
                                socket = new Socket();
                                socket.connect(address, 5000);
                                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                                serviceConnecting = false;
                                Log.d(TAG, "CONNECTED");
                                sendServiceBroadcast(BROAD_CON_SUCC, address.getHostName());
                                socketConnected = true;
                                wasConnected = true;
                                Log.d(TAG, "run: connTest" + connTest);
                                Log.d(TAG, "run: disconnectRequest" + disconnectRequest);
                                if (!connTest)
                                {
                                    showNotifyConnection(true, getApplicationContext());
                                    String str;
                                    while ((str = reader.readLine()) != null && !disconnectRequest)
                                    {
                                        onTelnetResult(str.trim());
                                    }
                                } else
                                {
                                    try
                                    {
                                        tstDis = true;
                                        socket.close();
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                        exception = e;
                                    } finally
                                    {
                                        socketConnected = false;
                                    }
                                }
                                socketConnected = false;
                            }
                            catch (Exception e)
                            {
                                exception = e;
                                e.printStackTrace();
                                if (connTest && tstDis)
                                {
                                    sendServiceBroadcast(BROAD_CON_DIS, "test");
                                } else
                                {
                                    sendServiceBroadcast(BROAD_CON_DIS, e.getLocalizedMessage());
                                }
                                errorHandled = true;
                                socketConnected = false;
                            }
                            showNotifyConnection(false, getApplicationContext());
                            serviceConnecting = false;

                            if (!errorHandled) sendServiceBroadcast(BROAD_CON_DIS, "DISCONNECTED");
                            errorHandled = true;
                            Log.e(TAG, wasConnected ? "DISCONNECTED" : "ERROR");
                            stopService(getApplicationContext());

                            Log.e(TAG, "openSocket: ", exception);
                            if (exception != null) Log.e(TAG, "run: " + exception.getMessage());
                        }
                    }).start();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                stopService(getApplicationContext());
            }
        } else
        {
            stopService(getApplicationContext());
        }
    }

    public static void showNotifyConnection(boolean b, Context c)
    {

        Log.d(TAG, "showNotification: " + b);
        if (!b || prefNotConEnabled)
        {
            NotificationManager nm = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            if (b)
            {
                Intent intentDis = new Intent(c, StopApp.class);
                Intent intentApp = new Intent(c, ReceiverListActivity.class);

                //intentDis.putExtra("DISCONNECT",false);

                TaskStackBuilder stackBuilderDis = TaskStackBuilder.create(c).addNextIntent(intentDis);
                TaskStackBuilder stackBuilderApp = TaskStackBuilder.create(c).addNextIntent(intentApp);

                PendingIntent pendingIntentDis = stackBuilderDis.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent pendingIntentApp = stackBuilderApp.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                final String text = MessageFormat.format(c.getString(R.string.connected_with_notifi), rc.receiverHostName);
                NotificationCompat.Builder nb = new NotificationCompat.Builder(c);
                nb.setSmallIcon(R.drawable.av_receiver_100_white);
                nb.setContentTitle(c.getString(R.string.app_name_long).toUpperCase());
                nb.setOngoing(true);
                nb.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
                nb.setContentText(text);
                nb.setContentIntent(pendingIntentApp);
                //mBuilder.addAction(R.drawable.abc_ic_ab_back_material, "open list", pendingIntentApp);
                nb.addAction(R.drawable.shutdown_100_white, c.getString(R.string.disconnect), pendingIntentDis);
                nb.setColor(ContextCompat.getColor(c, R.color.primary_color));
                nb.setPriority(2);

                nm.notify(notifyIdConnected, nb.build());

                if (prefNotPlayEnabled)
                    sendCom("NSE");
            } else
            {
                nm.cancel(notifyIdConnected);
                showNotifyPlayer(null, c);
            }
        }
    }

    public static void stopService(Context c)
    {
        //showNotification(false,getApplicationContext());
        Log.d(TAG, "stopService");
        //ReceiverConnectionService.this.stopSelf();
        showNotifyConnection(false, c);
        c.stopService(new Intent(c, ReceiverConnectionService.class));
    }

    @Override
    public void onDestroy()
    {
        showNotifyConnection(false, getApplicationContext());
        Log.d(TAG, "onDestroy: ");
        disconnectRequest = true;
        closeSocket();
        serviceActive = false;
        super.onDestroy();
    }

    public static boolean disconnect()
    {
        Log.d(TAG, "disconnect: ");
        if (socketConnected)
        {
            disconnectRequest = true;
            closeSocket();
            return true;
        } else
        {
            return false;
        }
    }

    private static void closeSocket()
    {

        Log.d(TAG, "closeSocket: ");
        handler.post(new Runnable()
        {
            public void run()
            {
                multicastAlive = false;
                if (socket != null)
                {
                    try
                    {
                        socketConnected = false;
                        socket.close();
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "CANNOT CLOSE SOCKET!", e);
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
