package com.brin.denonremotefree.Helper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.brin.denonremotefree.R;
import com.brin.denonremotefree.binding.ReceiverListActivity;
import com.brin.denonremotefree.service.ReceiverConnectionService;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class StopApp extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_app);
        boolean b = getIntent().getBooleanExtra("DISCONNECT",false);
        ReceiverConnectionService.stopService(getApplicationContext());
        if (!ReceiverListActivity.isActivityActive())
        {
            if (ReceiverListActivity.isActivityVisible())
            {
                finish();
            } else
            {
                if (b)
                CustomActivityOnCrash.restartApplicationWithIntent(this, new Intent(getApplicationContext(), ReceiverListActivity.class).putExtra("DISCONNECT",b));
            }
        }
        finish();
    }
}
