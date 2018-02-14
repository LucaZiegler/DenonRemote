package com.brin.denonremotefree;

import android.os.Handler;

import java.util.ArrayList;

/**
 * Created by Luca on 02.09.2016.
 */

public class testClass
{
    private ArrayList<String> alQueue = new ArrayList<>();
    private boolean started = false;
    private Handler handler = new Handler();

    public testClass()
    {

    }

    public void onStop()
    {
        stop();
    }

    public void addCommand(String com)
    {
        alQueue.add(com);
        if (!started)
            start();
    }

    private void exeCommand(String com)
    {

    }

    private Runnable runnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (alQueue.size() > 0)
            {
                exeCommand(alQueue.get(0));
                alQueue.remove(0);
            } else
            {
                stop();
            }
            if (started)
            {
                restart();
            }
        }
    };

    public void stop()
    {
        started = false;
        handler.removeCallbacks(runnable);
    }

    public void start()
    {
        started = true;
        handler.post(runnable);
    }

    public void restart()
    {
        started = true;
        handler.postDelayed(runnable, 2000);
    }
}
