package com.brin.denonremotefree.Helper;

import android.util.Log;

import com.brin.denonremotefree.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Luca on 29.06.2016.
 */
public class PlayerHelper
{
    private static final String TAG = "PLAYER.HELPER";

    public static boolean check(String l)
    {
        return (!l.startsWith("@") && !l.startsWith("PW") && !l.startsWith("Z2") && !l.startsWith("CV") && !l.startsWith("FAN0") && !l.startsWith("PSRSTR") && !l.startsWith("ZM") && !l.startsWith("DC") && !l.startsWith("SD") && !l.startsWith("Z3") && !l.startsWith("Z4") && !l.startsWith("SV") && !l.startsWith("SI") && !l.startsWith("MS") && !l.startsWith("MV") && !l.startsWith("BD") && !l.startsWith("SS") && !l.startsWith("NSL") && !l.startsWith("NSS"));
    }

    public static String UpnpPost(String a, String b, String c) throws Exception
    {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try
        {
            URL url = new URL(a);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
            conn.setRequestProperty("SOAPACTION", b);

            byte[] outputInBytes = c.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();

            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readIt(is, len);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally
        {
            if (is != null)
            {
                is.close();
            }
        }
    }

    private static String readIt(InputStream stream, int len) throws Exception
    {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        Log.d("UPNP.RES", new String(buffer));
        return new String(buffer);
    }

    public static int getNSEFromLine(String l, boolean checked)
    {
        if (!checked) if (!l.startsWith("NSE")) return -3;
        try
        {
            String n = l.substring(3, 4);
            return Integer.valueOf(n);
        } catch (Exception e)
        {
            e.printStackTrace();
            return -2;
        }
    }

    public static boolean contAllNSE(String r)
    {
        boolean t = true;
        for (int i = 0; i < 9; i++)
        {
            if (r.contains("NSE" + i))
            {
                t = false;
            }
        }
        return t;
    }

    public static int getIcon(String s)
    {
        s = s.trim();
        Log.d(TAG, "getIcon: " + s);
        switch (s)
        {
            case "Favorites":
                return R.drawable.like_100_white;
            case "Spotify":
                return R.drawable.spotify_100_white;
            case "Media Server":
                return R.drawable.mserver_100_white;
            case "Last.Fm":
                return R.drawable.net_100_white;
            case "Flickr":
                return R.drawable.net_100_white;
            case "Internet Radio":
                return R.drawable.iradio_100_white;
        }
        return -1;
    }
}
