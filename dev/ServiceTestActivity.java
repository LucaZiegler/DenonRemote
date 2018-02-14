package com.brin.denonremotefree.dev;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.BrinObj.ReceiverStored;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.service.ReceiverConnectionService;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ServiceTestActivity extends BrinActivity
{

    @Bind(R.id.btServiceStart) Button btStart;
    @Bind(R.id.btServiceStop) Button btStop;
    @Bind(R.id.btServiceSend) Button btSend;
    @Bind(R.id.tvServiceInfo) TextView tvInfo;
    @Bind(R.id.etService) EditText etCom;
    @Bind(R.id.etServiceIp) EditText etIp;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_test);
        ButterKnife.bind(this);
        btStart.setEnabled(!ReceiverConnectionService.serviceActive);
        btStop.setEnabled(ReceiverConnectionService.serviceActive);
        btSend.setEnabled(ReceiverConnectionService.socketConnected);
    }

    @Override
    public void onTelnetResult(String l)
    {
        addLine(l);
    }

    private void addLine(String l)
    {
        String timeStamp = new SimpleDateFormat("mm.ss.SSS").format(new Date());
        tvInfo.setText(timeStamp+": "+l + "\n"  + tvInfo.getText().toString());
    }

    public void onSend(View v)
    {
        String c = etCom.getText().toString();
        sendTelnetCom(c);
        addLine("on send: "+c);
    }

    public void onSetIp(View v)
    {
        v.setEnabled(false);
    }

    public void onClear(View v)
    {
        tvInfo.setText(null);
    }

    public void onStartService(View v)
    {
        ReceiverStored rc = new ReceiverStored();
        rc.receiverIp = etIp.getText().toString();
        rc.receiverZones = 1;
        startTelnetService(rc,false);
        addLine("starting service...");
    }

    public void onStopService(View v)
    {
        stopTelnetService();
    }

    @Override
    public void onConnecting()
    {
        addLine("connecting...");
        btStart.setEnabled(false);
        btStop.setEnabled(true);
        btSend.setEnabled(false);
    }

    @Override
    public void onConnectionError(int err, String ip)
    {
        addLine(ip+"connection error: "+err);
        btStart.setEnabled(true);
        btStop.setEnabled(false);
        btSend.setEnabled(false);
    }

    @Override
    public void onConnected(String hostName, String ipAddress)
    {
        addLine("connected with: "+hostName);
        btStart.setEnabled(false);
        btStop.setEnabled(true);
        btSend.setEnabled(true);
    }

    @Override
    public void onBroadcastOpen(boolean isConn)
    {
        addLine("broadcast is open, is "+ (isConn ? "":"not")+" connected");
    }
}
