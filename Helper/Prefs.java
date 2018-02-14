package com.brin.denonremotefree.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.brin.denonremotefree.BrinObj.ElementTool;
import com.brin.denonremotefree.BrinObj.HomeTab;
import com.brin.denonremotefree.BrinObj.ReceiverStored;
import com.brin.denonremotefree.BrinObj.ReceiverTools;
import com.brin.denonremotefree.BrinObj.Zone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Luca on 12.06.2016.
 */
public class Prefs
{
    public static final String SAV_HOM_ELMTS = "rdfth3";
    public static final String SAV_HOM_LIST = "STORED_ELEMENTS";
    public static final String SAV_HOM_TABS = "phTXrFM6Ex_";
    public static final String SAV_RCR = "STORED_RECEIVER";
    public static final String SAV_OBJ = "REC";
    public static final String SAV_VOICE = "STORED_VOICE";
    public static final String SAV_ZONE_NAME = "STORED_ZONE_NAME";
    public static final int MAX_TABS = 5;
    private SharedPreferences spLocal, spGlobal;
    //private boolean deviceZoneNamesNull = true;
    private ReceiverStored rc;
    private static final String TAG = "PREFS";
    private final String licensedTag1 = "pWGFTXrcd3Ex_";
    private final String licensedTag2 = "L§LFG)=§Ehe33rt";
    private final String prefFirstStart = "FIRST_START";
    private final String prefCheckBeta = "M;JNB§I(/UZkhbjdsf3";
    private final String prefSkipVersion = "elkwghbnewGi43ktbkdjbg|";

    public Prefs(Context c)
    {
        Log.d(TAG, "Prefs: GLOBAL");
        spGlobal = PreferenceManager.getDefaultSharedPreferences(c);
        rc = null;
    }

    public Prefs(Context c, long receiverTag)
    {
        Log.d(TAG, "Prefs: " + receiverTag);
        spLocal = c.getSharedPreferences(String.valueOf(receiverTag), Context.MODE_PRIVATE);
        spGlobal = PreferenceManager.getDefaultSharedPreferences(c);
        rc = ReceiverTools.ReceiverStoredFromJson(getGlobString(SAV_OBJ, null));
        if (rc == null) Log.e(TAG, "Prefs: CRITICAL ERROR! RECEIVER STORED IS NULL!");
        //Log.d(TAG, "Prefs: " + ReceiverTools.ReceiverStoredToJson(rc));
    }

    public Prefs(Context c, ReceiverStored rcNew)
    {
        Log.d(TAG, "Prefs: " + rcNew.storedTime);
        //Log.d(TAG, "Prefs: " + ReceiverTools.ReceiverStoredToJson(rcNew));
        rc = rcNew;
        spLocal = c.getSharedPreferences(String.valueOf(rc.storedTime), Context.MODE_PRIVATE);
        spGlobal = PreferenceManager.getDefaultSharedPreferences(c);
        saveData();
    }

    public boolean isDevOptionsActivated()
    {
        return getGlobBool("switch_preference_3_0", false);
    }

    public void skipVersionsCode(int code)
    {
        putGlobBool(prefSkipVersion + code, true);
    }

    public boolean isSkipVersion(int code)
    {
        return getGlobBool(prefSkipVersion + code, false);
    }

    public boolean checkBetaUpdate()
    {
        try
        {
            return getGlobBool(prefCheckBeta, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isVoiceAssistantEnabled()
    {
        return getGlobBool("switch_preference_3_1", false);
    }

    public boolean isFirstStart()
    {
        try
        {
            boolean s = getGlobBool(prefFirstStart, true);
            if (s)
            {
                spGlobal.edit().clear().apply();
                putGlobBool(prefFirstStart, false);
            }
            return s;
        }
        catch (Exception e)
        {
            Log.e(TAG, "isFirstStart: ", e);
            e.printStackTrace();
            return true;
        }
    }

    public boolean isVolControlEnabled()
    {
        try
        {
            return getGlobBool("switch_preference_1_0", true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return true;
        }
    }

    public boolean isReceiverDemo()
    {
        return rc.deviceDemo;
    }

    public boolean isDashboardFirstOpened()
    {
        try
        {
            boolean f = getLocBool("FIRST", true);
            putLocBool("FIRST", false);
            return f;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public void putInputRename(int id, String name)
    {
        Log.d(TAG, "putInputRename: " + id + "/" + name);
        putLocString("INPUT" + id, name);
    }

    public String getInputRename(int id)
    {
        String n = getLocString("INPUT" + id, null);
        Log.d(TAG, "getInputRename: " + id + "/" + n);
        return n;
    }

    public ReceiverStored returnReceiver()
    {
        return rc;
    }

    public Set<String> getElements(int tab)
    {
        return getLocStringSet(Prefs.SAV_HOM_ELMTS + tab, new HashSet<String>());
    }

    public Set<String> getVoiceCommands()
    {
        return getLocStringSet(rc.storedTime + Prefs.SAV_VOICE, new HashSet<String>());
    }

    public void setVoiceCommands(Set<String> s)
    {
        putLocStringSet(Prefs.SAV_VOICE, s);
    }

    public boolean isAppActivated()
    {
        return isAppActivated1() || isAppActivated2();
    }

    public boolean isAppActivated1()
    {
        try
        {
            return getGlobBool(licensedTag1, false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public void activateApp1(boolean to)
    {
        putGlobBool(licensedTag1, to);
    }

    public boolean isAppActivated2()
    {
        try
        {
            return getGlobBool(licensedTag2, false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public void activateApp2(boolean to)
    {
        putGlobBool(licensedTag2, to);
    }

    public boolean hasUserGivenFeedback()
    {
        return getGlobBool("FB_SENT", false);
    }

    public void userGaveFeedback()
    {
        putGlobBool("FB_SENT", true);
    }

    public long appDashCount()
    {
        long c = getGlobLong("DASH_COUNT", 0);
        c++;
        putGlobLong("DASH_COUNT", c);
        return c;
    }

    public void deleteReceiver()
    {
        if (rc != null)
        {
            spLocal.edit().clear().apply();
            /*
            deletePref(String.valueOf(rc.storedTime));
            deletePref(rc.storedTime + SAV_HOM_ELMTS);
            deletePref(rc.storedTime + SAV_HOM_TABS);
            for (int i = 1; i <= rc.receiverZones; i++)
            {
                deletePref(rc.storedTime + SAV_ZONE_NAME + i);
            }
            */
        }
    }

    private void saveData()
    {
        try
        {
            putGlobString(SAV_OBJ, ReceiverTools.ReceiverStoredToJson(rc));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean deviceSupportMultiZone()
    {
        return deviceZones() > 1;
    }

    public String deviceIp()
    {
        try
        {
            return rc.receiverIp;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public String deviceName()
    {
        try
        {
            return rc.receiverAltName == null ? deviceHostname() : rc.receiverAltName;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "null";
        }
    }

    public String deviceHostname()
    {
        try
        {
            return rc.receiverHostName;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void putDeviceHostname(String n)
    {
        try
        {
            Log.d(TAG, "putDeviceHostname: "+n);
            rc.receiverHostName = n;
            saveData();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public int deviceZones()
    {
        try
        {
            return rc.receiverZones;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    /*
        public boolean needUpdateZoneNames()
        {
            try
            {
                return rc.receiverZoneNames[0] == null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return true;
            }
        }
    */

    public long getStoredTime()
    {
        return rc.storedTime;
    }

    public String getDeviceZoneName(int z)
    {
        try
        {
            return getLocString(rc.storedTime + SAV_ZONE_NAME + z, Zone.getDefaultZoneName(z));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return Zone.getDefaultZoneName(z);
        }
    }

    public void putDeviceZoneName(int z, String name)
    {
        try
        {
            putLocString(rc.storedTime + SAV_ZONE_NAME + z, name);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public int deviceManufacturer()
    {
        try
        {
            return rc.receiverManufacturer;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public void putLocString(String name, String content)
    {
        Log.d(TAG, "putString: " + name + "/" + content);
        spLocal.edit().putString(name, content).apply();
    }

    private void putGlobString(String name, String content)
    {
        Log.d(TAG, "putString: " + name + "/" + content);
        spGlobal.edit().putString(name, content).apply();
    }

    public void putLocBool(String name, boolean content)
    {
        try
        {
            spLocal.edit().putBoolean(name, content).apply();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void putGlobBool(String name, boolean content)
    {
        try
        {
            spGlobal.edit().putBoolean(name, content).apply();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getLocString(String name, String ifNull)
    {
        return spLocal.getString(name, ifNull);
    }

    public boolean getGlobBool(String name, boolean ifNull)
    {
        return spGlobal.getBoolean(name, ifNull);
    }

    public long getGlobLong(String name, long ifNull)
    {
        return spGlobal.getLong(name, ifNull);
    }

    public String getGlobString(String name, String ifNull)
    {
        return spGlobal.getString(name, ifNull);
    }

    public boolean getLocBool(String name, boolean ifNull)
    {
        return spLocal.getBoolean(name, ifNull);
    }

    public Set<String> getLocStringSet(String name, Set<String> ifNull)
    {
        return spLocal.getStringSet(name, ifNull);
    }

    public Set<String> getGlobStringSet(String name, Set<String> ifNull)
    {
        return spGlobal.getStringSet(name, ifNull);
    }

    public void putLocStringSet(String name, Set<String> set)
    {
        Log.d(TAG, "putStringSet: " + set);
        spLocal.edit().putStringSet(name, set).apply();
    }

    public void putGlobStringSet(String name, Set<String> set)
    {
        Log.d(TAG, "putStringSet: " + set);
        spGlobal.edit().putStringSet(name, set).apply();
    }

    public void putGlobLong(String name, long l)
    {
        spGlobal.edit().putLong(name, l).apply();
    }

    public ArrayList<HomeTab> loadTabs()
    {
        ArrayList<HomeTab> alTabs = new ArrayList<>();
        Set<String> stTabs = new HashSet<>();
        stTabs = getLocStringSet(Prefs.SAV_HOM_TABS, stTabs);
        if (!stTabs.isEmpty())
        {
            for (String s : stTabs)
            {
                HomeTab ht = ElementTool.HomeTabFromJson(s);
                alTabs.add(ht);
            }

            sortTabList(alTabs);
        } else
        {
            // EMPTY
            Log.e(TAG, "loadTabSet: TAB SET EMPTY");
        }
        return alTabs;
    }

    public ArrayList<ReceiverStored> loadReceiver()
    {
        Set<String> stReceiver = getGlobStringSet(Prefs.SAV_RCR, new HashSet<String>());
        ArrayList<ReceiverStored> alReceiver = new ArrayList<>();
        if (!stReceiver.isEmpty())
        {
            for (String s : stReceiver)
            {
                ReceiverStored ht = ReceiverTools.ReceiverStoredFromJson(s);
                alReceiver.add(ht);
            }

            sortReceiverList(alReceiver);
        } else
        {
            // EMPTY
            Log.e(TAG, "loadTabSet: TAB SET EMPTY");
        }
        return alReceiver;
    }

    public void storeReceiver(ArrayList<ReceiverStored> alReceiver)
    {
        try
        {
            Set<String> stReceiver = new HashSet<>();

            for (ReceiverStored e : alReceiver)
            {
                String row = ReceiverTools.ReceiverStoredToJson(e);
                stReceiver.add(row);
            }

            if (stReceiver.size() == 0)
            {
                putGlobStringSet(Prefs.SAV_RCR, null);
            } else
            {
                putGlobStringSet(Prefs.SAV_RCR, stReceiver);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void sortReceiverList(ArrayList<ReceiverStored> al)
    {
        Collections.sort(al, new Comparator<ReceiverStored>()
        {
            @Override
            public int compare(ReceiverStored rc1, ReceiverStored rc2)
            {
                return String.valueOf(rc2.storedTime).compareTo(String.valueOf(rc1.storedTime)) * -1;
            }
        });
    }

    private void sortTabList(ArrayList<HomeTab> al)
    {
        Collections.sort(al, new Comparator<HomeTab>()
        {
            @Override
            public int compare(HomeTab lhs, HomeTab rhs)
            {
                return String.valueOf(lhs.getTabId()).compareTo(String.valueOf(rhs.getTabId()));
            }
        });
    }

    public void storeTabs(ArrayList<HomeTab> alTabs)
    {
        try
        {
            Set<String> stTabs = new HashSet<>();

            for (HomeTab e : alTabs)
            {
                String row = ElementTool.HomeTabToJson(e);
                stTabs.add(row);
            }

            if (stTabs.size() == 0)
            {
                putLocStringSet(Prefs.SAV_HOM_TABS, null);
            } else
            {
                putLocStringSet(Prefs.SAV_HOM_TABS, stTabs);
                Log.d(TAG, "storeElementSet: ALL/" + stTabs);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
