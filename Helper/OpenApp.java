package com.brin.denonremotefree.Helper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.brin.denonremotefree.DashboardActivity;
import com.brin.denonremotefree.R;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class OpenApp extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.activity_open_app);
        long rcr = getIntent().getLongExtra("RECEIVER_TAG", -1);
        if (DashboardActivity.isActivityActive())
        {
            if (DashboardActivity.isActivityVisible())
            {
                finish();
            } else
            {
                CustomActivityOnCrash.restartApplicationWithIntent(this, new Intent(getApplicationContext(), DashboardActivity.class).putExtra("RECEIVER_TAG",rcr));
            }
        } else
        {
            if (DashboardActivity.isActivityVisible())
            {
                finish();
            } else
            {
                startActivity(new Intent(this, DashboardActivity.class).putExtra("RECEIVER_TAG",rcr));
                finish();
            }
        }
        finish();
    }
}
