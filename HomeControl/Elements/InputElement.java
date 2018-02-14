package com.brin.denonremotefree.HomeControl.Elements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brin.denonremotefree.BrinObj.AddableElement;
import com.brin.denonremotefree.BrinObj.BrinDashFragment;
import com.brin.denonremotefree.Helper.SetView;
import com.brin.denonremotefree.HomeControl.Elements.Config.PrefsElements;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.control.Sound.InputList;
import com.brin.denonremotefree.db.InputTools;
import com.brin.denonremotefree.db.coms;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Luca on 21.06.2016.
 */
public class InputElement extends BrinDashFragment
{
    private String TAG = "INP.ELEMENT";
    //private ArrayList<Input> alInputs = InputTools.inputList();
    @Bind(R.id.elementText1) TextView tvInput;
    @Bind(R.id.elementImage1) ImageView ivInput;
    @Bind(R.id.brinProgressBar) ProgressBar pbMain;

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle bRoot)
    {
        Log.d(TAG, "onCreateView: ");
        View view = getLayout(i, c, R.layout.element_input);
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
        return new AddableElement(c.getString(R.string.input_element_title), c.getString(R.string.input_element_desc), true, 1, PrefsElements.TYPE_INP, 1, R.drawable.input_100_white);
    }

    private void updateStatus(int s)
    {
        switch (s)
        {
            case BROAD_CON_WAIT:
                pbMain.setVisibility(View.VISIBLE);
                tvInput.setVisibility(View.GONE);
                ivInput.setVisibility(View.GONE);
                ivInput.setImageBitmap(null);
                break;
            case BROAD_CON_SUCC:
                pbMain.setVisibility(View.GONE);
                break;
            case BROAD_CON_DIS:
                pbMain.setVisibility(View.VISIBLE);
                ivInput.setVisibility(View.GONE);
                tvInput.setVisibility(View.GONE);
                ivInput.setImageBitmap(null);

                break;
        }
    }

    @Override
    public void onBroadcastClosed()
    {
        updateStatus(BROAD_CON_WAIT);
    }

    @Override
    public void onTelnetResult(String l)
    {
        if (l.contains("SSFUN"))
        {
            try
            {
                String[] inps = l.split(" ", 2);
                int id = InputTools.getInpIdFromReceiver(inps[0]);
                prefs.putInputRename(id, inps[1]);
                Log.d(TAG, "DEV_754: " + id + "/" + Arrays.toString(inps));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return;
        }
        switch (zone.getZoneId())
        {
            case 1:
                if (l.startsWith("SI"))
                {
                    l = l.substring(2);
                    fetchInput(l, InputTools.getInpIdFromReceiver(l));
                    return;
                }
                break;
            default:
                if (l.startsWith("Z" + zone.getZoneId()))
                {
                    Log.d(TAG, "onTelnetResult: 1");
                    String l2 = l.substring(2);
                    if (!l2.startsWith("ON") && !l2.startsWith("OFF"))
                    {
                        Log.d(TAG, "onTelnetResult: 2");
                        int inpId = InputTools.getInpIdFromReceiver(l2);
                        if (inpId != -1)
                        {
                            Log.d(TAG, "onTelnetResult: 3");
                            fetchInput(l2, inpId);
                        }
                    }
                    return;
                }
                break;
        }
    }

    @OnClick(R.id.elementOpen)
    public void onSetInput(View v)
    {
        Intent intent = new Intent(getActivity(), InputList.class);
        intent.putExtra("ZONE_OBJ", zone.toJson());
        startActivity(intent);
    }

    private void fetchInput(String inputName, int inpId)
    {
        updateStatus(BROAD_CON_SUCC);
        //SetView.setText(tvInput, inputName, null);
        String n = prefs.getInputRename(inpId);
        if (n == null)
        {
            inputName = InputTools.inputList().get(inpId-1).getName();
        } else
        {
            inputName = n;
        }
        Log.d(TAG, "DEV_755: "+n);
        //tvInput.setText(inputName);
        SetView.setText(tvInput, inputName, null);
        tvInput.setVisibility(View.VISIBLE);
        if (inpId == -2) inpId = InputTools.getInpIdFromReceiver(inputName);
        Log.d(TAG, "fetchInput: " + inputName + "/" + inpId);
        if (inpId > 0)
        {
            try
            {
                ivInput.setImageResource(InputTools.getInputImageFromId(inpId));
                ivInput.setVisibility(View.VISIBLE);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                ivInput.setVisibility(View.GONE);
            }
        }
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
            getStatus();
        }
    }

    private void getStatus()
    {
        String get = zone.getInput();
        Log.d(TAG, "getStatus: " + get);
        sendTelnetCom(get);
        sendTelnetCom(coms.inputGetRenames);
    }
}