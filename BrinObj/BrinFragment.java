package com.brin.denonremotefree.BrinObj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import com.brin.denonremotefree.BuildConfig;
import com.brin.denonremotefree.DashboardActivity;
import com.brin.denonremotefree.Helper.Prefs;
import com.brin.denonremotefree.HomeControl.Elements.Config.PrefsElements;
import com.brin.denonremotefree.Interface.ReceiverConnectionInterface;
import com.brin.denonremotefree.ReturnClass;
import com.brin.denonremotefree.db.coms;
import com.brin.denonremotefree.service.ReceiverConnectionService;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import butterknife.ButterKnife;

/**
 * Created by Luca on 14.08.2016.
 */

public class BrinFragment extends android.support.v4.app.Fragment implements ReceiverConnectionInterface
{
    private static final String TAG = "BRIN.FRAG" + generateInt();
    private View layout;
    private ProgressBar pb;
    private static boolean isFragmentVisible = false;
    private static boolean isFragmentActive = false;
    private BroadcastReceiver bcReceiver;
    private boolean broadCastActive = false;
    private GoogleAnalytics analytics;
    private Tracker tracker;
    private String userId;
    private ViewFlipper vf = null;
    protected Prefs prefs;
    protected Zone zone;
    protected DragElement curElement = null;


    private static int generateInt()
    {
        return 1 + (int) (Math.random() * 1000);
    }

    public void setupViewFlipper(ViewFlipper vf)
    {
        this.vf = vf;
    }

    public void setChild(int c)
    {
        if (vf != null)
        {
            if (vf.getDisplayedChild() != c)
            {
                vf.setDisplayedChild(c);
            }
        }
    }

    public void getPrefs()
    {
        try
        {
            prefs = DashboardActivity.getPrefs();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void getZone()
    {
        try
        {
            zone = new Zone(curElement.elementZone, prefs.deviceZones());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void getIntentElement()
    {
        try
        {
            curElement = ElementTool.DragElementFromJson(getArguments().getString(PrefsElements.intentData(), null));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public View getLayout(LayoutInflater i, ViewGroup container, int layout)
    {
        this.layout = i.inflate(layout, container, false);
        return this.layout;
    }

    public void bindButterKnife()
    {
        ButterKnife.bind(this, layout);
    }

    public void setProgressBar(ProgressBar p)
    {
        pb = p;
    }

    public void showProgress(boolean v)
    {
        try
        {
            if (v)
            {
                if (pb.getVisibility() != View.VISIBLE) pb.setVisibility(View.VISIBLE);
            } else
            {
                if (pb.getVisibility() == View.VISIBLE) pb.setVisibility(View.GONE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean broadcastEnabled()
    {
        return true;
    }

    public void openBroadcast()
    {
        try
        {
            if (broadcastEnabled()/* && isActivityVisible()*/ && !broadCastActive)
            {
                final IntentFilter filter = new IntentFilter();
                filter.addAction(coms.serviceID);

                bcReceiver = new BroadcastReceiver()
                {
                    @Override
                    public void onReceive(Context context, Intent intent)
                    {
                        try
                        {
                            switch (intent.getIntExtra("id", -1))
                            {
                                case BROAD_CON_WAIT:
                                    onConnecting();
                                    break;
                                case BROAD_CON_SUCC:
                                    onConnected(intent.getStringExtra("msg"), intent.getStringExtra("ip"));
                                    break;
                                case BROAD_CON_DIS:
                                    int err = intent.getIntExtra("err", -1);
                                    onConnectionError(err, intent.getStringExtra("ip"));
                                    break;
                                case BROAD_MSG:
                                    Log.d("wlevnkekwbjg", intent.getStringExtra("msg"));
                                    onTelnetResult(intent.getStringExtra("msg"));
                                    break;
                                case BROAD_NSE_RES:
                                    String n = intent.getStringExtra("msg");
                                    Log.d(TAG, "onReceive: "+n);
                                    onNseResult(n);
                                    break;
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                try
                {
                    getActivity().getApplicationContext().registerReceiver(bcReceiver, filter);
                    broadCastActive = true;
                    onBroadcastOpen(ReceiverConnectionService.socketConnected);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    //broadCastActive = false;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onNseResult(String a)
    {

    }

    public void onConnecting()
    {

    }

    public void onConnected(String hostName, String ipAddress)
    {

    }

    public void onConnectionError(int err, String ip)
    {

    }

    public void onTelnetResult(String msg)
    {

    }

    public void onBroadcastOpen(boolean isConn)
    {

    }

    private void closeBroadcast()
    {
        try
        {
            Log.d(TAG, "closeBroadcast: ");
            if (bcReceiver != null && broadCastActive)
                getActivity().getApplicationContext().unregisterReceiver(bcReceiver);
            broadCastActive = false;
            onBroadcastClosed();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onBroadcastClosed()
    {

    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause: ");
        closeBroadcast();
        isFragmentVisible = false;
        isFragmentActive = true;
        super.onPause();
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume: ");
        isFragmentVisible = true;
        isFragmentActive = true;
        //openBroadcast();
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy: ");
        isFragmentVisible = false;
        isFragmentActive = false;
        closeBroadcast();
        super.onDestroy();
    }

    @Override
    public void onStop()
    {
        Log.d(TAG, "onStop: ");
        isFragmentVisible = false;
        isFragmentActive = false;
        closeBroadcast();
        super.onStop();
    }

    @Override
    public void onStart()
    {
        Log.d(TAG, "onStart: ");
        isFragmentVisible = true;
        isFragmentActive = true;
        openBroadcast();
        super.onStart();
    }

    public void enableAnalytics()
    {
        if (BuildConfig.DEBUG)
        {
            analytics = GoogleAnalytics.getInstance(getActivity());
            analytics.setLocalDispatchPeriod(1800);
            tracker = analytics.newTracker("UA-59925624-1");
            tracker.enableExceptionReporting(true);
            tracker.enableAdvertisingIdCollection(true);
            tracker.enableAutoActivityTracking(true);
            tracker.setScreenName(this.getClass().getSimpleName());
            userId = ReturnClass.getUserId(getActivity());
            tracker.setClientId(userId);
        }
    }

    public static boolean isFragmentVisible()
    {
        return isFragmentVisible;
    }

    public static boolean isFragmentActive()
    {
        return isFragmentActive;
    }

    public void sendTelnetCom(String c)
    {
        ReceiverConnectionService.sendCom(c);
    }

    public void sendDirectTelnetCom(String c)
    {
        ReceiverConnectionService.pushCom(c);
    }

}
