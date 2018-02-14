package com.brin.denonremotefree.HomeControl.Elements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brin.denonremotefree.BrinObj.AddableElement;
import com.brin.denonremotefree.BrinObj.BrinDashFragment;
import com.brin.denonremotefree.Helper.SetView;
import com.brin.denonremotefree.HomeControl.Elements.Config.PrefsElements;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.control.Sound.SoundList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Luca on 08.06.2016.
 */
public class SoundModeElement extends BrinDashFragment
{
    private String TAG = "SOUND.MODE.ELEMENT";
    @Bind(R.id.brinText1) TextView tvSwitch;
    @Bind(R.id.brinFrameLayout1) FrameLayout flSwitch;
    @Bind(R.id.brinProgressBar) ProgressBar pbMain;

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle bRoot)
    {
        Log.d(TAG, "onCreateView: ");
        View view = getLayout(i, c, R.layout.element_sound);
        ButterKnife.bind(this, view);
        bindButterKnife();
        getPrefs();
        getIntentElement();
        if (curElement == null)
        {
            Log.e(TAG, "Element is null");
            updateStatus(BROAD_CON_DIS);
        } else
        {
            getZone();
        }
        return view;
    }

    @Override
    public AddableElement getAddableElement(Context c)
    {
        return new AddableElement(c.getString(R.string.element_sound_title), c.getString(R.string.element_sound_desc), false, 1, PrefsElements.TYPE_SOUND_MODE, 1, R.drawable.sound_mode_100_white);
    }

    public void updateStatus(int s)
    {
        switch (s)
        {
            case BROAD_CON_WAIT:
                pbMain.setVisibility(View.VISIBLE);
                flSwitch.setVisibility(View.GONE);
                break;
            case BROAD_CON_SUCC:
                pbMain.setVisibility(View.GONE);
                flSwitch.setVisibility(View.VISIBLE);
                break;
            case BROAD_CON_DIS:
                pbMain.setVisibility(View.VISIBLE);
                flSwitch.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onTelnetResult(String m)
    {
        Log.d(TAG, "onTelnetResult: " + m);
        if (m.startsWith("MSQUICK")) return;
        if (m.startsWith(zone.setSound()))
        {
            fetchSoundMode(m);
            return;
        }
    }

    @OnClick(R.id.brinFrameLayout1)
    public void onOpenSound(View v)
    {
        startActivity(new Intent(getActivity(), SoundList.class));
    }

    private void fetchSoundMode(String r)
    {
        SetView.setText(tvSwitch, r.replaceFirst("MS", ""), null);
        updateStatus(BROAD_CON_SUCC);
    }

    @Override
    public void onConnectionError(int err, String ip)
    {
        Log.d(TAG, "onConnectionError: " + err);
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
        sendTelnetCom(zone.getSound());
    }

}