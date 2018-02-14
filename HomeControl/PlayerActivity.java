package com.brin.denonremotefree.HomeControl;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.brin.denonremotefree.DashboardActivity;
import com.brin.denonremotefree.Helper.PlayerHelper;
import com.brin.denonremotefree.Helper.SetView;
import com.brin.denonremotefree.Helper.StopApp;
import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.BrinObj.PlayerItem;
import com.brin.denonremotefree.BrinObj.Zone;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.db.coms;
import com.brin.denonremotefree.db.url;
import com.brin.denonremotefree.widgets.BrinToolbar;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

import static com.brin.denonremotefree.Helper.PlayerHelper.UpnpPost;

public class PlayerActivity extends BrinActivity implements Zone.ZoneInter
{
    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinToolbarSec) BrinToolbar tbSec;
    @Bind(R.id.brinRecycler) SuperRecyclerView rvMain;
    @Bind(R.id.brinProgressBar) ProgressBar pbMain;
    @Bind(R.id.brinFrameLayout1) FrameLayout flListBlock;
    @Bind(R.id.brinFrameLayout2) FrameLayout flBlockKey;
    @Bind(R.id.brinViewFlipper) ViewFlipper vfMain;
    @Bind(R.id.tvPlayView1) TextView tvPlayer1;
    @Bind(R.id.tvPlayView2) TextView tvPlayer2;
    @Bind(R.id.tvPlayView3) TextView tvPlayer3;
    @Bind(R.id.tvPlayViewVol) TextView tvVol;
    @Bind(R.id.tvPlayViewTime1) TextView tvPlayerTime1;
    @Bind(R.id.tvPlayViewTime2) TextView tvPlayerTime2;
    @Bind(R.id.ibPlayViewMedia1) ImageButton ibPlayerMed1;
    @Bind(R.id.ibPlayViewMedia2) ImageButton ibPlayerMed2;
    @Bind(R.id.ibPlayViewMedia3) ImageButton ibPlayerMed3;
    @Bind(R.id.ibPlayViewMedia4) ImageButton ibPlayerMed4;
    @Bind(R.id.brinVolMute) ImageButton ibPlayerMute;
    @Bind(R.id.sbPlayViewTime) SeekBar sbPlayerTime;
    @Bind(R.id.sbPlayViewVol) SeekBar sbVol;
    @Bind(R.id.ivPlayViewCover) ImageView ivPlayerCover;

    private static final int STAT_BROAD_OPEN_CON = 0, STAT_BROAD_CLOSED = 1, STAT_BROAD_OPEN_DIS = 2, STAT_DIS = 3, STAT_PLAYER = 4, STAT_LIST = 5, STAT_LIST_RESET = 6, STAT_LIST_DOWNLOAD = 7, STAT_LIST_DONE = 8, STAT_PLAYER_DONE = 9, STAT_LIST_SELECTED = 10, STAT_SEARCHING = 11, STAT_SPOTIFY_LOGIN_NEED = 12;
    private static int CUR_STAT = -1;
    private ArrayList<PlayerItem> alList;
    private RecyclerView.Adapter rvAdapter;
    private String TAG = "BRIN.PLAYER";
    //private ArrayList<String> alNSE = new ArrayList<>();
    private boolean stopScroll = false;

    private Pattern pat1 = Pattern.compile("[0-9][0-9][0-9]:[0-9][0-9]");
    private Pattern pat2 = Pattern.compile("[0-9][0-9]:[0-9][0-9]:[0-9][0-9]");
    private Pattern pat3 = Pattern.compile("[0-9][0-9]:[0-9][0-9]");
    private boolean isPlayerHome = false;
    private Zone zone;
    private CountDownTimer ctCheck = new CountDownTimer(3000, 2900)
    {
        @Override
        public void onTick(long l)
        {

        }

        @Override
        public void onFinish()
        {
            Toast.makeText(getApplicationContext(), "Please wait a moment...", Toast.LENGTH_LONG).show();
            Log.e(TAG, "onFinish: NO NSE ANSWER");
            sendTelnetCom("NSE");
        }
    };

    @Override
    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        isActivityVisible = true;
        isActivityActive = true;

        setToolbar(tbMain);
        setSecToolbar(tbSec);
        setTitle(getString(R.string.drawer_item_player));
        setSecTitle(getString(R.string.please_wait___));
        setNavBack(true);
        enableSecTitleMarquee(true);
        enableCustomProgress(pbMain);

        prefs = DashboardActivity.getPrefs();
        alList = new ArrayList<>();
        for (int i = 0; i < prefs.deviceZones(); i++)
            alZonePower.add(0);

        rvMain.setAnimation(AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_top));
        RecyclerView.LayoutManager rvManager = new GridLayoutManager(this, 1);
        rvMain.setLayoutManager(rvManager);
        rvAdapter = new PlayerAdapter();
        rvMain.setAdapter(rvAdapter);
        zone = new Zone(1, prefs.deviceZones());

        sbVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser)
            {
                if (fromUser)
                {
                    Float volFloat = (float) p;
                    if (zone.isMainZone())
                    {
                        volFloat /= 2;
                    }
                    String volCom = String.valueOf(volFloat);
                    SetView.setText(tvVol, volCom, null);
                    volCom = volCom.replaceAll("\\D+", "");
                    if (volFloat < 10) volCom = "0" + volCom;
                    //if (volFloat < 100) volCom = "0" + volCom;
                    if (volCom.endsWith("0")) volCom = volCom.substring(0, volCom.length() - 1);
                    sendDirectTelnetCom(zone.setVolume() + volCom);
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

        try
        {
            switch (getIntent().getIntExtra("notMed", 0))
            {
                case 1:
                    sendTelnetCom(coms.mediaSkipBack);
                    break;
                case 2:
                    sendTelnetCom(coms.mediaStop);
                    break;
                case 3:
                    sendTelnetCom(coms.mediaPause);
                    break;
                case 4:
                    sendTelnetCom(coms.mediaSkipFor);
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (prefs.isAppActivated())
        {
            ((RelativeLayout) flBlockKey.getParent()).removeView(flBlockKey);
        }
    }

    public void onKey(View v)
    {
        openKeyActivity();
    }

    @Override
    public void onTelnetResult(String l)
    {
        Log.d(TAG, "TELNET.RESULT: " + l);
        switch (zone.getZoneId())
        {
            case 1:
                if (l.matches("MV\\d+$"))
                {
                    fetchVol(true, l);
                    return;
                }
                if (l.matches("MVMAX \\d+$"))
                {
                    fetchMaxVol(l);
                    return;
                }
                if (l.matches("MU[A-Z][A-Z]+"))
                {
                    fetchMute(true, l);
                }
                break;
            default:
                if (l.matches("Z" + zone.getZoneId() + "\\d+$"))
                {
                    fetchVol(false, l);
                    return;
                }
                if (l.matches("Z" + zone.getZoneId() + "MU[A-Z][A-Z]+"))
                {
                    fetchMute(false, l);
                }
                break;
        }

        if (l.equals("PWSTANDBY") || l.equals("PWOFF"))
        {
            for (int i = 1; i <= alZonePower.size(); i++)
            {
                setZonePowerInfo(i, false);
            }
            return;
        }
        if (l.equals("ZMOFF"))
        {
            setZonePowerInfo(1, false);
            return;
        }
        if (l.equals("ZMON"))
        {
            setZonePowerInfo(1, true);
            return;
        }
        if (l.equals("PWON"))
        {
            if (prefs.deviceZones() == 1) setZonePowerInfo(1, true);
            return;
        }
        if (prefs.deviceZones() > 1)
        {
            if (l.equals("Z2ON"))
            {
                setZonePowerInfo(2, true);
                return;
            }
            if (l.equals("Z2OFF"))
            {
                setZonePowerInfo(2, false);
                return;
            }
        }
        if (prefs.deviceZones() > 2)
        {
            if (l.equals("Z3ON"))
            {
                setZonePowerInfo(3, true);
                return;
            }
            if (l.equals("Z3OFF"))
            {
                setZonePowerInfo(3, false);
                return;
            }
        }
        if (prefs.deviceZones() > 3)
        {
            if (l.equals("Z4ON"))
            {
                setZonePowerInfo(4, true);
                return;
            }
            if (l.equals("Z4OFF"))
            {
                setZonePowerInfo(4, false);
                return;
            }
        }
        if (l.startsWith("NSSPOSTASCH "))
        {
            setSpotifySearchActive(l.contains("ON"));
        }
        if (l.startsWith("NSSPO "))
        {
            if (l.contains(" NG"))
            {
                setSpotifyLogInActive(true);
                setSecNavBack(true);
                clearList("W", false);
                return;
            } else if (l.contains(" OK"))
            {
                setSpotifyLogInActive(false);
            }
        }

        if (l.startsWith("NSE"))
        {
            ctCheck.cancel();
        }
/**
 if (nseUpdate)
 {
 if (PlayerHelper.check(l) && !lastLine.equals(l))
 {
 Log.d(TAG, "EWKJHBGEW: " + l);
 int nseThis = -8;
 boolean nseThisNo = false;
 boolean nseThisShort = false;
 boolean fetchDone = false;
 try
 {
 if (l.startsWith("NSE0"))
 {
 Log.d(TAG, "onTelnetResult: 0");
 nseLOG = "";
 nseActive = true;
 nseLastShort = false;
 nseLast = -1;
 nseLastNo = false;
 nseThis = 0;
 alNSE.clear();
 } else
 {
 Log.d(TAG, "onTelnetResult: 1");
 if (!nseActive)
 {
 Log.d(TAG, "onTelnetResult: 2");
 //throw new NotActiveException("Error 1");
 }
 }

 Pattern r = Pattern.compile("NSE[0-8]");
 Matcher m = r.matcher(l);

 nseThisNo = !m.find();
 if (!nseThisNo)
 {
 Log.d(TAG, "onTelnetResult: 3");

 nseThisShort = l.length() <= 4;
 if (nseThisShort) l += "";
 if (nseThis != 0) nseThis = PlayerHelper.getNSEFromLine(l, true);
 if ((nseThis - 1) == nseLast)
 {
 Log.d(TAG, "onTelnetResult: 4");
 if (nseLOG.length() == 0) nseLOG = l;
 else nseLOG += "§#" + l;
 if (l.contains("NSE8"))
 {
 fetchDone = true;
 }
 } else
 {
 Log.d(TAG, "onTelnetResult: 5");
 throw new IndexOutOfBoundsException(MessageFormat.format("ERROR NOT EQUAL: nseThis/{0} nseLast/{1}", (nseThis - 1), nseLast));
 }
 } else
 {
 nseThisShort = false;
 nseThis = nseLast;
 if (true)
 {
 Log.d(TAG, "onTelnetResult: 6");
 nseLOG += l;
 if (l.contains("NSE8"))
 {
 fetchDone = true;
 }
 } else
 {
 Log.d(TAG, "onTelnetResult: 7");
 throw new InterruptedException("Error 2");
 }
 }
 if (fetchDone)
 {
 Log.d(TAG, "onTelnetResult: 8");
 nseLOG = nseLOG.replaceAll("NSE[0-8]", "");
 String[] extrct = nseLOG.split("§#");

 alNSE = new ArrayList<>();
 alNSE.addAll(Arrays.asList(extrct));
 onTelnetNseDone(alNSE);
 alNSE.clear();
 nseActive = false;
 }
 }
 catch (Exception e)
 {
 //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
 e.printStackTrace();
 Log.e(TAG, "onTelnetResult: ", e);
 clearList("T", true);
 }
 Log.d(TAG, "onTelnetResult: NSELOG: " + nseLOG);
 // AT THE END
 nseLastNo = nseThisNo;
 nseLastShort = nseThisShort;
 nseLast = nseThis;
 }
 lastLine = l;
 }
 */
    }


    private boolean spotifyLogInActive = false;

    private void setSpotifyLogInActive(boolean on)
    {
        spotifyLogInActive = on;
        if (on)
        {
            setSecTitle(getString(R.string.login_tv));
            if (vfMain.getChildCount() == 2)
            {
                LinearLayout llRoot = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_spotify_login, vfMain, false);
                vfMain.addView(llRoot);
                setVfChild(2);
            }
        } else
        {
            if (vfMain.getChildCount() == 3) vfMain.removeViewAt(2);
            setVfChild(0);
        }
    }

    private void setSpotifySearchActive(boolean on)
    {
        spotifySearchActive = on;
        if (on)
        {
            setSecTitle(getString(R.string.spotify_search));
            if (vfMain.getChildCount() == 2)
            {
                LinearLayout llRoot = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_player_search, vfMain, false);
                final EditText editText = (EditText) llRoot.findViewById(R.id.brinEditText);
                Button button = (Button) llRoot.findViewById(R.id.brinButton);
                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        SetView.hideKeyboard(PlayerActivity.this);
                        sendSpotifySearch(editText.getText().toString());
                        view.setEnabled(false);
                    }
                });
                vfMain.addView(llRoot);
                setVfChild(2);
            }
        } else
        {
            if (vfMain.getChildCount() == 3) vfMain.removeViewAt(2);
            setVfChild(0);
        }
    }

    private void sendSpotifySearch(String s)
    {
        try
        {
            updateStatus(STAT_SEARCHING);
            String URL = "http://" + prefs.deviceHostname() + url.spotifySearchUrl;

            final String mRequestBody = "cmd0=PutNetFuncSearchSpotify%2F" + s.replaceAll(" ", "+");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    clearList("SEARCH", true);
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Toast.makeText(getApplicationContext(), "Search error (" + error.getMessage() + ")", Toast.LENGTH_LONG).show();
                    sendTelnetCom(zone.setInput(coms.inputNet));
                }
            })
            {
                @Override
                public String getBodyContentType()
                {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError
                {
                    try
                    {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    }
                    catch (UnsupportedEncodingException uee)
                    {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }
            };
            DashboardActivity.rqMain.add(stringRequest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Search error (" + e.getMessage() + ")", Toast.LENGTH_LONG).show();
            sendTelnetCom(zone.setInput(coms.inputNet));
        }
    }

    private String lastTitle = "";

    private void onTelnetNseDone(ArrayList<String> alNSE)
    {
        if (alNSE.size() == 9)
        {
            Log.d(TAG, "onTelnetNseDone: " + alNSE);
            //updateStatus(ST);
            final String title = alNSE.get(0).replaceAll("  ", " ");
            if (!title.equals(lastTitle))
            {
                if (!title.toLowerCase().startsWith("now playing") && title.toLowerCase().contains("spotify"))
                {
                    sendTelnetCom("NSSPO ?");
                }
                setSecTitle(title);

                if (alList.size() > 0)
                {
                    /*
                    int alSize = alList.size();
                    alList.clear();
                    rvAdapter.notifyItemRangeRemoved(0, alSize);
                    updateStatus(STAT_LIST_RESET);
                    */
                    clearList("Y", false);
                    //return;
                }
            }
            if (title.equals("Spotify Log In"))
            {
                clearList("LOG IN", false);
                setSpotifyLogInActive(true);
                setSecNavBack(true);
                return;
            }
            if (title.toLowerCase().startsWith("now playing"))
            {
                Log.d(TAG, "onTelnetNseDone: KEJGHEIUWQG");
                updateStatus(STAT_PLAYER);
                renderPlayer(alNSE);
                setSecSubTitle(null);
            } else
            {
                if (timerActive) timerStop();
                updateStatus(STAT_LIST);
                renderList(alNSE);
            }
        } else
        {
            Log.e(TAG, "INVALID LIST SIZE: " + alNSE.size());
        }
    }


    @Override
    public boolean setSecTitle(String title)
    {
        lastTitle = title;
        return super.setSecTitle(title);
    }

    @Override
    public void onSecBackPressed()
    {
        if (spotifyLogInActive)
        {
            sendTelnetCom(zone.setInput(coms.inputIradio));
        } else goBack();
        setSpotifyLogInActive(false);
    }

    private void goBack()
    {
        /**showProgress(true);*/
        updateStatus(STAT_LIST_RESET);
        ctCheck.start();
        sendTelnetCom(coms.curLeft);
        //w.loadUrl(deviceIp + url.netStatus1 + "?CurLeft");
        if (vfMain.getDisplayedChild() == 0)
        {
            new CountDownTimer(200, 100)
            {
                public void onTick(long millisUntilFinished)
                {
                }

                public void onFinish()
                {
                    clearList("P", false);
                }
            }.start();
        }
    }

    private boolean fetchUpdate = true;

    private void clearList(String fromMethod, final boolean withGET)
    {
        Log.d(TAG + "CLEAR.LIST", fromMethod);
        setSecSubTitle(null);
        stopScroll = false;
        /*+
        nseUpdate = true;
        nseActive = false;
        nseLast = -2;
        nseLastShort = false;
        nseLastNo = false;
        nseLOG = "";
        //alNSE.clear();
        */
        int alSize = alList.size();
        alList.clear();
        rvAdapter.notifyItemRangeRemoved(0, alSize);
        fetchUpdate = true;
        if (withGET) getPlayerStatus();
    }

    private void getPlayerCover()
    {
        try
        {
            ImageRequest ir = new ImageRequest("http://" + prefs.deviceHostname() + url.coverUrl, new Response.Listener<Bitmap>()
            {
                @Override
                public void onResponse(Bitmap result)
                {
                    Log.d(TAG, "onResponse: DOWNLOAD COMPLETE");
                    try
                    {
                        if (result != null)
                        {
                            ivPlayerCover.setImageBitmap(result);
                        } else
                        {
                            ivPlayerCover.setImageResource(R.drawable.cover_null);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        ivPlayerCover.setImageResource(R.drawable.cover_null);
                    }
                }

            }, 0, 0, null, null);

            DashboardActivity.rqMain.add(ir);
            Log.d(TAG, "getPlayerCover: DOWNLOAD INITIALIZED");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ivPlayerCover.setImageResource(R.drawable.cover_null);
        }
    }

    private String lastTime = "";
    private boolean lastPause = false;

    private void renderPlayer(ArrayList<String> alNSE)
    {
        Log.d(TAG, "renderPlayer: ");
        boolean newer = false;
        if (SetView.setText(tvPlayer1, alNSE.get(1), null)) newer = true;
        if (SetView.setText(tvPlayer2, alNSE.get(2), null)) newer = true;
        if (SetView.setText(tvPlayer3, alNSE.get(3), null)) newer = true;
        if (newer)
        {
            getPlayerCover();
        }
        final String line5 = alNSE.get(5);
        try
        {
            if (line5.contains("%"))
            {
                final int percentIndex = line5.indexOf("%");
                int pro = Integer.valueOf((line5.substring(percentIndex - 3, percentIndex)).trim());
                Log.d(TAG, "renderPlayer: SEC.PROGRESS" + pro);
                sbPlayerTime.setSecondaryProgress(pro);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            Matcher mat1 = pat1.matcher(line5);
            Matcher mat2;
            Matcher mat3;

            int tHours = 0;
            int tMins = 0;
            int tSecs = 0;

            if (mat1.find())
            {
                Log.d(TAG, "renderPlayer: FALL 1");
                String[] time = mat1.group(0).split(":");
                int mins = Integer.valueOf(time[0]);
                tMins = mins % 60;
                tHours = mins / 60;
                tSecs = Integer.valueOf(time[1]);
            } else if ((mat2 = pat2.matcher(line5)).find())
            {
                Log.d(TAG, "renderPlayer: FALL 2");
                String[] time = mat2.group(0).split(":");
                tHours = Integer.valueOf(time[0]);
                tMins = Integer.valueOf(time[1]);
                tSecs = Integer.valueOf(time[2]);
            } else if ((mat3 = pat3.matcher(line5)).find())
            {
                Log.d(TAG, "renderPlayer: FALL 3");
                String[] time = mat3.group(0).split(":");
                tMins = Integer.valueOf(time[0]);
                tSecs = Integer.valueOf(time[1]);
            } else Log.d(TAG, "renderPlayer: FALL 4");

            final String finalTime = MessageFormat.format("{0}:{1}:{2}", timeToText(tHours), timeToText(tMins), timeToText(tSecs));
            Log.d(TAG, "renderPlayer: " + finalTime);
            SetView.setText(tvPlayerTime1, finalTime, null);
            playerPause = finalTime.equals(lastTime);
            if (lastPause != playerPause)
            {
                if (playerPause) ibPlayerMed3.setImageResource(R.drawable.play_100_white);
                else ibPlayerMed3.setImageResource(R.drawable.pause_100_white);
            }
            if (playerNonEnd) tvPlayerTime2.setText(finalTime);

            lastTime = finalTime;
            lastPause = playerPause;
            timerStart();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        updateStatus(STAT_PLAYER_DONE);
    }

    /**
     * REGION TIMER
     */
    private static boolean timerActive = false;
    private static Handler timerHandler = new Handler();
    private static final int timerIntervall = 1000;
    private boolean playerPause = false;
    private boolean playerNonEnd = true;
    private int timerCount = 0;

    private Runnable timerRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (!playerPause)
            {
                String time = tvPlayerTime1.getText().toString();
                SimpleDateFormat dateFormat;
                SimpleDateFormat sdf;
                if (time.matches(".*:.*:.*"))
                {
                    dateFormat = new SimpleDateFormat("HH:mm:ss");
                    sdf = new SimpleDateFormat("HH:mm:ss");
                    try
                    {
                        Date date = dateFormat.parse(time);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.SECOND, 1);
                        String test = sdf.format(calendar.getTime());
                        SetView.setText(tvPlayerTime1, test, null);
                        if (!playerNonEnd)
                        {
                            if (sbPlayerTime.getProgress() != sbPlayerTime.getMax())
                            {
                                sbPlayerTime.setProgress(sbPlayerTime.getProgress() + 1);
                            }
                            sbPlayerTime.setEnabled(true);
                        } else
                        {
                            sbPlayerTime.setEnabled(false);
                            SetView.setText(tvPlayerTime2, test, null);
                        }

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            if (timerActive && isActivityVisible())
            {
                timerRestart();
            } else
            {
                Log.d(TAG, "timerStop because: " + timerActive + "/" + isActivityVisible());
                timerStop();
            }
        }
    };

    private void timerStop()
    {
        int line = new Exception().getStackTrace()[1].getLineNumber();
        Log.d(TAG, "timerStop: " + line);
        if (timerActive)
        {
            timerHandler.removeCallbacks(timerRunnable);
        }
        timerActive = false;
    }

    private void timerStart()
    {
        Log.d(TAG, "timerStart: ");
        if (!timerActive)
        {
            timerHandler.postDelayed(timerRunnable, timerIntervall);
        }
        timerActive = true;
    }

    private void timerRestart()
    {
        Log.d(TAG, "timerRestart: ");
        timerCount++;
        if (timerCount >= 7)
        {
            timerCount = 0;
            getPlayerStatus();
            new UpnpGetDuration().execute();
        }
        timerHandler.postDelayed(timerRunnable, timerIntervall);
        timerActive = true;
    }

    /**
     * ENDREGION TIMER
     */

    private String timeToText(Integer t)
    {
        return t < 10 ? "0" + t : t.toString();
    }

    private void renderList(ArrayList<String> alNSE)
    {
        try
        {
            //alList.clear();
            cursors = getCursorsFromLine(alNSE.get(8));
            Log.d(TAG, "renderList: cursors:" + Arrays.toString(cursors));
            if (cursors == null) throw new NullPointerException("Cursor is null");

            if (cursors[0] == 0)
            {
                cursors[0] = 1;
                if (alNSE.get(0).toLowerCase().equals("network")) setSecNavBack(false);
            } else
            {
                setSecNavBack(true);
            }

            checkCursorAll(cursors[1]);

            if (!isSelectActive())
            {
                Integer dbSize = alList.size();
                Log.d("ENLKJO.DBSIZE/", dbSize.toString());
                if (dbSize != 0)
                {
                    dbSize++;
                    Log.d("ENLKJO", "A");
                }
                if ((dbSize < cursors[1] && cursors[0] != -1 && cursors[1] != -1) || cursors[0] == dbSize)
                {
                    setProgressMax(cursors[1]);
                    if (dbSize == 0)
                    {
                        /**dowloadListActive = true;
                         //showProgress(true);*/
                        updateStatus(STAT_LIST_DOWNLOAD);
                        if (cursors[0] == 1)
                        {
                            // fetch
                            fetchItem(cursors, alNSE);
                            Log.d("ENLKJO", "B");
                        } else
                        {
                            // invalid cursor
                            //if (cursors[1] < 8 && cursors[1] > 0) fetchItem(cursors);
                            scrollToTop(cursors);
                            Log.d("ENLKJO", "C");
                        }
                    } else
                    {
                        if (dbSize > cursors[0])
                        {
                            if (!stopScroll)
                            {
                                if (acceptCursor(cursors) || !checkSpace(cursors))
                                    loadMore(cursors);
                                //else
                                //scrollToTop(a,b);
                            }
                            Log.d("ENLKJO", "D");
                        } else
                        {
                            if (acceptCursor(cursors) || !checkSpace(cursors))
                            {
                                // fetch
                                fetchItem(cursors, alNSE);
                                Log.d("ENLKJO", "E");
                            } else
                            {
                                // invalid cursor
                                scrollToTop(cursors);
                                Log.d("ENLKJO", "F");
                            }
                        }
                    }
                } else
                {
                    if (cursors[0] != 1)
                    {
                        //loadCom("NSE?");
                        //scrollToTop(a, b);
                    }
                    updateStatus(STAT_LIST_DONE);
                    /**showProgress(false);
                     dowloadListActive = false;*/
                    Log.d("ENLKJO", "G");
                }
            } else
            {
                runSelection(cursors[0], getSelectDestination());
            }
            alNSE.clear();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        lastCursors = cursors;
    }

    private void checkCursorAll(int cursorAll)
    {
        if (cursorAll != lastCursors[1] || isSecSubTitleVisible())
        {
            setSecSubTitle(MessageFormat.format("{0} {1}", cursorAll, cursorAll > 1 ? getString(R.string.entries) : getString(R.string.entry)));
            //clearList("W",false);
        }
    }

    @Override
    public void setSecSubTitle(String sub)
    {
        super.setSecSubTitle(sub);
    }

    @Override
    public void setSecNavBack(boolean enable)
    {
        Log.d(TAG, "setSecNavBack: " + enable);
        isPlayerHome = !enable;
        super.setSecNavBack(enable);
    }

    private boolean atBottom()
    {
        return true;
    }


    private void scrollToTop(int[] cursors)
    {
        //if (Arrays.equals(cursors, lastCursors)) Toast.makeText(getApplicationContext(), "Can not scroll! Is the right input selected?", Toast.LENGTH_LONG).show();
        Log.d(TAG, "XIK scrollToTop: ");
        clearList("Z", false);
        final int topDif = cursors[0] - 1;
        final int botDif = cursors[1] - cursors[0];
        if (topDif <= botDif)
        {
            //setProgressMax(topDif);
            if (cursors[0] > 7)
            {
                sendTelnetCom(coms.comPageUp);
                ctCheck.start();
            } else
            {
                sendTelnetCom(coms.curUp);
                ctCheck.start();
            }
        } else
        {
            //setProgressMax(botDif);
            if (botDif > 7)
            {
                sendTelnetCom(coms.comPageDn);
            } else
            {
                sendTelnetCom(coms.curDown);
            }
        }
    }

    @Override
    public void sendTelnetCom(String c)
    {
        Log.d(TAG, "sendTelnetCom: " + c);
        super.sendTelnetCom(c);
    }

    private String htmlToString(String inp)
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        {
            return Html.fromHtml(inp, Html.FROM_HTML_MODE_LEGACY).toString();
        } else
        {
            return Html.fromHtml(inp).toString();
        }
    }

    /*
    private void fetchItem(int[] cursors) {
        final int space = cursors[1] - cursors[0];
        int index = 1;
        int line = (space >= 7 ? 0 : space);
        int alPosi = (cursors[0] + index) - 2;
        Log.d(TAG, "fetchItem: "+space+"/"+line+"/"+alPosi+"/"+alList.size());
        while (alList.size() == alPosi && cursors[1] > alPosi && index < 8) {
            Log.d("ADD.ITEM." + alPosi, index + "|" + alNSE.get(line));
            try {
                PlayerItem playerItem = new PlayerItem(alPosi, isPlayerHome ? PlayerHelper.getIcon(alNSE.get(line + index)) : -1, htmlToString(alNSE.get(line + index).trim()), false);
                alList.add(alPosi, playerItem);
                rvAdapter.notifyItemInserted(alPosi);
            }
            catch (Exception e) {
                Log.e("ERROR.DB/", Arrays.toString(e.getStackTrace()));
                break;
            }
            index++;
            alPosi = (cursors[0] + index) - 2;
        }

        if (alList.size() == cursors[1]) {
            updateStatus(STAT_LIST_DONE);
        } else {
            if (index < 8 && cursors[1] > 6) {
                Log.e("ERROR", "While schleife wurde zu früh geschlossen!");
                //scrollToTop(a,b);
            } else {
                loadMore(cursors);
                //rvMain.showMoreProgress();
            }
        }
    }
        int nseLine = (space < 7 && cursors[1] > 7 ? space + 1 : 1);
        int alPosi;
        for (int i = 0; nseLine < 8 && (alPosi = c[0] + i) - 1 == alList.size(); nseLine += ++i) {
            try {
                PlayerItem playerItem = new PlayerItem(alPosi, isPlayerHome ? PlayerHelper.getIcon(alNSE.get(alPosi)) : -1, htmlToString(alNSE.get(alPosi).trim()), false);
                alList.add(alPosi, playerItem);
                rvAdapter.notifyItemInserted(alPosi);
            }
            catch (Exception e) {
                Log.e("ERROR.DB/", Arrays.toString(e.getStackTrace()));
                break;
            }
        }
    */

    private void fetchItem(int[] c, ArrayList<String> alNSE)
    {
        int space = 7 - (c[1] - c[0]) - 1;
        if (!(space < 7 && c[0] > 7))
        {
            space = 0;
        }

        int alPosi;
        int alSize = alList.size();
        int indNse = 1;
        for (int indLoop = 1; indLoop < 8 && (alPosi = c[0] + (indNse) - 2) == alSize && alPosi < c[1]; indLoop++)
        {
            if (indLoop > space)
            {
                try
                {
                    setProgress(false, alPosi + 1);
                    final PlayerItem playerItem = new PlayerItem(alPosi, isPlayerHome ? PlayerHelper.getIcon(alNSE.get(indLoop)) : -1, htmlToString(alNSE.get(indLoop).trim()), false);
                    alList.add(alPosi, playerItem);
                    alSize++;
                    rvAdapter.notifyItemInserted(alPosi);
                }
                catch (Exception e)
                {
                    Log.e("ERROR.DB/", Arrays.toString(e.getStackTrace()));
                    break;
                }
                indNse++;
            }
        }

        if (alSize == cursors[1])
        {
            updateStatus(STAT_LIST_DONE);
        } else if (alSize < cursors[0])
        {
            Log.e(TAG, "fetchItem: ERROR");
        } else if (alSize < cursors[1])
        {
            loadMore(cursors);
        } else
        {
            clearList("R", false);
        }
    }

    private boolean checkSpace(int[] cursors)
    {
        return cursors[1] - cursors[0] > 6;
    }

    private void loadMore(int[] cursors)
    {
        Log.d(TAG, "XIK loadMore: ");

        if (acceptCursor(cursors) && checkSpace(cursors))
        {
            sendTelnetCom(coms.comPageDn);
            return;
        }
        if (!checkSpace(cursors))
        {
            Log.e(TAG, "loadMore: NOT ENOUGH SPACE");
            sendTelnetCom(coms.curDown);
        }
    }

    private boolean acceptCursor(int[] cursors)
    {
        Boolean accept;
        switch (cursors[0])
        {
            case 0:
                accept = true;
                break;
            case 1:
                accept = true;
                break;
            default:
                int scrollSpace = cursors[1] - cursors[0];
                if (scrollSpace < 7)
                {
                    accept = true;
                } else
                {
                    int c = cursors[0] - 1;
                    accept = ((c % 7) == 0);
                }
                break;
        }

        Log.d("ACCEPT.(" + cursors[0] + ")", accept.toString());
        return accept;
    }

    private int[] getCursorsFromLine(String s)
    {
        try
        {
            s = s.replaceAll(" ", "");
            s = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
            String[] cs = s.split("/");
            int[] ics = new int[2];
            for (int c = 0; c < cs.length; c++)
            {
                ics[c] = Integer.valueOf(cs[c]);
            }
            return ics;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onConnectionError(int err, String ip)
    {
        updateStatus(STAT_DIS);
    }

    @Override
    public void onBroadcastOpen(boolean isConn)
    {
        if (isConn)
        {
            updateStatus(STAT_BROAD_OPEN_CON);
        } else
        {
            updateStatus(STAT_BROAD_OPEN_DIS);
        }
    }

    @Override
    public void onNseResult(ArrayList<String> al)
    {
        Log.d(TAG, "onNseResult: ");
        onTelnetNseDone(al);
    }

    @Override
    public void onBackPressed(boolean hardware)
    {
        if (hardware && prefs.getGlobBool("switch_preference_1_1", false))
        {
            goBack();
        } else
        {
            super.onBackPressed(hardware);
        }
    }

    private void updateStatus(int status)
    {
        if (status != CUR_STAT)
        {
            int name = new Exception().getStackTrace()[1].getLineNumber();
            Log.d(TAG, "updateStatus: " + status + "/" + name);

            switch (status)
            {
                case STAT_BROAD_OPEN_CON:
                    getPlayerStatus();
                    //showListBlock(true);
                    if (selectWithEnter) setSelectActive(false, -1, false);
                    break;
                case STAT_BROAD_OPEN_DIS:
                    setProgress(false, 0);
                    showListBlock(false);
                    setSelectActive(false, -1, false);
                    break;
                case STAT_BROAD_CLOSED:
                    setProgress(false, 0);
                    showListBlock(false);
                    setSelectActive(false, -1, false);
                    break;
                case STAT_DIS:
                    setProgress(false, 0);
                    showListBlock(false);
                    closeApp(true);
                    setSelectActive(false, -1, false);
                    break;
                case STAT_LIST:
                    //showListBlock(true);
                    setVfChild(0);
                    break;
                case STAT_PLAYER:
                    setSecNavBack(true);
                    setVfChild(1);
                    setSelectActive(false, -1, false);
                    break;
                case STAT_LIST_DOWNLOAD:
                    showListBlock(false);
                    setProgress(false, 0);
                    //rvMain.showMoreProgress();
                    break;
                case STAT_LIST_DONE:
                    showListBlock(false);
                    setProgress(false, 0);
                    rvMain.hideMoreProgress();
                    break;
                case STAT_LIST_RESET:
                    showListBlock(false);
                    setProgress(true, 1);
                    setSecTitle(getString(R.string.please_wait___));
                    if (selectWithEnter) setSelectActive(false, -1, false);
                    break;
                case STAT_PLAYER_DONE:
                    setProgress(false, 0);
                    break;
                case STAT_LIST_SELECTED:
                    clearList("U", false);
                    showListBlock(true);
                    setSelectActive(false, -1, false);
                    break;
                case STAT_SEARCHING:
                    setProgress(true, 1);
                    break;
                case STAT_SPOTIFY_LOGIN_NEED:
                    setProgress(false, 0);
                    showListBlock(false);
                    break;
            }
        }
        CUR_STAT = status;
    }

    private void nowPlayingIsVisible(boolean b)
    {
        try
        {
            Log.d(TAG, "nowPlayingIsVisible: " + b);
            if (b)
            {
                tbSec.getMenu().clear();
                tbSec.inflateMenu(R.menu.menu_playerview);

                tbSec.getMenu().getItem(0).setChecked(zone.getRandomState());

                if (zone.getRepeatState() >= 0)
                    tbSec.getMenu().getItem(zone.getRepeatState() + 1).setChecked(true);

                tbSec.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            case R.id.brinMenuItem1:
                                sendTelnetCom(coms.netAddFav);
                                break;

                            case R.id.brinMenuItem2:
                                sendTelnetCom(zone.getRandomState() ? coms.mediaRandomOff : coms.mediaRandomOn);
                                zone.setRandomState(!zone.getRandomState());
                                break;

                            case R.id.brinMenuItem3:
                                sendTelnetCom(coms.mediaRepeatOff);
                                zone.setRepeatState(REP_OFF);
                                break;
                            case R.id.brinMenuItem4:
                                sendTelnetCom(coms.mediaRepeatAll);
                                zone.setRepeatState(REP_ALL);
                                break;
                            case R.id.brinMenuItem5:
                                sendTelnetCom(coms.mediaRepeatOne);
                                zone.setRepeatState(REP_ONE);
                                break;
                        }
                        return false;
                    }
                });
            } else
            {
                tbSec.getMenu().clear();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setVfChild(int i)
    {
        if (vfMain.getDisplayedChild() != i)
        {
            nowPlayingIsVisible(i == 1);
            vfMain.setDisplayedChild(i);
        }
    }

    private void closeApp(boolean openList)
    {
        stopTelnetService();
        if (openList)
            CustomActivityOnCrash.restartApplicationWithIntent(this, new Intent(this, StopApp.class).putExtra("DISCONNECT", true));
        finish();
    }

    private void showListBlock(boolean s)
    {

        if (s)
        {
            flListBlock.setVisibility(View.VISIBLE);
        } else
        {
            flListBlock.setVisibility(View.GONE);
        }

    }

    private void getPlayerStatus()
    {
        ctCheck.start();
        setProgress(true, 1);
        sendTelnetCom("NSE");
        sendTelnetCom("PW?");
        sendTelnetCom("ZM?");
        sendTelnetCom(zone.getVolume());
        sendTelnetCom(zone.getMute());
        //sendTelnetCom("NSLASSTA ?");
    }

    private boolean isSelectActive()
    {
        return selectActive;
    }

    private int getSelectDestination()
    {
        return selectDest;
    }

    private int selectDest = -1;
    private boolean selectWithEnter = false;

    private void setSelectActive(boolean selectActive, int dest, boolean withEnter)
    {
        Log.d(TAG, "setSelectActive: " + selectActive + "/" + dest);
        this.selectActive = selectActive;
        selectWithEnter = withEnter;
        if (!selectActive) selectDest = -1;
        if (dest != -2)
        {
            selectDest = dest;
        }
    }

    class PlayerAdapter extends RecyclerView.Adapter<PlayerActivity.PlayerAdapter.ViewHolderClass>
    {
        class ViewHolderClass extends RecyclerView.ViewHolder
        {
            private TextView tv1;
            private ImageView ivIcon;
            private FrameLayout flRoot;

            ViewHolderClass(View v)
            {
                super(v);
                tv1 = (TextView) v.findViewById(android.R.id.text1);
                flRoot = (FrameLayout) v.findViewById(android.R.id.content);
                ivIcon = (ImageView) v.findViewById(android.R.id.icon);
            }
        }

        @Override
        public PlayerActivity.PlayerAdapter.ViewHolderClass onCreateViewHolder(ViewGroup vg, int i)
        {
            View v = LayoutInflater.from(vg.getContext()).inflate(R.layout.item_player, vg, false);
            return new PlayerActivity.PlayerAdapter.ViewHolderClass(v);
        }

        @Override
        public void onBindViewHolder(final PlayerActivity.PlayerAdapter.ViewHolderClass vhc, final int i)
        {
            try
            {
                PlayerItem pItem = alList.get(i);
                Log.d(TAG, "onBindViewHolder: " + pItem.getTitle());
                vhc.tv1.setText(pItem.getTitle());
                if (pItem.getIcon() != -1)
                {
                    vhc.ivIcon.setImageResource(pItem.getIcon());
                } else
                {
                    try
                    {
                        ((LinearLayout) vhc.ivIcon.getParent()).removeView(vhc.ivIcon);
                    }
                    catch (Exception e)
                    {

                    }
                }
                vhc.flRoot.setTag(i);
                vhc.flRoot.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        try
                        {
                            onRvItemSelected(alList.get(Integer.valueOf(v.getTag().toString())));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount()
        {
            return alList.size();
        }
    }

    private boolean selectActive = false;

    private int[] cursors;
    private int[] lastCursors = new int[]{0, 0};

    private int[] getLastCursors()
    {
        return cursors;
    }

    private void onRvItemSelected(final PlayerItem playerItem)
    {
        updateStatus(STAT_LIST_SELECTED);
        final String title = playerItem.getTitle();
        if (isPlayerHome)
        {
            if (title.startsWith("Favorites")) sendTelnetCom(zone.setInput(coms.inputFav));
            else if (title.startsWith("Internet Radio"))
                sendTelnetCom(zone.setInput(coms.inputIradio));
            else if (title.startsWith("Last.Fm")) sendTelnetCom(zone.setInput(coms.inputLastfm));
            else if (title.startsWith("Spotify")) sendTelnetCom(zone.setInput(coms.inputSpotify));
            else if (title.startsWith("Media Server"))
                sendTelnetCom(zone.setInput(coms.inputServer));
            else if (title.startsWith("Pandora")) sendTelnetCom(zone.setInput(coms.inputPandora));
            else if (title.startsWith("SiriusXM")) sendTelnetCom(zone.setInput(coms.inputSirius));
            else if (title.startsWith("Rhapsody")) sendTelnetCom(zone.setInput(coms.inputRhapsody));
            else if (title.startsWith("Flickr")) sendTelnetCom(zone.setInput(coms.inputFlickr));
        } else
        {
            if (tbSec.getTitle().toString().toLowerCase().equals("spotify") && title.toLowerCase().equals("playlists"))
            {
                Toast.makeText(this, R.string.spotify_playlist_download, Toast.LENGTH_LONG).show();
            }
            setSelectActive(true, playerItem.getPosition() + 1, true);
            runSelection(getLastCursors()[0], playerItem.getPosition() + 1);
        }
    }

    private boolean spotifySearchActive = false;
    private int runSelectionLast = -1;

    private void runSelection(int cur, int to)
    {
        Log.d(TAG, "runSelection: " + cur + "/" + to);
        int itemPage = getCursorPage(to);
        int curPage = getCursorPage(cur);
        if (itemPage == curPage)
        {
            if (cur == to)
            {
                onSelectionDone(selectWithEnter);
            } else if (cur > to)
            {
                sendTelnetCom(coms.curUp);
            } else
            {
                sendTelnetCom(coms.curDown);
            }
        } else if (itemPage > curPage)
        {
            sendTelnetCom(coms.comPageDn);
        } else
        {
            sendTelnetCom(coms.comPageUp);
        }
        runSelectionLast = cur;
    }

    private void onSelectionDone(final boolean e)
    {
        setSelectActive(false, -1, false);
        if (e)
        {
            sendTelnetCom(coms.curEnter);
            ctCheck.start();
        }
    }

    public void onMedia(View v)
    {
        switch (v.getTag().toString())
        {
            case "1":
                sendTelnetCom(coms.mediaSkipBack);
                break;

            case "2":
                sendTelnetCom(coms.mediaStop);
                break;

            case "3":
                sendTelnetCom(coms.mediaPause);
                break;

            case "4":
                sendTelnetCom(coms.mediaSkipFor);
                break;
        }
    }

    private int getCursorPage(int cursor)
    {
        try
        {
            int page;
            if (cursor < 8) page = 1;
            else page = cursor / 7;
            return page;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (prefs.deviceZones() > 1) getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuItem1:
                showZonesDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setZonePowerInfo(int zone, boolean isOn)
    {
        try
        {
            alZonePower.set(zone - 1, isOn ? 1 : 0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private int getZonePowerInfo(int zone)
    {
        try
        {
            return alZonePower.get(zone - 1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    private ArrayList<Integer> alZonePower = new ArrayList<>();

    private void showZonesDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_dialog_zones);

        LinearLayout llList = (LinearLayout) dialog.findViewById(R.id.brinLinearLayout1);
        TextView tvTitle = (TextView) llList.getChildAt(0);

        tvTitle.setText(getString(R.string.select_zone));

        for (int z = 1; z <= prefs.deviceZones(); z++)
        {
            FrameLayout flItem = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.item_dialog_singlechoice_zone, llList, false);
            CheckedTextView ctv = (CheckedTextView) flItem.getChildAt(1);
            if (zone.getZoneId() == z) ctv.setCheckMarkDrawable(R.drawable.check_100_white);
            ctv.setText(prefs.getDeviceZoneName(z));
            View vIndi = flItem.getChildAt(0);
            switch (getZonePowerInfo(z))
            {
                case -1:
                    vIndi.setBackgroundColor(Color.GRAY);
                    break;
                case 0:
                    vIndi.setBackgroundColor(Color.RED);
                    break;
                case 1:
                    vIndi.setBackgroundColor(Color.GREEN);
                    break;
            }
            llList.addView(flItem);
            flItem.setTag(z);
            flItem.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    int z = Integer.valueOf(view.getTag().toString());
                    zone = new Zone(z, prefs.deviceZones());
                    sendTelnetCom(zone.getVolume());
                    dialog.dismiss();
                }
            });
        }

        dialog.setCancelable(true);
        dialog.show();
    }


    private boolean muteActive = false;

    public void onVolMute(View v)
    {
        sendDirectTelnetCom(zone.setMute(muteActive = !muteActive));
    }

    public void onVolDn(View v)
    {
        sendDirectTelnetCom(zone.setVolumeDn());
    }

    public void onVolUp(View v)
    {
        sendDirectTelnetCom(zone.setVolumeUp());
    }

    private void fetchMute(boolean mainZone, String l)
    {
        if (l.contains("MUOFF"))
        {
            muteActive = false;
            ibPlayerMute.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.accent_color));
        } else if (l.contains("MUON"))
        {
            muteActive = true;
            ibPlayerMute.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.mute_color));
        }
    }

    private void fetchVol(boolean mainZone, String l)
    {
        try
        {
            if (mainZone)
            {
                String volMax = l.replaceAll("\\D+", "");
                String volText;
                Float volFloat = Float.valueOf(volMax);
                Integer volInt;
                if (volMax.matches("[0-9][0-9]5"))
                {
                    // FLOAT VALUE
                    volFloat /= 10;
                    volText = volFloat.toString();
                } else
                {
                    volText = volFloat.toString()/* + ".0"*/;
                }
                volFloat *= 2;
                volInt = Math.round(volFloat);
                SetView.setText(tvVol, volText, null);
                SetView.setProgress(sbVol, volInt);
            } else
            {
                String volZone = l.substring(2).replaceAll("\\D+", "");
                Integer volInt = Integer.valueOf(volZone);
                SetView.setText(tvVol, volZone, null);
                SetView.setProgress(sbVol, volInt);
            }
            sbVol.setEnabled(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String upnpGetDurationLastResponse = "";

    private class UpnpGetDuration extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            try
            {
                return UpnpPost("http://" + prefs.deviceHostname() + ":8080/AVTransport/ctrl", "\"urn:schemas-upnp-org:service:AVTransport:1#GetPositionInfo\"", "<s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        " <s:Body>\n" +
                        "  <u:GetPositionInfo xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\">\n" +
                        "   <InstanceID>0</InstanceID>\n" +
                        "  </u:GetPositionInfo>\n" +
                        " </s:Body>\n" +
                        "</s:Envelope>");
            }
            catch (Exception e)
            {
                return "ERROR";
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            Log.d("UPNP.RES", result);
            if (!result.equals(upnpGetDurationLastResponse))
            {
                try
                {
                    String cont1 = "<TrackDuration>";
                    String cont2 = "</TrackDuration>";
                    if (result.contains(cont1) && result.contains(cont2))
                    {
                        String TrackDuration = result;
                        TrackDuration = TrackDuration.substring(TrackDuration.indexOf(cont1) + cont1.length());
                        TrackDuration = TrackDuration.substring(0, TrackDuration.indexOf(cont2));

                        SimpleDateFormat dateFormat = new SimpleDateFormat("H:mm:ss");
                        Date date = dateFormat.parse(TrackDuration);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        Integer proHour = calendar.get(Calendar.HOUR);
                        Integer proMinutes = calendar.get(Calendar.MINUTE);
                        Integer proSecond = calendar.get(Calendar.SECOND);
                        Integer proSecondes = ((proHour * 60 + proMinutes) * 60) + proSecond;
                        sbPlayerTime.setMax(proSecondes);

                        playerNonEnd = sbPlayerTime.getProgress() == sbPlayerTime.getMax();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            upnpGetDurationLastResponse = result;
        }
    }

    public void fetchMaxVol(String l)
    {
        Log.d(TAG, "fetchMaxVol: " + l);
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
