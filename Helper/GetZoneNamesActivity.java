package com.brin.denonremotefree.Helper;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.util.Log;
import android.view.View;
import android.widget.ViewFlipper;

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
import com.brin.denonremotefree.widgets.BrinToolbar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GetZoneNamesActivity extends BrinActivity
{
    private static final String TAG = "GET.ZONE.NAMES";
    @Bind(R.id.tbGetZoneNames) BrinToolbar tbMain;
    @Bind(R.id.blGetZoneNames) AppBarLayout tlMain;
    @Bind(R.id.vfGetZoneNames) ViewFlipper vfMain;
    private RequestQueue queue;
    private Prefs prefs;
    private ReceiverStored rc;

    @Override
    public boolean broadcastEnabled()
    {
        return false;
    }

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.activity_get_zone_names);
        ButterKnife.bind(this);
        setResult(RESULT_CANCELED);
        setToolbar(tbMain);
        setTitle("download zone names");
        setNavBack(true);
        enableToolbarProgess(tlMain);
        tbMain.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }

        });
        queue = Volley.newRequestQueue(this);
        rc = ReceiverTools.ReceiverStoredFromJson(getIntent().getStringExtra("RECEIVER"));
        if (rc == null)
        {
            Log.e(TAG, "onCreate: RECEIVER IS NULL");
            finish();
            return;
        }
        prefs = new Prefs(this, rc.storedTime);
        setProgressMax(prefs.deviceZones());
        downloadZonesNames();
    }

    private void downloadZonesNames()
    {
        setSubTitle(getString(R.string.please_wait___).toUpperCase());
        for (int zs = 1; zs <= prefs.deviceZones(); zs++)
        {
            final int zone = zs;
            String u;
            switch (zs)
            {
                case 1:
                    u = url.mainInfo;
                    break;
                default:
                    u = url.zoneInfo + zone;
                    break;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + prefs.deviceHostname() + u, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    onDownloadResponse(zone, response);
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    onDownloadResponse(zone, null);
                }
            });
            queue.add(stringRequest);
        }
    }

    public void onFinish(View v)
    {
        onBackPressed();
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void onDownloadResponse(final int zone, final String response)
    {
        try
        {
            Log.d(TAG, "onDownloadResponse: " + response);
            if (response != null)
            {
                String name;
                final String cont1 = "<RenameZone><value>";
                final String cont2 = "</value></RenameZone>";
                if (response.contains(cont1) && response.contains(cont2))
                {
                    name = response.substring(response.indexOf(cont1) + cont1.length());
                    name = name.substring(0, name.indexOf(cont2));
                    name = name.trim();
                    Log.d(TAG, "onDownloadResponse: ZONE"+zone+"/"+name);
                    if (name.length() > 0)
                    {
                        prefs.putDeviceZoneName(zone, name);
                    } else
                    {
                        throw new PackageManager.NameNotFoundException("Name too short");
                    }
                } else
                {
                    throw new Resources.NotFoundException("Tag not found");
                }
            } else
            {
                throw new NullPointerException("response is null");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, "onDownloadResponse: ", e);
        } finally
        {
            setProgress(false, zone);
            if (zone == prefs.deviceZones())
            {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}