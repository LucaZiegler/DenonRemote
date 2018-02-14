package com.brin.denonremotefree.extras;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.activation.BuyKeyActivity;
import com.brin.denonremotefree.db.coms;
import com.brin.denonremotefree.views.BrinViewFlipper;
import com.brin.denonremotefree.widgets.BrinToolbar;

import butterknife.Bind;
import butterknife.ButterKnife;


public class LevelActivity extends BrinActivity
{
    private static final String TAG = "LEVEL.ACT";
    private String[] cms;

    @Bind(R.id.brinTabLayout) TabLayout tlAb;
    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinViewFlipper) BrinViewFlipper vfMain;

    @Bind(R.id.sbLevelBass) SeekBar sbBass;
    @Bind(R.id.sbLevelTreble) SeekBar sbTreble;
    @Bind(R.id.sbLevelDialog) SeekBar sbDialog;
    @Bind(R.id.sbLevelBassSync) SeekBar sbBassSync;
    @Bind(R.id.sbLevelLFE) SeekBar sbLFE;
    @Bind(R.id.sbLevelDimension) SeekBar sbDimension;

    @Bind(R.id.tvLevelBass) TextView tvBass;
    @Bind(R.id.tvLevelTreble) TextView tvTreble;
    @Bind(R.id.tvLevelDialog) TextView tvDialog;
    @Bind(R.id.tvLevelBassSync) TextView tvBassSync;
    @Bind(R.id.tvLevelLFE) TextView tvLFE;
    @Bind(R.id.tvLevelDimension) TextView tvDimension;

    private Integer i = 1;

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setCrashReporter();
        setContentView(R.layout.activity_level);
        ButterKnife.bind(this);

        tlAb.addTab(tlAb.newTab().setText("sound"));
        tlAb.addTab(tlAb.newTab().setText("front"));
        tlAb.addTab(tlAb.newTab().setText("5.X"));
        tlAb.addTab(tlAb.newTab().setText("7.X"));
        getPref();
        setToolbar(tbMain);
        setTitle(getString(R.string.levels));
        setNavBack(true);

        if (lic())
        {
            cms = new String[]{
                    "CVFL%20", "CVFR%20", "CVC%20", "CVSW%20", "CVSL%20", "CVSR%20", "CVSBL%20", "CVSBR%20", "CVSB%20", "CVFHL%20", "CVFHR%20", "CVFWL%20", "CVFWR%20", "CVSW2%20"
            };

            tlAb.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
            {
                @Override
                public void onTabSelected(TabLayout.Tab tab)
                {
                    setDisChild(tlAb.getSelectedTabPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab)
                {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab)
                {
                }
            });

            sbBass.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser)
                {
                    if (fromUser)
                    {
                        Integer b = p - (seekBar.getMax() / 2);
                        Integer c = p + (50 - (seekBar.getMax() / 2));
                        tvBass.setText(String.valueOf(b));
                        sendTelnetCom(coms.bass + String.valueOf(c));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {

                }

            });

            sbTreble.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser)
                {
                    if (fromUser)
                    {
                        Integer b = p - (seekBar.getMax() / 2);
                        Integer c = p + (50 - (seekBar.getMax() / 2));
                        tvTreble.setText(String.valueOf(b));
                        sendTelnetCom(coms.treble + String.valueOf(c));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {
                }

            });
            sbDialog.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser)
                {
                    if (fromUser)
                    {
                        Integer b = p - (seekBar.getMax() / 2);
                        Integer c = p + (50 - (seekBar.getMax() / 2));
                        tvDialog.setText(String.valueOf(b));
                        sendTelnetCom(coms.dialog + c.toString());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {
                }
            });
            sbBassSync.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser)
                {
                    if (fromUser)
                    {
                        tvBassSync.setText(String.valueOf(p));
                        sendTelnetCom(coms.basssync + String.valueOf(p));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {
                }
            });
            sbDimension.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser)
                {
                    if (fromUser)
                    {
                        tvDimension.setText(String.valueOf(p));
                        sendTelnetCom(coms.dimension + String.valueOf(p));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {
                }
            });
            sbLFE.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser)
                {
                    if (fromUser)
                    {
                        tvLFE.setText(String.valueOf(p));
                        sendTelnetCom(coms.lfe + String.valueOf(p));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {
                }
            });


            i = 1;
            while (i <= 14)
            {
                final SeekBar sb = getSeekbar(i);
                sb.setTag(i.toString());
                //sb.setProgress(sp.getInt("level" + String.valueOf(i), 12));
                getTextView(i).setText(String.valueOf(sb.getProgress() - 12));
                sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser)
                    {
                        int s = getInt(seekBar.getTag().toString());
                        if (fromUser)
                        {
                            Integer b = p - (seekBar.getMax() / 2);
                            Integer c = p + (50 - (seekBar.getMax() / 2));
                            getTextView(s).setText(String.valueOf(b));
                            sendTelnetCom(cms[s - 1] + String.valueOf(c));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar)
                    {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar)
                    {

                    }
                });
                i++;
            }
        } else
        {
            // not licensed
        }
    }

    private void setDisChild(int position)
    {
        vfMain.setChild(position);
    }

    public int getInt(String st)
    {
        return Integer.valueOf(st);
    }

    private SeekBar getSeekbar(Integer id)
    {
        int sbId = getResources().getIdentifier("sbLevel" + id.toString(), "id", getPackageName());
        return ((SeekBar) findViewById(sbId));
    }

    private TextView getTextView(Integer id)
    {
        int tvId = getResources().getIdentifier("tvLevel" + id.toString(), "id", getPackageName());
        return ((TextView) findViewById(tvId));
    }

    private boolean lic()
    {
        //TODO return prefs.isAppActivated();
        return true;
    }

    public void onPro(View v)
    {
        Intent i = new Intent(this, BuyKeyActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBroadcastOpen(boolean isConn)
    {
        Log.d(TAG + "BROAD", "onBroadcastOpen");
        sendTelnetCom("PSBAS ?");
        sendTelnetCom("PSTRE ?");
        sendTelnetCom("PSBSC ?");
        sendTelnetCom("PSDIL ?");
        sendTelnetCom("PSDIM ?");
        sendTelnetCom("PSLFE ?");
        sendTelnetCom("CV?");
    }

    @Override
    public void onTelnetResult(String l)
    {
        if (l.startsWith("CV"))
        {
            fetchLevelResult(l, returnSB(l));
        }
        if (l.startsWith("PSBAS"))
        {
            fetchOtherResult(l, sbBass, tvBass);
        }
        if (l.startsWith("PSTRE"))
        {
            fetchOtherResult(l, sbTreble, tvTreble);
        }

        if (l.startsWith("PSBSC"))
        {
            fetchOtherResult(l, sbBassSync, tvBassSync);
        }
        if (l.startsWith("PSDIL"))
        {
            fetchOtherResult(l, sbDialog, tvDialog);
        }
        if (l.startsWith("PSDIM"))
        {
            fetchOtherResult(l, sbDimension, tvDimension);
        }
        if (l.startsWith("PSLFE"))
        {
            fetchOtherResult(l, sbLFE, tvLFE);
        }
    }

    private void fetchLevelResult(String l, int sb)
    {
        try
        {
            l = l.substring(l.indexOf(" "));
            l = l.replaceAll("\\D+", "");
            fetchLevel(sb, Integer.valueOf(l));
        } catch (Exception e)
        {

        }
    }

    private void fetchOtherResult(String l, SeekBar sb, TextView tv)
    {
        try
        {
            l = l.substring(l.indexOf(" "));
            l = l.replaceAll("\\D+", "");
            Integer lvl = Integer.valueOf(l);
            if (lvl > 100)
            {
                lvl = lvl / 10;
            }
            if (lvl < 30)
            {
                tv.setText(String.valueOf(lvl));
            } else
            {
                lvl = lvl - (50 - (sb.getMax() / 2));
                tv.setText(String.valueOf(lvl - (sb.getMax() / 2)));
            }
            sb.setProgress(lvl);
        } catch (Exception e)
        {
            sb.setEnabled(false);
        }
    }


    private int returnSB(String l)
    {
        i = 1;
        if (l.contains("CVFL"))
            return 1;
        if (l.contains("CVFR"))
            return 2;
        if (l.contains("CVC"))
            return 3;
        if (l.contains("CVSW"))
            return 4;
        if (l.contains("CVSL"))
            return 5;
        if (l.contains("CVSR"))
            return 6;
        if (l.contains("CVSBL"))
            return 7;
        if (l.contains("CVSBR"))
            return 8;
        if (l.contains("CVSB"))
            return 9;
        if (l.contains("CVFHL"))
            return 10;
        if (l.contains("CVFHR"))
            return 11;
        if (l.contains("CVFWL"))
            return 12;
        if (l.contains("CVFWR"))
            return 13;
        if (l.contains("CVSW2"))
            return 14;
        return 0;
    }

    private void fetchLevel(int i, int l)
    {
        if (i > 0 && i < 19)
        {
            if (l > 100)
            {
                l = l / 10;
            }
            l = l - 38;
            Log.d("fetchLevels/", String.valueOf(i) + "/" + String.valueOf(l));
            getSeekbar(i).setProgress(l);
            getTextView(i).setText(String.valueOf(l - 12));
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        super.closeBroadcast();
        isActivityVisible = false;
        isActivityActive = true;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        isActivityVisible = true;
        isActivityActive = true;
        openBroadcast();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        isActivityVisible = false;
        isActivityActive = false;
        closeBroadcast();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        isActivityVisible = true;
        isActivityActive = true;
        openBroadcast();
    }

    @Override
    public void onDestroy()
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
