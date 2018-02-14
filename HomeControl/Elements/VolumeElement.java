package com.brin.denonremotefree.HomeControl.Elements;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.brin.denonremotefree.BrinObj.AddableElement;
import com.brin.denonremotefree.BrinObj.BrinDashFragment;
import com.brin.denonremotefree.Helper.SetView;
import com.brin.denonremotefree.HomeControl.Elements.Config.PrefsElements;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.views.BrinViewFlipper;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Luca on 08.06.2016.
 */
public class VolumeElement extends BrinDashFragment
{
    private String TAG = "VOLUME.ELEMENT";
    @Bind(R.id.brinText1) TextView tvVol;
    @Bind(R.id.brinSeekBar) SeekBar sbVol;
    @Bind(R.id.brinVolMute) ImageButton ibMute;
    @Bind(R.id.brinViewFlipper) BrinViewFlipper vfMain;

    private ArrayList<String> alGets = new ArrayList<>();
    private boolean muteActive = false;

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle bRoot)
    {
        Log.d(TAG, "onCreateView: ");
        View view = getLayout(i, c, R.layout.element_volume);
        bindButterKnife();
        getPrefs();
        getIntentElement();
        if (curElement == null || prefs == null)
        {
            Log.e(TAG, "Element is null");
            //updateStatus(BROAD_CON_DIS);
        } else
        {
            getZone();
            setListener();
            prepareGets();
        }
        return view;
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
    public AddableElement getAddableElement(Context c)
    {
        return new AddableElement(
                c.getString(R.string.element_vol_title),
                c.getString(R.string.element_vol_desc),
                true,
                1,
                PrefsElements.TYPE_VOLUME,
                1,
                R.drawable.vol_up_100_white
        );
    }

    private void prepareGets()
    {
        if (zone.isMainZone())
        {
            sbVol.setMax(160);
        } else
        {
            sbVol.setMax(80);
        }
        alGets.addAll(Arrays.asList(zone.getVolume(), zone.getMute()));
    }

    private void setListener()
    {
        sbVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser)
            {
                if (fromUser)
                {
                    Float volFloat = (float) p;
                    if (zone.isMainZone())
                    {
                        volFloat /= 2;
                    }
                    String volCom = String.valueOf(volFloat);
                    SetView.setText(tvVol, volCom, null);
                    volCom = volCom.replaceAll("\\D+", "");
                    if (volFloat < 10) volCom = "0" + volCom;
                    //if (volFloat < 100) volCom = "0" + volCom;
                    if (volCom.endsWith("0")) volCom = volCom.substring(0, volCom.length() - 1);
                    sendDirectTelnetCom(zone.setVolume() + volCom);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });
    }

    @Override
    public void onTelnetResult(String m)
    {
        Log.d(TAG, "onTelnetResult: " + m);
        switch (zone.getZoneId())
        {
            case 1:
                if (m.matches("MV\\d+$"))
                {
                    fetchVol(true, m);
                    return;
                }
                if (m.matches("MVMAX \\d+$"))
                {
                    fetchMaxVol(m);
                    return;
                }
                if (m.matches("MU[A-Z][A-Z]+"))
                {
                    fetchMute(true, m);
                }
                break;
            default:
                if (m.matches("Z" + zone.getZoneId() + "\\d+$"))
                {
                    fetchVol(false, m);
                    return;
                }
                if (m.matches("Z" + zone.getZoneId() + "MU[A-Z][A-Z]+"))
                {
                    fetchMute(false, m);
                }
                break;
        }
    }

    private void fetchMute(boolean mainZone, String l)
    {
        if (l.contains("MUOFF"))
        {
            muteActive = false;
            ibMute.setColorFilter(ContextCompat.getColor(getActivity(),R.color.accent_color));
        } else if (l.contains("MUON"))
        {
            muteActive = true;
            ibMute.setColorFilter(ContextCompat.getColor(getActivity(),R.color.mute_color));
        }
    }

    private void fetchVol(boolean mainZone, String l)
    {
        try
        {
            if (mainZone)
            {
                String volMax = l.replaceAll("\\D+", "");
                String volText;
                Float volFloat = Float.valueOf(volMax);
                Integer volInt;
                if (volMax.matches("[0-9][0-9]5"))
                {
                    // FLOAT VALUE
                    volFloat /= 10;
                    volText = volFloat.toString();
                } else
                {
                    volText = volFloat.toString()/* + ".0"*/;
                }
                volFloat *= 2;
                volInt = Math.round(volFloat);
                SetView.setText(tvVol, volText, null);
                SetView.setProgress(sbVol, volInt);
            } else
            {
                String volZone = l.substring(2).replaceAll("\\D+", "");
                Integer volInt = Integer.valueOf(volZone);
                SetView.setText(tvVol, volZone, null);
                SetView.setProgress(sbVol, volInt);
            }
            sbVol.setEnabled(true);
            updateStatus(BROAD_CON_SUCC);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void fetchMaxVol(String l)
    {
        Log.d(TAG, "fetchMaxVol: " + l);
    }

    @Override
    public void onConnectionError(int err, String ip)
    {
        Log.d(TAG, "onConnectionError: " + err);
        updateStatus(BROAD_CON_DIS);
    }

    @Override
    public void onBroadcastOpen(boolean isConn)
    {
        if (isConn)
        {
            getStatus();
        }
    }

    @Override
    public void onBroadcastClosed()
    {
        updateStatus(BROAD_CON_WAIT);
    }

    private void getStatus()
    {
        for (String c : alGets)
        {
            sendTelnetCom(c);
        }
    }

    @OnClick (R.id.brinVolDn)
    public void onVolDn(View v)
    {
        sendDirectTelnetCom(zone.setVolumeDn());
    }

    @OnClick (R.id.brinVolUp)
    public void onVolUp(View v)
    {
        sendDirectTelnetCom(zone.setVolumeUp());
    }

    @OnClick (R.id.brinVolMute)
    public void onVolMute(View v)
    {
        sendDirectTelnetCom(zone.setMute(!muteActive));
    }
}
