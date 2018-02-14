package com.brin.denonremotefree;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by Luca on 25.09.2015.
 */
public class ReturnClass
{
    public static String getUserId(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
    }
}
