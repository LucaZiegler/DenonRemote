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
public class FavoriteElement extends BrinDashFragment
{
    private String TAG = "FAV.ELEMENT";

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle bRoot)
    {
        Log.d(TAG, "onCreateView: ");
        View view = getLayout(i, c, R.layout.element_favorite);
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
    public AddableElement getAddableElement(Context c)
    {
        return new AddableElement(
                c.getString(R.string.element_fav_title),
                c.getString(R.string.element_fav_desc),
                true,
                1,
                PrefsElements.TYPE_FAV,
                2,
                R.drawable.like_100_white
        );
    }


    @OnClick(R.id.brinButton)
    public void onNav1(View v)
    {
        sendTelnetCom(zone.setFav(1));
    }

    @OnClick(R.id.brinButton2)
    public void onNav2(View v)
    {
        sendTelnetCom(zone.setFav(2));
    }

    @OnClick(R.id.brinButton3)
    public void onNav3(View v)
    {
        sendTelnetCom(zone.setFav(3));
    }

    @OnClick(R.id.brinButton4)
    public void onNav4(View v)
    {
        sendTelnetCom(zone.setFav(4));
    }


    @OnLongClick(R.id.brinButton)
    public boolean onLongNav1(View v)
    {
        v.setEnabled(false);
        sendTelnetCom(zone.setFavMemory(1));
        showSavedMsg(1);
        v.cancelLongPress();
        v.setEnabled(true);
        return true;
    }

    @OnLongClick(R.id.brinButton2)
    public boolean onLongNav2(View v)
    {
        v.setEnabled(false);
        sendTelnetCom(zone.setFavMemory(2));
        showSavedMsg(2);
        v.cancelLongPress();
        v.setEnabled(true);
        return true;
    }

    @OnLongClick(R.id.brinButton3)
    public boolean onLongNav3(View v)
    {
        v.setEnabled(false);
        sendTelnetCom(zone.setFavMemory(3));
        showSavedMsg(3);
        v.cancelLongPress();
        v.setEnabled(true);
        return true;
    }

    @OnLongClick(R.id.brinButton4)
    public boolean onLongNav4(View v)
    {
        v.setEnabled(false);
        sendTelnetCom(zone.setFavMemory(4));
        showSavedMsg(4);
        v.cancelLongPress();
        v.setEnabled(true);
        return true;
    }

    private void showSavedMsg(int i)
    {
        Toast.makeText(getActivity(), MessageFormat.format(getString(R.string.msg_fav_saved), i), Toast.LENGTH_LONG).show();
    }
}