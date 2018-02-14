package com.brin.denonremotefree.HomeControl.Elements;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brin.denonremotefree.BrinObj.AddableElement;
import com.brin.denonremotefree.BrinObj.BrinDashFragment;
import com.brin.denonremotefree.HomeControl.Elements.Config.PrefsElements;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.db.coms;

import java.text.MessageFormat;

import butterknife.Bind;

/**
 * Created by Luca on 21.06.2016.
 */
public class TunerElement extends BrinDashFragment
{
    private String TAG = "INP.ELEMENT";
    @Bind(R.id.brinText1) TextView tvFreq;
    @Bind(R.id.brinText2) TextView tvMode;
    @Bind(R.id.brinProgressBar) ProgressBar pbMain;
    private String[] gets = new String[]{coms.tunerFreqStatus, coms.tunerModeStatus};

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle bRoot)
    {
        Log.d(TAG, "onCreateView: ");
        View view = getLayout(i, c, R.layout.element_tuner);
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
        }
        return view;
    }

    @Override
    public AddableElement getAddableElement(Context c)
    {
        return new AddableElement(c.getString(R.string.tuner_element_title), c.getString(R.string.tuner_element_desc), false, 1, PrefsElements.TYPE_TUNER, 1, R.drawable.tuner_100_white);
    }

    private void updateStatus(int s)
    {
        switch (s)
        {
            case BROAD_CON_WAIT:
                tvMode.setVisibility(View.GONE);
                tvFreq.setVisibility(View.GONE);
                pbMain.setVisibility(View.VISIBLE);
                break;
            case BROAD_CON_SUCC:
                break;
            case BROAD_CON_DIS:
                tvMode.setVisibility(View.GONE);
                tvFreq.setVisibility(View.GONE);
                pbMain.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void openBroadcast()
    {
        updateStatus(BROAD_CON_WAIT);
        super.openBroadcast();
    }

    @Override
    public void onTelnetResult(String l)
    {
        if (l.startsWith("TMAN"))
        {
            if (l.contains("AUTO"))
            {
                tvMode.setText(getString(R.string.automatic));
                pbMain.setVisibility(View.GONE);
                tvMode.setVisibility(View.VISIBLE);
                return;
            }
            if (l.contains("MANUAL"))
            {
                tvMode.setText(getString(R.string.manual));
                pbMain.setVisibility(View.GONE);
                tvMode.setVisibility(View.VISIBLE);
                return;
            }

            if (l.contains("FM"))
            {

                return;
            }
            if (l.contains("AM"))
            {

                return;
            }
        }
        if (l.matches("TFAN[0-9]*"))
        {
            fetchFreq(l);
        }
    }

    private void fetchFreq(String l)
    {
        try
        {
            l = l.replaceAll("\\D+", "");
            if (l.length() == 0) return;
            Double freq = Double.valueOf(l);
            freq = freq / 100;
            tvFreq.setText(MessageFormat.format("{0} {1}", freq, freq >= 500 ? "KHZ" : "MHZ"));
            pbMain.setVisibility(View.GONE);
            tvFreq.setVisibility(View.VISIBLE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionError(int err, String ip)
    {
        updateStatus(BROAD_CON_WAIT);
    }

    @Override
    public void onBroadcastOpen(boolean isConn)
    {
        if (isConn)
        {
            for (String c : gets)
            {
                sendTelnetCom(c);
            }
        }
    }

    private void getStatus()
    {
        sendTelnetCom("SI?");
        for (int i = 2; i <= prefs.deviceZones(); i++)
        {
            sendTelnetCom("Z" + i + "?");
        }
    }

    private void getStatusTuner()
    {

    }
}