package com.brin.denonremotefree.Helper;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by Luca on 20.07.2016.
 */
public class FileHelper
{
    public static String readJSONFromResource(Context c, int r)
    {
        InputStream is;
        try
        {
            is = c.getResources().openRawResource(r);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1)
            {
                writer.write(buffer, 0, n);
            }
            is.close();
            return writer.toString();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
