package com.brin.denonremotefree.HomeControl.Elements;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brin.denonremotefree.BrinObj.AddableElement;
import com.brin.denonremotefree.BrinObj.BrinDashFragment;
import com.brin.denonremotefree.HomeControl.Elements.Config.PrefsElements;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.views.BrinSwitch;
import com.brin.denonremotefree.views.BrinViewFlipper;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Luca on 21.06.2016.
 */
public class PowerElement extends BrinDashFragment
{
    private String TAG = "PW.ELEMENT";
    @Bind(R.id.brinViewFlipper) BrinViewFlipper vfMain;
    @Bind(R.id.brinSwitch) BrinSwitch swPw;

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle bRoot)
    {
        Log.d(TAG, "onCreateView: ");
        View view = getLayout(i, c, R.layout.element_power);
        bindButterKnife();
        getPrefs();
        getIntentElement();
        //Toast.makeText(getActivity(),curElement.elementTitle,Toast.LENGTH_LONG).show();
        if (curElement == null || prefs == null)
        {
            Log.e(TAG, "Element is null");
            //updateStatus(BROAD_CON_DIS);
        } else
        {
            getZone();
            swPw.setText(prefs.getDeviceZoneName(curElement.elementZone));
        }
        return view;
    }

    public AddableElement getAddableElement(Context c)
    {
        return new AddableElement(
                c.getString(R.string.element_pw_title),
                c.getString(R.string.element_pw_desc),
                true,
                1,
                PrefsElements.TYPE_PW,
                1,
                R.drawable.standby_100_white
        );
    }

    private void updateStatus(int s)
    {
        switch (s)
        {
            case BROAD_CON_WAIT:
                vfMain.setChild(0);
                break;
            case BROAD_CON_SUCC:
                vfMain.setChild(1);
                break;
            case BROAD_CON_DIS:
                vfMain.setChild(0);
                break;
        }
    }

    @Override
    public void onTelnetResult(String m)
    {
        Log.d(TAG, "onTelnetResult: " + m);
        if (m.equals("PWSTANDBY") || m.equals("PWOFF"))
        {
            fetchPw(false);
            return;
        }
        switch (zone.getZoneId())
        {
            case 1:
                if (m.equals("ZMOFF"))
                {
                    fetchPw(false);
                    return;
                }
                if (m.equals("ZMON"))
                {
                    fetchPw(true);
                    return;
                }
                if (m.equals("PWON") && prefs.deviceZones() == 1)
                {
                    fetchPw(true);
                    return;
                }
                break;
            default:
                if (m.equals("Z" + zone.getZoneId() + "ON"))
                {
                    fetchPw(true);
                    return;
                }
                if (m.equals("Z" + zone.getZoneId() + "OFF"))
                {
                    fetchPw(false);
                    return;
                }
                break;
        }
    }

    @OnClick (R.id.brinFrameLayout1)
    public void onPw(View v)
    {
        sendTelnetCom(zone.setPower(!swPw.isChecked()));
    }

    private void fetchPw(boolean b)
    {
        swPw.setChecked(b);
        updateStatus(BROAD_CON_SUCC);
    }

    @Override
    public void onConnectionError(int err, String ip)
    {
        updateStatus(BROAD_CON_DIS);
        Log.d(TAG, "onConnectionError: " + err);
    }

    @Override
    public void onBroadcastClosed()
    {
        updateStatus(BROAD_CON_WAIT);
    }

    @Override
    public void onBroadcastOpen(boolean isConn)
    {
        if (isConn)
        {
            getPowerStatus();
        }
    }

    private void getPowerStatus()
    {
        sendTelnetCom(zone.getPower());
    }
}