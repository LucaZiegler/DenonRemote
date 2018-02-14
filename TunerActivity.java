package com.brin.denonremotefree;

import android.animation.AnimatorInflater;
import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.BrinObj.Zone;
import com.brin.denonremotefree.Helper.SetView;
import com.brin.denonremotefree.activation.BuyKeyActivity;
import com.brin.denonremotefree.db.coms;
import com.brin.denonremotefree.widgets.BrinToolbar;

import java.text.DecimalFormat;
import java.text.MessageFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TunerActivity extends BrinActivity
{

    private static final String TAG = "TUNER";

    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinText1) TextView tvFreq;
    @Bind(R.id.brinText2) TextView tvPres;
    @Bind(R.id.brinProgressBar) ProgressBar pbFreq;
    @Bind(R.id.brinTabLayout) TabLayout tlMode;
    @Bind(R.id.brinTabLayout2) TabLayout tlBand;
    private String[] gets = new String[]{coms.tunerFreqStatus, coms.tunerModeStatus};
    private Zone zone;

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.activity_tuner);

        ButterKnife.bind(this);
        setToolbar(tbMain);
        setNavBack(true);
        setTitle(getString(R.string.title_tuner));

        SetView.addTabLayoutItem(tlMode, getString(R.string.manual));
        SetView.addTabLayoutItem(tlMode, getString(R.string.automatic));

        SetView.addTabLayoutItem(tlBand, getString(R.string.am));
        SetView.addTabLayoutItem(tlBand, getString(R.string.fm));

        setTabLayoutListener1(tlMode);
        setTabLayoutListener2(tlBand);

        prefs = DashboardActivity.getPrefs();
        if (prefs == null)
        {
            finish();
            return;
        }
        zone = new Zone(1, prefs.deviceZones());
    }

    private void setTabLayoutListener1(TabLayout tl)
    {
        try
        {
            LinearLayout tabStrip = ((LinearLayout) tl.getChildAt(0));
            tabStrip.setLayoutTransition(new LayoutTransition());
            for (Integer i = 0; i < tabStrip.getChildCount(); i++)
            {
                LinearLayout tab = (LinearLayout) tabStrip.getChildAt(i);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    tab.setStateListAnimator(AnimatorInflater.loadStateListAnimator(this, R.animator.state_list_animator));
                }
                tab.setTag(i);
                tab.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent)
                    {
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                        sendTelnetCom(Integer.valueOf(view.getTag().toString()) == 0 ? coms.tunerModeManual : coms.tunerModeAuto);
                        return false;
                    }
                });
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setTabLayoutListener2(TabLayout tl)
    {
        try
        {
            LinearLayout tabStrip = ((LinearLayout) tl.getChildAt(0));
            tabStrip.setLayoutTransition(new LayoutTransition());
            for (Integer i = 0; i < tabStrip.getChildCount(); i++)
            {
                LinearLayout tab = (LinearLayout) tabStrip.getChildAt(i);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    tab.setStateListAnimator(AnimatorInflater.loadStateListAnimator(this, R.animator.state_list_animator));
                }
                tab.setTag(i);
                tab.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent)
                    {
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                        sendTelnetCom(Integer.valueOf(view.getTag().toString()) == 0 ? coms.tunerBand1 : coms.tunerBand2);
                        return false;
                    }
                });
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBroadcastOpen(boolean isConn)
    {
        if (isConn)
        {
            for (String c : gets)
            {
                sendTelnetCom(c);
            }
        }
    }

    @Override
    public void onConnectionError(int err, String ip)
    {
        super.onConnectionError(err, ip);
        finish();
    }

    public void onFreqPlus(View v)
    {
        if (lic())
        sendTelnetCom(coms.tunerFreqUp);
    }

    public void onFreqMinus(View v)
    {
        if (lic())
        sendTelnetCom(coms.tunerFreqDn);
    }

    public void onPresUp(View v)
    {
        if (lic())
        sendTelnetCom(coms.tunerPresetUp);
    }

    public void onPresDown(View v)
    {
        if (lic())
        sendTelnetCom(coms.tunerPresetDn);
    }

    public void onVolUp(View v)
    {
        sendTelnetCom(zone.setVolumeUp());
    }

    public void onVolDn(View v)
    {
        sendTelnetCom(zone.setVolumeUp());
    }

    private boolean lic()
    {
        if (!prefs.isAppActivated())
        {
            Toast.makeText(getApplicationContext(), getString(R.string.pro_required), Toast.LENGTH_SHORT).show();
        }
        return prefs.isAppActivated();
    }

    @Override
    public void onTelnetResult(String l)
    {
        if (l.startsWith("TMAN"))
        {
            if (l.contains("AUTO"))
            {
                SetView.selectTabLayoutItem(tlMode, 1);
                return;
            }
            if (l.contains("MANUAL"))
            {
                SetView.selectTabLayoutItem(tlMode, 0);
                return;
            }

            if (l.contains("FM"))
            {
                SetView.selectTabLayoutItem(tlBand, 1);
                return;
            }
            if (l.contains("AM"))
            {
                SetView.selectTabLayoutItem(tlBand, 0);
                return;
            }
        }

        if (l.startsWith("TPAN"))
        {
            if (l.equals("TPANOFF"))
            {
                tvPres.setVisibility(View.GONE);
                return;
            }
            if (l.matches("TPAN[0-9][0-9]"))
            {
                fetchPreset(l);
                return;
            }
        }

        if (l.matches("TFAN[0-9]*"))
        {
            fetchFreq(l);
        }
    }

    private void fetchPreset(String l)
    {
        try
        {
            l = l.replaceAll("\\D+", "");
            if (l.length() == 0)
                return;
            int pres = Integer.valueOf(l);
            tvPres.setText(MessageFormat.format("{0}: {1}", "preset",pres));
            pbFreq.setVisibility(View.GONE);
            tvPres.setVisibility(View.VISIBLE);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private DecimalFormat dfFreq = new DecimalFormat("#.00");
    private void fetchFreq(String l)
    {
        try
        {
            l = l.replaceAll("\\D+", "");
            if (l.length() == 0)
                return;
            Double freq = Double.valueOf(l);
            freq = freq / 100;

            setFreq(freq);

            if (pbFreq.getVisibility() == View.VISIBLE)
            {
                pbFreq.setVisibility(View.GONE);
            }
            tvFreq.setVisibility(View.VISIBLE);
        } catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private double lastFreq = 0;
    private void setFreq(final double f)
    {
        final Handler h = new Handler();
        new Thread(new Runnable()
        {
            public void run()
            {
                if (lastFreq > 0)
                {
                    for (double fTemp = lastFreq += 0.01; fTemp < f; fTemp += 0.01)
                    {
                        if (!isActivityVisible)
                            break;
                        Log.d(TAG, "run: "+fTemp);
                        final double finalFTemp = fTemp;
                        h.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                tvFreq.setText(MessageFormat.format("{0} {1}", dfFreq.format(finalFTemp), finalFTemp >= 500 ? "KHZ" : "MHZ"));
                            }
                        });
                        try
                        {
                            Object obj = new Object();
                            synchronized (obj)
                            {
                                obj.wait(50);
                            }
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                h.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tvFreq.setText(MessageFormat.format("{0} {1}", dfFreq.format(f), f >= 500 ? "KHZ" : "MHZ"));
                    }
                });
                lastFreq = f;
            }
        }).start();
    }

    public void onPro()
    {
        Intent i = new Intent(this, BuyKeyActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_tuner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuKey:
                onPro();
                break;
            case R.id.menuSave:
                Toast.makeText(getApplicationContext(),getString(R.string.coming_soon),Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        super.closeBroadcast();
        isActivityVisible = false;
        isActivityActive = true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        isActivityVisible = true;
        isActivityActive = true;
        openBroadcast();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        isActivityVisible = false;
        isActivityActive = false;
        closeBroadcast();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        isActivityVisible = true;
        isActivityActive = true;
        openBroadcast();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        isActivityVisible = false;
        isActivityActive = false;
        closeBroadcast();
    }

    public static boolean isActivityVisible = false;
    public static boolean isActivityActive = false;

    public static boolean isActivityVisible()
    {
        return isActivityVisible;
    }

    public static boolean isActivityActive()
    {
        return isActivityActive;
    }
}