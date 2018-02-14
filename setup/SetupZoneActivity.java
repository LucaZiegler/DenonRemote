package com.brin.denonremotefree.setup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.BrinObj.Zone;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.activation.BuyKeyActivity;
import com.brin.denonremotefree.widgets.BrinToolbar;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SetupZoneActivity extends BrinActivity
{
    private ArrayList<String> al1, al2, al3, al4, al5_0, al5_1, al6, alGet;
    private int CHILD_WAIT = 1, CHILD_KEY = 2, CHILD_NORM = 0;
    private Zone zone;

    @Bind(R.id.brinTabLayout) TabLayout tlZone;
    @Bind(R.id.brinToolbar) BrinToolbar tbMain;

    @Bind(R.id.tlSetupZone1) TabLayout tl1;
    @Bind(R.id.tlSetupZone2) TabLayout tl2;
    @Bind(R.id.tlSetupZone3) TabLayout tl3;
    @Bind(R.id.tlSetupZone6) TabLayout tl5;

    @Bind(R.id.etSetupZoneRename) EditText etZoneRen;
    @Bind(R.id.llSetupZone6) LinearLayout llView6;

    @Bind(R.id.sbSetupZoneBass) SeekBar sbBass;
    @Bind(R.id.sbSetupZoneTreble) SeekBar sbTreble;
    @Bind(R.id.sbSetupZonePwOnVol) SeekBar sbPwOnVol;
    @Bind(R.id.sbSetupZoneVolLimit) SeekBar sbVolLimit;

    @Bind(R.id.tvSetupZoneBass) TextView tvBass;
    @Bind(R.id.tvSetupZoneTreble) TextView tvTreble;
    @Bind(R.id.tvSetupZonePwOnVol) TextView tvPwOnVol;
    @Bind(R.id.tvSetupZoneVolLimit) TextView tvVolLimit;

    @Bind(R.id.btSetupZoneRenZone) Button btRen;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_zone);

        ButterKnife.bind(this);
        getPref();
        setToolbar(tbMain);
        setNavBack(true);
        setTitle(getString(R.string.setup_zone_title));

        for (int i = 2; i <= prefs.deviceZones(); i++)
        {
            tlZone.addTab(tlZone.newTab().setText(MessageFormat.format("{0} {1}", "zone", i)));
        }

        al1 = new ArrayList<>();
        al2 = new ArrayList<>();
        al3 = new ArrayList<>();
        al4 = new ArrayList<>();
        al5_0 = new ArrayList<>();
        al5_1 = new ArrayList<>();
        al6 = new ArrayList<>();
        alGet = new ArrayList<>();

        al1.addAll(Arrays.asList("#CSST", "#CSMONO"));
        al2.addAll(Arrays.asList("#HPFOFF", "#HPFON"));
        al3.add("#PSBAS ");
        al4.add("#PSTRE ");
        al5_0.addAll(Arrays.asList("SSVCT#SPON LAS", "SSVCT#SPON MUT", "SSVCT#SPON VAR"));
        al5_1.add("SSVCT#SPON ");
        //al6.addAll(Arrays.asList(MessageFormat.format("SSVCT{0}SMLV MUT",com),MessageFormat.format("SSVCT{0}SMLV 040",com),MessageFormat.format("SSVCT{0}SMLV 020",com)));
        al6.add("SSVCT#SLIM ");
        alGet.addAll(Arrays.asList("#CS?", "#HPF?", "#PSBAS ?", "#PSTRE ?", "SSVCT#S ?"));

        tl1.addTab(tl1.newTab().setText("stereo"));
        tl1.addTab(tl1.newTab().setText("mono"));

        tl2.addTab(tl2.newTab().setText("through"));
        tl2.addTab(tl2.newTab().setText("pcm"));

        tl3.addTab(tl3.newTab().setText(getString(R.string.off)));
        tl3.addTab(tl3.newTab().setText(getString(R.string.on)));

        tl5.addTab(tl5.newTab().setText("last"));
        tl5.addTab(tl5.newTab().setText("mute"));
        tl5.addTab(tl5.newTab().setText("custom"));

        etZoneRen.setHint(prefs.getDeviceZoneName(tlZone.getSelectedTabPosition() + 2));

        tl1.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                try
                {
                    sendDirectTelnetCom(al1.get(tl1.getSelectedTabPosition()).replaceAll("#", zone.getZoneCaller()));
                    hideKeyboard();
                }
                catch (Exception e)
                {
                    sendTrackMsg(e.getMessage());
                }
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

        tl3.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                try
                {
                    sendDirectTelnetCom(al2.get(tl3.getSelectedTabPosition()).replaceAll("#", zone.getZoneCaller()));
                    hideKeyboard();
                }
                catch (Exception e)
                {
                    sendTrackMsg(e.getMessage());
                }
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

        etZoneRen.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO)
                {
                    VolleyRenameZone(tlZone.getSelectedTabPosition() + 2, etZoneRen.getText().toString());
                    hideKeyboard();
                    handled = true;
                }
                return handled;
            }
        });

        tl5.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                try
                {
                    sendDirectTelnetCom(al5_0.get(tl5.getSelectedTabPosition()).replaceAll("#", zone.getZoneCaller()));
                    switch (tl5.getSelectedTabPosition())
                    {
                        case 2:
                            llView6.setVisibility(View.VISIBLE);
                            break;
                        default:
                            llView6.setVisibility(View.GONE);
                            break;
                    }
                    hideKeyboard();
                }
                catch (Exception e)
                {
                    sendTrackMsg(e.getMessage());
                }
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

        tlZone.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                try
                {
                    zone = new Zone(tlZone.getSelectedTabPosition() + 2, prefs.deviceZones());
                    etZoneRen.setHint(prefs.getDeviceZoneName(tlZone.getSelectedTabPosition() + 2));
                    etZoneRen.setText(null);
                    hideKeyboard();
                    getStatus();
                }
                catch (Exception e)
                {
                    sendTrackMsg(e.getMessage());
                }
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
                try
                {
                    if (fromUser)
                    {
                        Integer b = p - (seekBar.getMax() / 2);
                        Integer c = p + (50 - (seekBar.getMax() / 2));
                        tvBass.setText(MessageFormat.format("{0}", b));
                        sendDirectTelnetCom(MessageFormat.format("{0}{1}", al3.get(0), c).replaceAll("#", zone.getZoneCaller()));
                        hideKeyboard();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        sbTreble.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser)
            {
                try
                {
                    if (fromUser)
                    {
                        Integer b = p - (seekBar.getMax() / 2);
                        Integer c = p + (50 - (seekBar.getMax() / 2));
                        tvTreble.setText(MessageFormat.format("77{0}", b));
                        sendDirectTelnetCom(MessageFormat.format("{0}{1}", al4.get(0), c).replaceAll("#", zone.getZoneCaller()));
                        hideKeyboard();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        sbVolLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser)
            {
                try
                {
                    if (fromUser)
                    {
                        boolean enabled = p > 0;
                        String t;
                        String c;
                        if (enabled)
                        {
                            t = String.valueOf(p);
                            if (p < 10) c = MessageFormat.format("00{0}", p);
                            else if (p < 100) c = MessageFormat.format("0{0}", p);
                            else c = MessageFormat.format("{0}", p);
                        } else
                        {
                            t = getString(R.string.off);
                            c = "OFF";
                        }
                        tvVolLimit.setText(t);
                        sendDirectTelnetCom((al6.get(0) + c).replaceAll("#", zone.getZoneCaller()));
                        hideKeyboard();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        sbPwOnVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser)
            {
                try
                {
                    if (fromUser)
                    {
                        tvPwOnVol.setText(MessageFormat.format("{0}", p));

                        String t = String.valueOf(p);
                        String c;
                        if (p < 10) c = MessageFormat.format("0{0}", p);
                        else c = MessageFormat.format("{0}", p);
                        sendDirectTelnetCom((al5_1.get(0) + c).replaceAll("#", zone.getZoneCaller()));
                        hideKeyboard();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        etZoneRen.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                try
                {
                    if (!renRunning) btRen.setEnabled(s.length() > 0);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        zone = new Zone(tlZone.getSelectedTabPosition() + 2, prefs.deviceZones());
        etZoneRen.setHint(prefs.getDeviceZoneName(tlZone.getSelectedTabPosition() + 2));
        etZoneRen.setText(null);
        hideKeyboard();
    }

    @Override
    public void onBroadcastOpen(boolean isConn)
    {
        getStatus();
    }

    public void onRenZone(View v)
    {
        VolleyRenameZone(tlZone.getSelectedTabPosition() + 2, etZoneRen.getText().toString());
        hideKeyboard();
    }

    public void onPro(View v)
    {
        Intent i = new Intent(this, BuyKeyActivity.class);
        startActivity(i);
        finish();
    }

    private void hideKeyboard()
    {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etZoneRen.getWindowToken(), 0);
    }

    private boolean renRunning = false;

    private void VolleyRenameZone(int renameZone, String renameTo)
    {
        try
        {
            renRunning = true;
            btRen.setEnabled(false);
            String postZone;
            switch (renameZone)
            {
                case 1:
                    postZone = "MainZone";
                    break;
                default:
                    postZone = MessageFormat.format("Zone{0}", renameZone);
                    break;
            }
            renameTo = renameTo.replaceAll(" ", "+");
            final String postData = "textZoneRename" + postZone + "=" + renameTo + "&setZoneRename" + postZone + "=on";
            final String url = MessageFormat.format("http://{0}{1}", prefs.deviceHostname(), "/SETUP/GENERAL/ZONERENAME/s_general.asp");
            Log.d("SETUP.ZONE.POST.URL", url);
            Log.d("SETUP.ZONE.POST.DATA", postData);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String res)
                {
                    btRen.setEnabled(true);
                    renRunning = false;
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
                    btRen.setEnabled(true);
                    renRunning = false;
                }
            })
            {
                @Override
                public byte[] getBody() throws AuthFailureError
                {
                    try
                    {
                        return postData == null ? null : postData.getBytes("utf-8");
                    }
                    catch (UnsupportedEncodingException uee)
                    {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", postData, "utf-8");
                        return null;
                    }
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getStatus()
    {
        //pbPro.setVisibility(View.VISIBLE);
        for (int c = 0; c < alGet.size(); c++)
        {
            sendTelnetCom(alGet.get(c).replaceAll("#", zone.getZoneCaller()));
        }
    }

    @Override
    public void onTelnetResult(String l)
    {
        String cont;
        cont = al1.get(0).replaceAll("#", zone.getZoneCaller());

        if (l.startsWith(cont))
        {
            tl1.getTabAt(0).select();
            return;
        }

        cont = al1.get(1).replaceAll("#", zone.getZoneCaller());
        if (l.startsWith(cont))
        {
            tl1.getTabAt(1).select();
            return;
        }

        cont = al2.get(0).replaceAll("#", zone.getZoneCaller());
        if (l.startsWith(cont))
        {
            tl3.getTabAt(0).select();
            return;
        }

        cont = al2.get(1).replaceAll("#", zone.getZoneCaller());
        if (l.startsWith(cont))
        {
            tl3.getTabAt(1).select();
            return;
        }


        cont = al3.get(0).replaceAll("#", zone.getZoneCaller());
        if (l.startsWith(cont))

        {
            fetchLvl(l.replaceFirst(cont, ""), 1);
            return;
        }

        cont = al4.get(0).replaceAll("#", zone.getZoneCaller());

        if (l.startsWith(cont))

        {
            fetchLvl(l.replaceFirst(cont, ""), 2);
            return;
        }

        cont = al5_1.get(0).replaceAll("#", zone.getZoneCaller());

        if (l.startsWith(cont))
        {
            fetchPwOnLvl(l.replaceFirst(cont, ""));
            return;
        }

        cont = al5_0.get(0).replaceAll("#", zone.getZoneCaller());

        if (l.startsWith(cont))

        {
            tl5.getTabAt(0).select();
            llView6.setVisibility(View.GONE);
            return;
        }

        cont = al5_0.get(1).replaceAll("#", zone.getZoneCaller());
        if (l.startsWith(cont))

        {
            tl5.getTabAt(1).select();
            llView6.setVisibility(View.GONE);
            return;
        }

        cont = al5_0.get(2).replaceAll("#", zone.getZoneCaller());
        if (l.startsWith(cont))

        {
            tl5.getTabAt(2).select();
            llView6.setVisibility(View.VISIBLE);
            return;
        }

        cont = al6.get(0).replaceAll("#", zone.getZoneCaller());
        if (l.startsWith(cont))
        {
            fetchVolLim(l.replaceFirst(cont, ""));
            return;
        }

        if (l.startsWith("R"))
        {
            Toast.makeText(getApplicationContext(), getString(R.string.succesfully_set).toUpperCase(), Toast.LENGTH_LONG).show();
        }
    }

    private void fetchPwOnLvl(String l)
    {
        try
        {
            if (l.contains("LAS") || l.contains("MUT"))
            {
                llView6.setVisibility(View.GONE);
            } else
            {
                l = l.replaceAll("\\D+", "");
                Integer lvl = Integer.valueOf(l);
                if (!sbPwOnVol.isPressed()) sbPwOnVol.setProgress(lvl);
                tvPwOnVol.setText(MessageFormat.format("{0}", lvl));
                tl5.getTabAt(2).select();
                llView6.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void fetchVolLim(String l)
    {
        try
        {
            if (l.contains("OFF"))
            {
                if (!sbPwOnVol.isPressed()) sbPwOnVol.setProgress(0);
                tvPwOnVol.setText(getString(R.string.off));
            } else
            {
                l = l.replaceAll("\\D+", "");
                Integer lvl = Integer.valueOf(l);
                if (!sbVolLimit.isPressed()) sbVolLimit.setProgress(lvl);
                if (lvl > 0) tvVolLimit.setText(MessageFormat.format("{0}", lvl));
                else tvVolLimit.setText(getString(R.string.off));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void fetchLvl(String l, int i)
    {
        try
        {
            TextView tv = null;
            SeekBar sb = null;
            switch (i)
            {
                case 1:
                    tv = tvBass;
                    sb = sbBass;
                    break;
                case 2:
                    tv = tvTreble;
                    sb = sbTreble;
                    break;
                case 3:
                    tv = tvPwOnVol;
                    sb = sbPwOnVol;
                    break;
                case 4:
                    tv = tvVolLimit;
                    sb = sbVolLimit;
                    break;
            }
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
            if (!sb.isPressed()) sb.setProgress(lvl);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
