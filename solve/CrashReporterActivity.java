package com.brin.denonremotefree.solve;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.binding.ReceiverListActivity;
import com.brin.denonremotefree.widgets.BrinToolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class CrashReporterActivity extends BrinActivity
{

    private static final String TAG = "CRASH.REPORTER";
    private String stack = "";
    private String stackFull = "";

    @Bind(R.id.brinToolbar) BrinToolbar tbMain;

    @Override
    public boolean broadcastEnabled()
    {
        return false;
    }

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.activity_crash_reporter);
        try
        {
            CustomActivityOnCrash.install(this);
            stack = CustomActivityOnCrash.getStackTraceFromIntent(getIntent());
            stackFull = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, getIntent());
            ButterKnife.bind(this);
            enableAnalytics();

            Log.e(TAG, "onCreate: "+stackFull);

            sendTrackMsg("CRASH_REP/" + stackFull);

            setToolbar(tbMain);
            setTitle(getString(R.string.crash_reporter));
            setNavBack(true);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        CustomActivityOnCrash.restartApplicationWithIntent(this, new Intent(this, ReceiverListActivity.class));
    }

    public void onClick(View v)
    {
        int c = Integer.valueOf(v.getTag().toString());
        switch (c)
        {
            case 1:
                restartApp();
                break;
            case 2:
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.setType("vnd.android.cursor.item/email");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.stacktrace_email)});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "STACKTRACE REPORT");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "\n\n" + stackFull);
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_stacktrace_using)));
                break;
            case 3:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(getString(R.string.stacktrace));
                dialog.setMessage(stack);
                dialog.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
            case 4:
                PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
                restartApp();
                break;
        }
    }
}