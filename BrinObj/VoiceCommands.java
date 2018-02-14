package com.brin.denonremotefree.BrinObj;

import com.google.gson.Gson;

/**
 * Created by Luca on 21.11.2016.
 */

public class VoiceCommands
{
    public static String ToJson(VoiceCommand stored)
    {
        if (stored != null)
        {
            return new Gson().toJson(stored);
        } else
        {
            return null;
        }
    }

    public static VoiceCommand FromJson(String json)
    {
        if (json != null)
        {
            return new Gson().fromJson(json, VoiceCommand.class);
        } else
        {
            return null;
        }
    }
}
