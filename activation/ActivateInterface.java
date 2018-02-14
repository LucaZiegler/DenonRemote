package com.brin.denonremotefree.activation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brin.denonremotefree.Helper.Prefs;
import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActivateInterface extends BrinActivity
{

    private final String comKey = "XXX";

    @Override
    public boolean broadcastEnabled()
    {
        return false;
    }

    @Bind(R.id.brinIcon) ImageView ivMsg;
    @Bind(R.id.brinText1) TextView tvMsg;
    @Bind(R.id.brinProgressBar2) ProgressBar pbWait;

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setCrashReporter();
        setContentView(R.layout.activity_activate_interface);
        ButterKnife.bind(this);

        Intent i = getIntent();
        prefs = new Prefs(getApplicationContext());

        String key = i.getStringExtra("key");
        if (key != null)
        {
            if (comKey.equals(key))
            {
                sendTrackMsg("correct.key.licensing...");
                try
                {
                    prefs.activateApp2(true);
                    Intent resultData = new Intent();
                    resultData.putExtra("status", 0);
                    setResult(Activity.RESULT_OK, resultData);
                    setStatus(getString(R.string.activate_succes));
                }
                catch (Exception e)
                {
                    Intent resultData = new Intent();
                    resultData.putExtra("status", 3);
                    setResult(Activity.RESULT_CANCELED, resultData);
                    setStatus(null);
                }
            } else
            {
                sendTrackMsg("activation.critical.warn.wrong.key/" + key);
                Intent resultData = new Intent();
                resultData.putExtra("status", 2);
                setResult(Activity.RESULT_CANCELED, resultData);
                setStatus(null);
            }
        } else
        {
            sendTrackMsg("activation.warn.no.key/");
            Intent resultData = new Intent();
            resultData.putExtra("status", 1);
            setResult(Activity.RESULT_CANCELED, resultData);
            setStatus(null);
        }
    }

    public void onFinish(View v)
    {
        Intent resultData = new Intent();
        resultData.putExtra("status", 1);
        setResult(Activity.RESULT_CANCELED, resultData);
        finish();
    }

    private void setStatus(String msg)
    {
        pbWait.setVisibility(msg == null ? View.VISIBLE : View.GONE);
        tvMsg.setVisibility(msg == null ? View.GONE : View.VISIBLE);
        ivMsg.setVisibility(msg == null ? View.GONE : View.VISIBLE);
        tvMsg.setText(msg);
        if (msg != null)
        {
            new CountDownTimer(1500, 900)
            {
                public void onTick(long millisUntilFinished)
                {}

                public void onFinish()
                {
                    finish();
                }
            }.start();
        }
    }

    @Override
    public void onBackPressed()
    {

    }
}
