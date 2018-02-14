package com.brin.denonremotefree.setup;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.widgets.BrinToolbar;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Luca on 24.01.2016.
 */
public class SetupInputActivity extends BrinActivity
{
    private ArrayList<String> al1, alGet;
    private ArrayList<Integer> alInpId;

    @Bind(R.id.brinToolbar) BrinToolbar tbMain;

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setCrashReporter();
        setContentView(R.layout.activity_setup_input);
        ButterKnife.bind(this);

        setToolbar(tbMain);
        enableTitleMarquee(true);
        setNavBack(true);
        setTitle(getString(R.string.setup_input_title));
        getPref();
        alGet = new ArrayList<>();
        al1 = new ArrayList<>();
        alInpId = new ArrayList<>();

        alGet.addAll(Arrays.asList("SSSOD ?", "SSFUN ?"));
        al1.addAll(Arrays.asList("TUNER", "CD", "PHONO", "DVD", "BD", "TV", "SAT/CBL", "GAME", "AUX1", "AUX2", "MPLAY", "USB/IPOD", "XPORT", "BT", "IRADIO", "FLICKR", "LASTFM", "SPOTIFY", "SERVER", "FAVORITES", "SIRIUSXM"));
        alInpId.addAll(Arrays.asList(8, 9, 12, 1, 3, 7, 0, 2, 4, 15, 16, 13, 23, 21, 11, 24, 20, 14, 6, 5, 25));
        //vfMain.setDisplayedChild(1);
    }

    @Override
    public void onBroadcastOpen(boolean isConn)
    {
        super.onBroadcastOpen(isConn);
        for (String c : alGet)
            sendTelnetCom(c);
    }

    private boolean lic()
    {
        if (!prefs.isAppActivated())
        {
            Toast.makeText(getApplicationContext(), getString(R.string.pro_required), Toast.LENGTH_SHORT).show();
        }
        return prefs.isAppActivated();
    }

    public void onSetting1(View v)
    {
        Switch sw = (Switch) v;
        int id = Integer.valueOf(sw.getTag().toString());
        String to;

        if (sw.isChecked())
            to = " USE";
        else
            to = " DEL";

        if (lic())
            sendTelnetCom("SSSOD" + al1.get(id - 1) + to);
    }

    public void onSetting2(View v)
    {
        final Integer id = Integer.valueOf(v.getTag().toString());
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle((getString(R.string.rename) + " " + returnSwitch(id).getText().toString()).toUpperCase());

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton(getString(R.string.rename).toUpperCase(), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                String t = input.getEditableText().toString();
                sendTelnetCom("SSFUN" + al1.get(id - 1) + " " + t);
                prefs.putInputRename(alInpId.get(id - 1), t);
            }
        });

        alert.setNegativeButton(getString(android.R.string.cancel),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();

        input.setHint(prefs.getInputRename(id - 1));
    }

    private Switch returnSwitch(Integer id)
    {
        return ((Switch) findViewById(getResources().getIdentifier("swSetupInput" + id.toString(), "id", getPackageName())));
    }

    @Override
    public void onTelnetResult(String l)
    {
        super.onTelnetResult(l);

        try
        {
            if (l.contains("SSSOD"))
            {
                Integer in = 0;
                while (in < al1.size())
                {
                    if (l.contains(al1.get(in)))
                    {
                        if (l.contains("USE"))
                        {
                            returnSwitch(in + 1).setChecked(true);
                        } else
                        {
                            returnSwitch(in + 1).setChecked(false);
                        }
                        break;
                    }
                    in++;
                }
            } else if (l.contains("SSFUN"))
            {
                Integer in = 0;
                while (in < al1.size())
                {
                    if (l.contains(al1.get(in)))
                    {
                        String r = l.substring(l.indexOf(" ") + 1);
                        //sp.edit().putString("setup.input.rename." + String.valueOf(in), r).apply();
                        //prefs.putInputRename(in, r);
                        break;
                    }
                    in++;
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed()
    {
        goBack();
    }

    private void goBack()
    {
        finish();
    }

    public void onPro()
    {
        openKeyActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (!prefs.isAppActivated())
            getMenuInflater().inflate(R.menu.menu_show_buy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuKey:
                onPro();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        super.closeBroadcast();
        isActivityVisible = false;
        isActivityActive = true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        isActivityVisible = true;
        isActivityActive = true;
        openBroadcast();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        isActivityVisible = false;
        isActivityActive = false;
        closeBroadcast();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        isActivityVisible = true;
        isActivityActive = true;
        openBroadcast();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        isActivityVisible = false;
        isActivityActive = false;
        closeBroadcast();
    }

    public static boolean isActivityVisible = false;
    public static boolean isActivityActive = false;

    public static boolean isActivityVisible()
    {
        return isActivityVisible;
    }

    public static boolean isActivityActive()
    {
        return isActivityActive;
    }
}
