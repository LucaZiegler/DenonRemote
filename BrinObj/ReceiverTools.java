package com.brin.denonremotefree.BrinObj;

import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by Luca on 15.05.2016.
 */
public class ReceiverTools
{
    private static final String TAG = "REC.TOOLS";
    public static String ReceiverStoredToJson(ReceiverStored stored)
    {
        if (stored != null)
        {
            String r =  new Gson().toJson(stored);
            Log.d(TAG, "ReceiverStoredToJson: "+r);
            return r;
        } else
        {
            return null;
        }
    }

    public static ReceiverStored ReceiverStoredFromJson(String json)
    {
        if (json != null)
        {
            return new Gson().fromJson(json, ReceiverStored.class);
        } else
        {
            return null;
        }
    }

    public static String newElementToJson(NewElement ne)
    {
        if (ne != null)
        {
            return new Gson().toJson(ne);
        } else
        {
            return null;
        }
    }

    public static NewElement newElementFromJson(String json)
    {
        if (json != null)
        {
            return new Gson().fromJson(json, NewElement.class);
        } else
        {
            return null;
        }
    }
}
