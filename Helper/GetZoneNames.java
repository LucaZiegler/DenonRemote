package com.brin.denonremotefree.Helper;

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brin.denonremotefree.DashboardActivity;
import com.brin.denonremotefree.db.url;

/**
 * Created by Luca on 14.09.2016.
 */

public class GetZoneNames
{
    private static final String TAG = "DOWN.ZONE.NAMES";

    public GetZoneNames(GetZoneNamesRequest face)
    {
        downloadZonesNames(face);
    }

    private void onDownloadResponse(final int zone, final String response, GetZoneNamesRequest face)
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
                        DashboardActivity.getPrefs().putDeviceZoneName(zone, name);
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
            if (zone == DashboardActivity.getPrefs().deviceZones())
            {
                face.onDone();
            }
        }
    }

    private void downloadZonesNames(final GetZoneNamesRequest face)
    {
        face.onStart();
        for (int zs = 1; zs <= DashboardActivity.getPrefs().deviceZones(); zs++)
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
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + DashboardActivity.getPrefs().deviceHostname() + u, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    onDownloadResponse(zone, response,face);
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    onDownloadResponse(zone, null,face);
                }
            });
            DashboardActivity.rqMain.add(stringRequest);
        }
    }

    public interface GetZoneNamesRequest
    {
        void onStart();
        void onDone();
    }
}
