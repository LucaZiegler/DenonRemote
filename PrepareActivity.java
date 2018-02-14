package com.brin.denonremotefree;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.brin.denonremotefree.Helper.Prefs;
import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.BrinObj.HomeTab;
import com.brin.denonremotefree.widgets.BrinToolbar;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PrepareActivity extends BrinActivity
{

    private static final String TAG = "PREP.ACT";
    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinProgressBar) ProgressBar pbMain;

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setCrashReporter();
        setContentView(R.layout.activity_prepare);
        ButterKnife.bind(this);
        enableAnalytics();

        getPrefsByIntent();

        setToolbar(tbMain);
        setTitle("preparing...");
        setProgress(true, 1);
        setNavBack(true);
        enableCustomProgress(pbMain);
        enableTitleMarquee(true);

        setProgressMax(prefs.deviceZones() + 1);

        getZoneNames();
    }

    @Override
    public void onBroadcastOpen(boolean isConn)
    {
        Log.d(TAG, "onBroadcastOpen: " + isConn);
        sendTelnetCom("AMX");
    }

    @Override
    public void onTelnetResult(String msg)
    {
        Log.d(TAG, "onTelnetResult: " + msg);
        sendTrackMsg(msg);
    }

    @Override
    public void onZoneNamesStart()
    {
        Log.d(TAG, "onZoneNamesStart: ");
        setTitle("Loading zone names...");
    }

    @Override
    public void onZoneNamesResult(int z, String n)
    {
        Log.d(TAG, "onZoneNamesResult: " + z + "/" + n);
        setProgress(false, z);
        prefs.putDeviceZoneName(z, n);
    }

    @Override
    public void onZoneNamesDone(boolean success)
    {
        Log.d(TAG, "onZoneNamesDone: " + success);
        prepareDashboard();
    }

    private final String[] bak = new String[]{"[{\"elementParams\":{\"gravity\":-1,\"bottomMargin\":0,\"endMargin\":-2147483648,\"leftMargin\":35,\"mMarginFlags\":12,\"rightMargin\":35,\"startMargin\":-2147483648,\"topMargin\":0,\"height\":-2,\"width\":1370},\"elementPosX\":0,\"elementPosY\":0,\"elementRow\":0,\"elementTitle\":\"\",\"elementType\":2,\"elementZone\":1,\"elementZoneSupport\":1},{\"elementParams\":{\"gravity\":-1,\"bottomMargin\":0,\"endMargin\":-2147483648,\"leftMargin\":35,\"mMarginFlags\":12,\"rightMargin\":35,\"startMargin\":-2147483648,\"topMargin\":341,\"height\":-2,\"width\":1370},\"elementPosX\":0,\"elementPosY\":0,\"elementRow\":1,\"elementTitle\":\"\",\"elementType\":1,\"elementZone\":1,\"elementZoneSupport\":1}]",

            "[{\"elementParams\":{\"gravity\":-1,\"bottomMargin\":0,\"endMargin\":-2147483648,\"leftMargin\":35,\"mMarginFlags\":12,\"rightMargin\":35,\"startMargin\":-2147483648,\"topMargin\":0,\"height\":-2,\"width\":1370},\"elementPosX\":0,\"elementPosY\":0,\"elementRow\":0,\"elementTitle\":\"\",\"elementType\":2,\"elementZone\":2,\"elementZoneSupport\":1},{\"elementParams\":{\"gravity\":-1,\"bottomMargin\":0,\"endMargin\":-2147483648,\"leftMargin\":35,\"mMarginFlags\":12,\"rightMargin\":35,\"startMargin\":-2147483648,\"topMargin\":341,\"height\":-2,\"width\":1370},\"elementPosX\":0,\"elementPosY\":0,\"elementRow\":1,\"elementTitle\":\"\",\"elementType\":1,\"elementZone\":2,\"elementZoneSupport\":1}]",

            "[{\"elementParams\":{\"gravity\":-1,\"bottomMargin\":0,\"endMargin\":-2147483648,\"leftMargin\":35,\"mMarginFlags\":12,\"rightMargin\":35,\"startMargin\":-2147483648,\"topMargin\":0,\"height\":-2,\"width\":1370},\"elementPosX\":0,\"elementPosY\":0,\"elementRow\":0,\"elementTitle\":\"\",\"elementType\":2,\"elementZone\":3,\"elementZoneSupport\":1},{\"elementParams\":{\"gravity\":-1,\"bottomMargin\":0,\"endMargin\":-2147483648,\"leftMargin\":35,\"mMarginFlags\":12,\"rightMargin\":35,\"startMargin\":-2147483648,\"topMargin\":341,\"height\":-2,\"width\":1370},\"elementPosX\":0,\"elementPosY\":0,\"elementRow\":1,\"elementTitle\":\"\",\"elementType\":1,\"elementZone\":3,\"elementZoneSupport\":1}]"};

    private void prepareDashboard()
    {
        try
        {
            Log.d(TAG, "prepareDashboard: ");
            setTitle("generating dashboard...");
            ArrayList<HomeTab> alTabs = new ArrayList<>();
            for (int z = 1; z <= prefs.deviceZones(); z++)
            {
                HomeTab ht = new HomeTab(z - 1, prefs.getDeviceZoneName(z));
                ht.setGenDef(true);
                ht.setGenZone(z);
                alTabs.add(ht);
                try
                {
                    prefs.putLocString(Prefs.SAV_HOM_LIST + (z - 1), bak[z - 1]);
                }
                catch (Exception e) {}
            }
            prefs.storeTabs(alTabs);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Dashboard Error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        openApp(prefs.getStoredTime());
    }
}
