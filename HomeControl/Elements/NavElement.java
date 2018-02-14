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

/**
 * Created by Luca on 21.10.2016.
 */

public class NavElement extends BrinDashFragment
{
    private String TAG = "INP.ELEMENT";

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle bRoot)
    {
        Log.d(TAG, "onCreateView: ");
        View view = getLayout(i, c, R.layout.element_nav);
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
        return new AddableElement(
                c.getString(R.string.nav_element_title),
                c.getString(R.string.nav_element_desc),
                false,
                1,
                PrefsElements.TYPE_NAV,
                1,
                R.drawable.remote_100_white
        );
    }
}