package com.brin.denonremotefree.Helper;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.BrinObj.ReceiverStored;
import com.brin.denonremotefree.BrinObj.ReceiverTools;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.db.url;
import com.brin.denonremotefree.service.ReceiverConnectionService;
import com.brin.denonremotefree.widgets.BrinToolbar;

import java.text.MessageFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConnectActivity extends BrinActivity
{

    private static final String TAG = "CONNECT.ACT";
    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinIcon) ImageView ivMsg;
    @Bind(R.id.brinText1) TextView tvMsg;
    @Bind(R.id.brinProgressBar) ProgressBar pbMain;
    @Bind(R.id.brinProgressBar2) ProgressBar pbWait;
    private ReceiverStored rc;

    public static boolean isActivityVisible = false;
    public static boolean isActivityActive = false;

    @Override
    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        setCrashReporter();
        setContentView(R.layout.activity_connect3);
        ButterKnife.bind(this);

        setToolbar(tbMain);
        enableCustomProgress(pbMain);
        setTitle(getString(R.string.connection_assistant));
        setNavBack(true);
        setProgress(true, 1);
        Log.d(TAG, "onCreate: START");

        try
        {
            rc = ReceiverTools.ReceiverStoredFromJson(getIntent().getStringExtra(BrinActivity.INTENT_RCR_OBJ));
            prefs = new Prefs(this, rc.storedTime);
        } catch (Exception e)
        {
            rc = null;
        }
    }

    private void updateStatus(boolean wait, String msg, final boolean error, int countDown)
    {
        tvMsg.setText(msg);
        pbWait.setVisibility(wait ? View.VISIBLE : View.GONE);
        ivMsg.setVisibility(wait ? View.GONE : View.VISIBLE);
        ivMsg.setImageResource(error ? R.drawable.error_100_white : R.drawable.approval_100_white);

        if (!wait)
        {
            if (countDown <= 0)
            {
                activityDone(!error);
                return;
            }
            proConnAnimActive = false;
            setProgress(false, 0);
            final int max = countDown / 10;
            setProgressMax(max);
            if (countDown > 0)
            {
                new CountDownTimer(countDown, 10)
                {
                    public void onTick(long millisUntilFinished)
                    {
                        setProgress(false, max - ((int) millisUntilFinished / 10));
                    }

                    public void onFinish()
                    {
                        activityDone(!error);
                    }
                }.start();
            }
        }
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause: ");
        super.onPause();
        super.closeBroadcast();
        isActivityVisible = false;
        isActivityActive = true;
        if (!success)
            cancel();
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume: ");
        super.onResume();
        isActivityVisible = true;
        isActivityActive = true;
        openBroadcast();
        connect();
    }

    @Override
    public void onStop()
    {
        Log.d(TAG, "onStop: ");
        super.onStop();
        isActivityVisible = false;
        isActivityActive = false;
        closeBroadcast();
    }

    @Override
    public void onStart()
    {
        Log.d(TAG, "onStart: ");
        super.onStart();
        isActivityVisible = true;
        isActivityActive = true;
        openBroadcast();
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        isActivityVisible = false;
        isActivityActive = false;
        closeBroadcast();
    }

    public static boolean isActivityVisible()
    {
        return isActivityVisible;
    }

    public static boolean isActivityActive()
    {
        return isActivityActive;
    }

    public void onFinish(View v)
    {
        onBackPressed();
    }

    private void cancel()
    {
        //stopTelnetService();
        finish();
    }

    @Override
    public void onBackPressed()
    {
        cancel();
    }


    private boolean proConnAnimActive = false;

    @Override
    public void onConnecting()
    {
        super.onConnecting();
        updateStatus(true, getString(R.string.connecting), false, 0);
        proConnAnimActive = true;
        setProgressMax(500);
        new CountDownTimer(5000, 10)
        {
            public void onTick(long millisUntilFinished)
            {
                if (proConnAnimActive)
                    setProgress(false, 500 - ((int) millisUntilFinished / 10));
                else
                    cancel();
            }

            public void onFinish()
            {
                setProgress(true, 1);
            }
        }.start();
    }

    private boolean success = false;

    @Override
    public void onConnected(String hostName, String ipAddress)
    {
        success = true;
        super.onConnected(hostName, ipAddress);
        proConnAnimActive = false;
        prefs = new Prefs(this, rc.storedTime);
        //showMSG(R.drawable.checkmark_100_white, MessageFormat.format("{0}\n{1}", getString(R.string.connected_with), rc.receiverAltName == null ? hostName : rc.receiverAltName), rc.receiverPrimary ? 500 : 1500, false);
        updateStatus(false, MessageFormat.format("{0}\n{1}", getString(R.string.connected_with), rc.receiverAltName == null ? hostName : rc.receiverAltName), false, rc.receiverPrimary ? 0 : 0);
    }

    @Override
    public void onConnectionError(int err, String ip)
    {
        super.onConnectionError(err, ip);
        Log.d(TAG, "onConnectionError: " + err);
        switch (err)
        {
            case 0:
                updateStatus(false,getString(R.string.con_error_timeout),true,2000);
                //showMSG(R.drawable.error_100_white, getString(R.string.con_error_timeout), 2000, true);
                break;
            case 2:
                if (!VolleyRepairConnection(ip))
                {
                    updateStatus(false,getString(R.string.con_error_refused),true,3000);
                    //showMSG(R.drawable.error_100_white, getString(R.string.con_error_refused), 3000, true);
                    //Snackbar.make(clRoot, getString(R.string.receiver_list_other_connection_warn), Snackbar.LENGTH_LONG).show();
                    //retried = false;
                }
                break;
            case 3:
                updateStatus(false,getString(R.string.con_error_host),true,3000);
                //showMSG(R.drawable.error_100_white, getString(R.string.con_error_host), 3000, true);
                break;
            case 4:
                updateStatus(false,getString(R.string.con_error_host),true,3000);
                //showMSG(R.drawable.error_100_white, getString(R.string.con_error_host), 3000, true);
                break;
            default:
                updateStatus(false,getString(R.string.error_connection_unknown),true,2000);
                //showMSG(R.drawable.error_100_white, getString(R.string.error_connection_unknown), 2000, true);
                break;
        }
    }

    private boolean retried = false;

    private boolean VolleyRepairConnection(final String i)
    {
        if (!retried)
        {
            try
            {
                if (ReceiverConnectionService.serviceActive)
                {
                    Log.d(TAG, "VolleyRepairConnection: stopping service");
                    stopTelnetService();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            updateStatus(true,getString(R.string.info_try_repair),false,-1);
            RequestQueue queue = Volley.newRequestQueue(this);
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + i + url.netStatus1, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String result)
                {
                    connect();
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    updateStatus(false,getString(R.string.con_error_refused),true,2000);
                    //showMSG(R.drawable.error_100_white, getString(R.string.con_error_refused), 2000, true);
                }
            });
            queue.add(stringRequest);
            retried = true;
            return true;
        } else
        {
            return false;
        }
    }

    private void connect()
    {
        try
        {
            updateStatus(true,getString(R.string.connecting),false,-1);
            //showWait(getString(R.string.connecting));
            if (checkIntent())
            {
                if (checkService())
                {
                    if (rc.deviceDemo)
                    {
                        onConnected(rc.receiverHostName,rc.receiverIp);
                    } else
                    {
                        startTelnetService(rc, false);
                    }
                } else
                {
                    throw new Exception("Service error");
                }
            } else
            {
                throw new Exception("Intent error");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            updateStatus(false,e.getMessage(),true,3000);
            //showMSG(R.drawable.error_100_white, e.getMessage(), 3000, true);
        }
    }

    private boolean checkIntent()
    {
        try
        {
            if (rc == null)
            {
                updateStatus(false,"intent error",true,3000);
                //showMSG(R.drawable.error_100_white, "intent error", 3000, true);
                return false;
            }
            if (rc.storedTime > 0 && rc.receiverHostName != null && rc.receiverZones > 0)
            {
                return true;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkService()
    {
        try
        {
            if (ReceiverConnectionService.socketConnected)
            {
                if (ReceiverConnectionService.getReceiverStored().storedTime == rc.storedTime)
                {
                    activityDone(true);
                } else
                {
                    stopTelnetService();
                    return true;
                }
            }
            if (ReceiverConnectionService.serviceConnecting)
            {
                stopTelnetService();
                return true;
            }
        } catch (Exception e)
        {
            return false;
        }
        return true;
    }

    private void activityDone(boolean success)
    {
        if (success)
        {
            Intent i = new Intent();
            i.putExtra(BrinActivity.INTENT_RCR_OBJ, rc.storedTime);
            setResult(RESULT_OK, i);
        } else
        {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

}
