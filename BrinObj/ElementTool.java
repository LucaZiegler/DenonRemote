package com.brin.denonremotefree.BrinObj;

import android.util.Log;

import com.brin.denonremotefree.Helper.Prefs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by Luca on 09.06.2016.
 */
public class ElementTool
{
    private static final String TAG = "ElementTool";



    public static String DragElementToJson(DragElement element)
    {
        if (element == null)
            return null;
        Log.d(TAG, "ElementToJson: is not null");
        try
        {
            return new Gson().toJson(element);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static DragElement DragElementFromJson(String jsonString)
    {
        if (jsonString == null) return null;
        Log.d(TAG, "ElementToJson: ");
        try
        {
            return new Gson().fromJson(jsonString, DragElement.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String DragElementListToJson(ArrayList<DragElement> element)
    {
        if (element == null)
            return null;
        Log.d(TAG, "ElementToJson: is not null");
        try
        {
            return new Gson().toJson(element);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<DragElement> DragElementListFromJson(String jsonString)
    {
        if (jsonString == null) return null;
        Log.d(TAG, "ElementToJson: ");
        try
        {
            //Type collectionType = new TypeToken<ArrayList<channelSearchEnum>>(){}.getType();
            return new Gson().fromJson(jsonString, new TypeToken<ArrayList<DragElement>>() {}.getType());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String HomeTabToJson(HomeTab homeTab)
    {
        if (homeTab == null)
            return null;
        try
        {
            return new Gson().toJson(homeTab);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static HomeTab HomeTabFromJson(String jsonString)
    {
        if (jsonString == null) return null;
        try
        {
            return new Gson().fromJson(jsonString, HomeTab.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String GenerateElementTitle(DragElement element, AddableElement e, Prefs prefs)
    {
        if (prefs.deviceZones() > 1 && e.isElementZoneCompatible())
        {
            return MessageFormat.format("{0} {1}",e.getElementTitle(), prefs.getDeviceZoneName(element.elementZone));
        } else
        {
            return e.getElementTitle();
        }
    }
}
