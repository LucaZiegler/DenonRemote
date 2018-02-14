package com.brin.denonremotefree.BrinObj;

import com.google.gson.Gson;

import java.text.MessageFormat;

/**
 * Created by Luca on 12.06.2016.
 */
public class Zone
{
    public interface ZoneInter
    {
        int REP_OFF = 0, REP_ALL = 1, REP_ONE = 2;
    }

    private String input;
    private String sound;
    private String volume;
    private String[] mute;
    private String fav;
    private boolean isMainZone;
    private int zoneId = -1;
    private String[] power;
    private int repeatState = -1;
    private boolean randomState = false;

    public Zone(int id, int count)
    {
        zoneId = id;
        isMainZone = id == 1;
        String zoneSpecific = MessageFormat.format("Z{0}", id);
        input = isMainZone ? "SI" : zoneSpecific;
        sound = isMainZone ? "MS" : zoneSpecific;
        volume = isMainZone ? "MV" : zoneSpecific;
        mute = new String[]{isMainZone ? "MUOFF" : zoneSpecific + "MUOFF", isMainZone ? "MUON" : zoneSpecific + "MUON", isMainZone ? "MU?" : zoneSpecific + "MU?"};
        fav = isMainZone ? "ZM" : zoneSpecific;
        power = new String[]{isMainZone ? (count > 1 ? "ZM?" : "PW?") : (zoneSpecific + "?"), isMainZone ? (count > 1 ? "ZMOFF" : "PWSTANDBY") : (zoneSpecific + "OFF"), isMainZone ? (count > 1 ? "ZMON" : "PWON") : (zoneSpecific + "ON")};
    }

    public static String getDefaultZoneName(int z)
    {
        switch (z)
        {
            case 1:
                return "Main Zone";
            default:
                return "Zone " + z;
        }
    }

    public String getZoneCaller()
    {
        if (!isMainZone)
            return "Z" + zoneId;
        else
            return null;
    }

    public String setPower(boolean on)
    {
        return on ? power[2] : power[1];
    }

    public String getPower()
    {
        return power[0];
    }

    public String setInput(String inp)
    {
        return input + inp;
    }

    public String getInput()
    {
        return input + "?";
    }

    public String setSound()
    {
        return sound;
    }

    public String getSound()
    {
        return sound + "?";
    }

    public String setVolume()
    {
        return volume;
    }

    public String setVolumeUp()
    {
        return volume + "UP";
    }

    public String setVolumeDn()
    {
        return volume + "DOWN";
    }

    public String getVolume()
    {
        return volume + "?";
    }

    public String setMute(boolean on)
    {
        return mute[on ? 1 : 0];
    }

    public String getMute()
    {
        return mute[2];
    }

    public String setFav(int i)
    {
        return fav + "FAVORITE" + i;
    }

    public String setQuick(int i)
    {
        return sound + "QUICK" + i;
    }

    public String getQuick()
    {
        return sound + "QUICK ?";
    }

    public String setFavMemory(int wich)
    {
        return fav + "FAVORITE" + wich + " MEMORY";
    }

    public String setQuickMemory(int wich)
    {
        return sound + "QUICK" + wich + " MEMORY";
    }

    public boolean isMainZone()
    {
        return isMainZone;
    }

    public int getZoneId()
    {
        return zoneId;
    }

    public String toJson()
    {
        try
        {
            return new Gson().toJson(this);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String ZoneToJson(Zone zone)
    {
        if (zone == null)
            return null;
        try
        {
            return new Gson().toJson(zone);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Zone ZoneFromJson(String jsonString)
    {
        if (jsonString == null) return null;
        try
        {
            return new Gson().fromJson(jsonString, Zone.class);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public int getRepeatState()
    {
        return repeatState;
    }

    public void setRepeatState(int repeatState)
    {
        this.repeatState = repeatState;
    }

    public boolean getRandomState()
    {
        return randomState;
    }

    public void setRandomState(boolean randomState)
    {
        this.randomState = randomState;
    }
}
