package com.brin.denonremotefree.HomeControl.Elements;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.brin.denonremotefree.BrinObj.AddableElement;
import com.brin.denonremotefree.BrinObj.BrinDashFragment;
import com.brin.denonremotefree.HomeControl.Elements.Config.PrefsElements;
import com.brin.denonremotefree.R;

import java.text.MessageFormat;

import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by Luca on 21.06.2016.
 */
public class QuickSelectElement extends BrinDashFragment
{
    private String TAG = "QUICK.ELEMENT";

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle bRoot)
    {
        Log.d(TAG, "onCreateView: ");
        View view = getLayout(i, c, R.layout.element_quick_select);
        bindButterKnife();
        getPrefs();
        getIntentElement();
        if (curElement == null || prefs == null)
        {
            Log.e(TAG, "Element is null");
        } else
        {
            getZone();
        }
        return view;
    }

    @Override
    public void onBroadcastOpen(boolean isConn)
    {
        super.onBroadcastOpen(isConn);
        sendTelnetCom(zone.getQuick());
    }

    @Override
    public AddableElement getAddableElement(Context c)
    {
        return new AddableElement(
                c.getString(R.string.element_quick_title),
                c.getString(R.string.element_quick_desc),
                true,
                1,
                PrefsElements.TYPE_QUICK,
                2,
                R.drawable.quick_100_white
        );
    }

    @OnClick(R.id.brinButton)
    public void onNav1(View v)
    {
        sendTelnetCom(zone.setQuick(1));
    }

    @OnClick(R.id.brinButton2)
    public void onNav2(View v)
    {
        sendTelnetCom(zone.setQuick(2));
    }

    @OnClick(R.id.brinButton3)
    public void onNav3(View v)
    {
        sendTelnetCom(zone.setQuick(3));
    }

    @OnClick(R.id.brinButton4)
    public void onNav4(View v)
    {
        sendTelnetCom(zone.setQuick(4));
    }

    @OnClick(R.id.brinButton5)
    public void onNav5(View v)
    {
        sendTelnetCom(zone.setQuick(5));
    }

    @OnLongClick(R.id.brinButton)
    public boolean onLongNav1(View v)
    {
        v.setEnabled(false);
        sendTelnetCom(zone.setQuickMemory(1));
        showSavedMsg(1);
        v.cancelLongPress();
        v.setEnabled(true);
        return true;
    }

    @OnLongClick(R.id.brinButton2)
    public boolean onLongNav2(View v)
    {
        v.setEnabled(false);
        sendTelnetCom(zone.setQuickMemory(2));
        showSavedMsg(2);
        v.cancelLongPress();
        v.setEnabled(true);
        return true;
    }

    @OnLongClick(R.id.brinButton3)
    public boolean onLongNav3(View v)
    {
        v.setEnabled(false);
        sendTelnetCom(zone.setQuickMemory(3));
        showSavedMsg(3);
        v.cancelLongPress();
        v.setEnabled(true);
        return true;
    }

    @OnLongClick(R.id.brinButton4)
    public boolean onLongNav4(View v)
    {
        v.setEnabled(false);
        sendTelnetCom(zone.setQuickMemory(4));
        showSavedMsg(4);
        v.cancelLongPress();
        v.setEnabled(true);
        return true;
    }

    @OnLongClick(R.id.brinButton5)
    public boolean onLongNav5(View v)
    {
        v.setEnabled(false);
        sendTelnetCom(zone.setQuickMemory(5));
        showSavedMsg(5);
        v.cancelLongPress();
        v.setEnabled(true);
        return true;
    }

    private void showSavedMsg(int i)
    {
        Toast.makeText(getActivity(), MessageFormat.format(getString(R.string.msg_quick_saved), i), Toast.LENGTH_LONG).show();
    }
}