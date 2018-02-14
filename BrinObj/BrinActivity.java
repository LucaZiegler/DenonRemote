package com.brin.denonremotefree.BrinObj;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.brin.denonremotefree.AssistantSettingsActivity;
import com.brin.denonremotefree.BuildConfig;
import com.brin.denonremotefree.DashboardActivity;
import com.brin.denonremotefree.Helper.Prefs;
import com.brin.denonremotefree.Interface.ReceiverConnectionInterface;
import com.brin.denonremotefree.PreferencesActivity;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.ReturnClass;
import com.brin.denonremotefree.activation.BuyKeyActivity;
import com.brin.denonremotefree.binding.ReceiverListActivity;
import com.brin.denonremotefree.db.coms;
import com.brin.denonremotefree.db.url;
import com.brin.denonremotefree.service.ReceiverConnectionService;
import com.brin.denonremotefree.solve.CrashReporterActivity;
import com.brin.denonremotefree.widgets.BrinToolbar;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import butterknife.ButterKnife;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

/**
 * Created by Luca on 31.07.2016.
 */
public class BrinActivity extends AppCompatActivity implements ReceiverConnectionInterface
{
    private static final String TAG = "BRIN.ACT";
    private boolean serviceStartedByThis = false;
    private BroadcastReceiver bcReceiver;
    private boolean broadCastActive = false;
    private BrinToolbar tbMain = null;
    private BrinToolbar tbSec = null;
    private ProgressBar pbMain = null;
    private boolean toolbarProgressbarEnabled = false;
    private GoogleAnalytics analytics;
    private Tracker tracker;
    private String userId;
    public Prefs prefs;
    private final int reqVoice = 99;
    private RequestQueue volleyQueue = null;
    public static final String INTENT_RCR_OBJ = "R";
    public static boolean prefVolButtons = true;

    private void initializeVolleyQueue(Context c)
    {
        if (volleyQueue == null)
        {
            volleyQueue = Volley.newRequestQueue(c);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        try
        {
            if (prefVolButtons)
            {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                        sendDirectTelnetCom("MVDOWN");
                        return true;
                    case KeyEvent.KEYCODE_VOLUME_UP:
                        sendDirectTelnetCom("MVUP");
                        return true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void openSettings()
    {
        startActivity(new Intent(this, PreferencesActivity.class));
    }

    public void openApp(long tag)
    {
        Log.d(TAG, "openApp: " + tag);
        Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
        i.putExtra(INTENT_RCR_OBJ, tag);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    public void openStoreKey()
    {
        final String appPackageName = "com.brin.denonremote"; // getPackageName() from Context or Activity object
        try
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        }
        catch (android.content.ActivityNotFoundException anfe)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void restartApp()
    {
        CustomActivityOnCrash.restartApplicationWithIntent(this, new Intent(this, ReceiverListActivity.class));
    }

    public void openKeyActivity()
    {
        startActivity(new Intent(this, BuyKeyActivity.class));
        finish();
    }

    public void showFeedbackDialog()
    {
        try
        {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_feedback);

            final BrinToolbar tbDia = (BrinToolbar) dialog.findViewById(R.id.brinToolbar);
            final ImageButton ibNeg = (ImageButton) dialog.findViewById(R.id.brinButton);
            final ImageButton ibPos = (ImageButton) dialog.findViewById(R.id.brinButton2);
            final ImageButton ibSend = (ImageButton) dialog.findViewById(R.id.brinButton3);
            final FrameLayout flText = (FrameLayout) dialog.findViewById(R.id.brinFrameLayout1);
            final EditText etText = (EditText) dialog.findViewById(R.id.brinEditText);
            final LinearLayout llDone = (LinearLayout) dialog.findViewById(R.id.brinLinearLayout1);

            etText.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence c, int i, int i1, int i2)
                {
                    ibSend.setEnabled(c.length() < 300 && c.length() > 0);
                }

                @Override
                public void afterTextChanged(Editable e) {}
            });

            ibSend.setEnabled(false);

            tbDia.setNavBack(true);
            tbDia.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    dialog.dismiss();
                }
            });
            tbDia.setTitle(getString(R.string.feedback_dialog_title));

            ibNeg.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    prefs.userGaveFeedback();
                    flText.setVisibility(View.VISIBLE);
                    view.setVisibility(View.GONE);
                    ibPos.setVisibility(View.GONE);
                    new CountDownTimer(300, 200)
                    {
                        public void onTick(long millisUntilFinished) {}

                        public void onFinish()
                        {
                            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(etText, 0);
                        }
                    }.start();
                }
            });

            ibPos.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    sendFeedbackOverAnalytics(null);
                    prefs.userGaveFeedback();
                    view.setVisibility(View.GONE);
                    ibNeg.setVisibility(View.GONE);
                    llDone.setVisibility(View.VISIBLE);
                    new CountDownTimer(1500, 900)
                    {
                        public void onTick(long millisUntilFinished) {}

                        public void onFinish()
                        {
                            dialog.dismiss();
                        }
                    }.start();
                }
            });

            ibSend.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    sendFeedbackOverAnalytics(etText.getText().toString());
                    view.setEnabled(false);
                    flText.setVisibility(View.GONE);
                    llDone.setVisibility(View.VISIBLE);
                    new CountDownTimer(1500, 900)
                    {
                        public void onTick(long millisUntilFinished) {}

                        public void onFinish()
                        {
                            dialog.dismiss();
                        }
                    }.start();
                }
            });

            dialog.show();
        }
        catch (Exception e)
        {
            //sendTrackMsg(e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendFeedbackOverAnalytics(String text)
    {
        try
        {
            sendTrackMsg(MessageFormat.format("USER_FEED|VERS_{0}|{1}|", BuildConfig.VERSION_CODE, text == null ? "POS_" + System.currentTimeMillis() : text));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private int zoneNameDownloadCount = 0;

    public void getZoneNames()
    {
        try
        {
            initializeVolleyQueue(getApplicationContext());
            final int zoneCount = prefs.deviceZones();
            for (int zs = zoneCount; zs > 0; zs--)
            {
                final int z = zs;
                String u;
                switch (zs)
                {
                    case 1:
                        u = url.mainInfo;
                        break;
                    default:
                        u = url.zoneInfo + z;
                        break;
                }
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + prefs.deviceHostname() + u, new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String r)
                    {
                        try
                        {
                            if (r != null)
                            {
                                String name;
                                final String cont1 = "<RenameZone><value>";
                                final String cont2 = "</value></RenameZone>";
                                if (r.contains(cont1) && r.contains(cont2))
                                {
                                    name = r.substring(r.indexOf(cont1) + cont1.length());
                                    name = name.substring(0, name.indexOf(cont2));
                                    name = name.trim();
                                    Log.d(TAG, "onDownloadResponse: ZONE" + z + "/" + name);
                                    if (name.length() > 0)
                                    {
                                        onZoneNamesResult(z, name);
                                        zoneNameDownloadCount++;
                                        if (zoneCount == zoneNameDownloadCount)
                                            onZoneNamesDone(true);
                                    } else
                                    {
                                        throw new PackageManager.NameNotFoundException("Name too short");
                                    }
                                } else
                                {
                                    throw new Resources.NotFoundException("Tag not found");
                                }
                            } else
                            {
                                throw new NullPointerException("response is null");
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            onZoneNamesResult(z, null);
                            zoneNameDownloadCount++;
                            if (zoneCount == zoneNameDownloadCount) onZoneNamesDone(true);
                        }
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        onZoneNamesResult(z, null);
                        zoneNameDownloadCount++;
                        if (zoneCount == zoneNameDownloadCount) onZoneNamesDone(false);
                    }
                });
                volleyQueue.add(stringRequest);
            }
            onZoneNamesStart();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            onZoneNamesDone(false);
        }
    }

    public void onZoneNamesResult(int z, String n) {}

    public void onZoneNamesDone(boolean success) {}

    public void onZoneNamesStart() {}

    public void openActivity(Class b)
    {
        startActivity(new Intent(this, b));
    }

    public void getPrefsByIntent()
    {
        long t = getIntent().getLongExtra(INTENT_RCR_OBJ, -1);
        if (t == -1) Log.e(TAG, "CAN NOT LOAD INTENT!");
        prefs = new Prefs(this, t);
        prefVolButtons = prefs.isVolControlEnabled();
    }

    public void sendFeedback()
    {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("vnd.android.cursor.item/email");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"breakinginterest@yahoo.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.help));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "\n\n\n(" + "user:" + userId + ")");
        startActivity(Intent.createChooser(emailIntent, "Send mail using..."));
    }

    public void openVoiceAssistant()
    {
        if (getStoredVoiceCommands().size() > 0)
        {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_prompt));
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            startActivityForResult(intent, reqVoice);
        } else
        {
            openVoiceAssistantSettings();
        }
    }

    public void openVoiceAssistantSettings()
    {
        startActivity(new Intent(this, AssistantSettingsActivity.class));
    }

    public class CheckAppUpdate extends AsyncTask<String, String, String>
    {
        private boolean checkBetaVers = false;

        public CheckAppUpdate(boolean checkBeta)
        {
            checkBetaVers = checkBeta;
        }

        @Override
        protected String doInBackground(String... args)
        {
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try
            {
                urlConnection = (HttpURLConnection) new URL(url.updateURL1).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream())));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    result.append(line);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            } finally
            {
                if (urlConnection != null)
                {
                    urlConnection.disconnect();
                }
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(final String r)
        {
            try
            {
                final JSONObject obj = new JSONObject(r);
                final Integer appCode = BuildConfig.VERSION_CODE;
                Integer updCode;
                String updInfo = null;
                Boolean betaAvailable = false;

                if (checkBetaVers && obj.getJSONObject("upBeta").getBoolean("status"))
                {
                    updCode = obj.getJSONObject("upBeta").getInt("code");
                    try
                    {
                        updInfo = obj.getJSONObject("upBeta").getString("desc");
                    }
                    catch (Exception ignore)
                    {
                    }
                    betaAvailable = true;
                } else
                {
                    updCode = obj.getJSONObject("upRel").getInt("code");
                    try
                    {
                        updInfo = obj.getJSONObject("upRel").getString("desc");
                    }
                    catch (Exception ignore)
                    {
                    }
                }

                if (updCode > appCode)
                {
                    onAppUpdateAvailable(betaAvailable, updInfo, updCode);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void showGplayFreeVersion()
    {
        final String appPackageName = "com.brin.denonremotefree";
        try
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        }
        catch (android.content.ActivityNotFoundException anfe)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void onAppUpdateAvailable(boolean isBeta, String desc, final Integer updCode)
    {
        if (prefs.isSkipVersion(updCode)) return;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_app_update);

        final TextView tvDesc = (TextView) dialog.findViewById(R.id.brinText1);
        final Button btOpen = (Button) dialog.findViewById(R.id.brinButton2);
        final Button btSkip = (Button) dialog.findViewById(R.id.brinButton);
        final Toolbar tbDia = (Toolbar) dialog.findViewById(R.id.brinToolbar);

        if (desc == null || desc.length() == 0)
        {
            tvDesc.setText(MessageFormat.format(getString(R.string.app_update_msg), updCode));
        } else
        {
            tvDesc.setText(desc);
        }

        tbDia.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        tbDia.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });
        tbDia.setTitle(getString(R.string.app_update_title));

        btOpen.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
                showGplayFreeVersion();
            }
        });

        btSkip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                prefs.skipVersionsCode(updCode);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void getPref()
    {
        try
        {
            prefs = DashboardActivity.getPrefs();
            prefVolButtons = prefs.isVolControlEnabled();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            enableAnalytics();
            sendTrackMsg("BRIN_ACT_ERROR/"+e.getMessage());
        }
    }

    public int[] getScreenSize()
    {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int[] s = new int[]{size.x, size.y};
        Log.d(TAG, "getScreenSize: " + Arrays.toString(s));
        return s;
    }

    public void setCrashReporter()
    {
        CustomActivityOnCrash.install(this);
        CustomActivityOnCrash.setErrorActivityClass(CrashReporterActivity.class);
    }

    public void enableToolbarProgess(AppBarLayout abl)
    {
        try
        {
            pbMain = (ProgressBar) LayoutInflater.from(abl.getContext()).inflate(R.layout.layout_progress_top, abl, false);
            abl.addView(pbMain);
            pbMain.setVisibility(View.GONE);
            toolbarProgressbarEnabled = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            toolbarProgressbarEnabled = false;
        }
    }

    public void enableCustomProgress(ProgressBar pb)
    {
        pbMain = pb;
        toolbarProgressbarEnabled = true;
    }

    public void setProgressMax(int max)
    {
        try
        {
            pbMain.setMax(max);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public int getProgressMax()
    {
        try
        {
            return pbMain.getMax();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public void setProgress(boolean i, int pro)
    {
        try
        {
            if (toolbarProgressbarEnabled && tbMain != null)

                if (pro == 0)
                {
                    if (pbMain.getVisibility() == View.VISIBLE) pbMain.setVisibility(View.GONE);

                } else
                {
                    if (pbMain.getVisibility() != View.VISIBLE)
                    {
                        pbMain.setVisibility(View.VISIBLE);
                        //pbLoad.startAnimation(animSlideTopIn);
                    }
                    pbMain.setIndeterminate(i);
                    if (!i)
                    {
                        /*
                        if (pbMain.getProgress() > pro)
                            pbMain.startAnimation(AnimationUtils.loadAnimation(this,android.R.anim.fade_in));
                            */
                        pbMain.setProgress(pro);
                        //Log.d(TAG, "WEIKUG3: " + pro);
                        pbMain.setIndeterminate(pro == pbMain.getMax());
                    }
                }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public int getProgress()
    {
        try
        {
            return pbMain.getProgress();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public void setToolbar(BrinToolbar tb)
    {
        try
        {
            setSupportActionBar(tb);
            tbMain = tb;
            tb.animateLayoutChanges(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setSecToolbar(BrinToolbar tb)
    {
        try
        {
            tbSec = tb;
            tb.animateLayoutChanges(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public ActionBar getToolbar()
    {
        try
        {
            return getSupportActionBar();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public boolean setSecTitle(String title)
    {
        try
        {
            Log.d(TAG, "setSecTitle: " + title);
            tbSec.setTitle(title);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setTitle(String title)
    {
        try
        {
            Log.d(TAG, "setTitle: " + title);
            if (title != null) title = title.toUpperCase();
            getToolbar().setTitle(title);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setSubTitle(String sub)
    {
        try
        {
            tbMain.setSubtitle(sub);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
/*
    public boolean setSecSubTitle(String sub)
    {
        try
        {
            Log.d(TAG, "setSecSubTitle: " + sub+"/"+tbSec.getSubtitle());
            if (sub == null)
            {
                if (tbSec.getSubtitle() == null)
                    return false;
                //if (!tbSec.getSubtitle().equals(" "))
                //{
                    tbSec.getChildAt(2).setVisibility(View.GONE);
                //}
                tbSec.setSubtitle(" ");
                mainSecSubTitleNull = true;
            } else
            {
                if (tbSec.getSubtitle() != null && sub.equalsIgnoreCase(tbSec.getSubtitle().toString()) && tbSec.getChildAt(2).getVisibility() == View.VISIBLE)
                    return false;
                tbSec.setSubtitle(sub.toUpperCase());
                mainSecSubTitleNull = false;
                tbSec.getChildAt(2).setVisibility(View.VISIBLE);
                tbSec.getChildAt(2).startAnimation(AnimationUtils.loadAnimation(this, R.anim.window_fade_in));
            }
            return true;
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    */

    public void setSecSubTitle(String sub)
    {
        try
        {
            tbSec.setSubtitle(sub);
        }
        catch (Exception e)
        {
            Log.e(TAG, "setSecSubTitle: ERROR", e);
            e.printStackTrace();
        }
    }

    public boolean isSecSubTitleVisible()
    {
        try
        {
            return tbSec.isSubTitleVisible();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public void enableSecTitleMarquee(boolean enabled)
    {
        tbSec.enableTitleMarquee(enabled);
    }

    public void enableTitleMarquee(boolean enabled)
    {
        try
        {
            tbMain.enableTitleMarquee(enabled);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void enableSubTitleMarquee(boolean enabled)
    {
        try
        {
            tbSec.enableSubTitleMarquee(enabled);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setNavBack(boolean enable)
    {
        try
        {
            tbMain.setNavBack(enable);
            tbMain.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onBackPressed(false);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        onBackPressed(true);
    }

    public void onBackPressed(boolean hardware)
    {
        super.onBackPressed();
    }

    public void setSecNavBack(boolean enable)
    {
        try
        {
            if (tbSec.isNavBackEnabled() == enable) return;
            tbSec.setNavBack(enable);
            tbSec.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onSecBackPressed();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onSecBackPressed()
    {
        onBackPressed(false);
    }

    public void openBroadcast()
    {
        try
        {
            if (broadcastEnabled()/* && isActivityVisible()*/ && !broadCastActive)
            {
                final IntentFilter filter = new IntentFilter();
                filter.addAction(coms.serviceID);

                bcReceiver = new BroadcastReceiver()
                {
                    @Override
                    public void onReceive(Context context, Intent intent)
                    {
                        try
                        {
                            switch (intent.getIntExtra("id", -1))
                            {
                                case BROAD_CON_WAIT:
                                    onConnecting();
                                    break;
                                case BROAD_CON_SUCC:
                                    onConnected(intent.getStringExtra("msg"), intent.getStringExtra("ip"));
                                    break;
                                case BROAD_CON_DIS:
                                    int err = intent.getIntExtra("err", -1);
                                    onConnectionError(err, intent.getStringExtra("ip"));
                                    break;
                                case BROAD_MSG:
                                    Log.d("wlevnkekwbjg", intent.getStringExtra("msg"));
                                    onTelnetResult(intent.getStringExtra("msg"));
                                    break;
                                case BROAD_NSE_RES:
                                    onNseResult((ArrayList<String>) new Gson().fromJson(intent.getStringExtra("msg"), ArrayList.class));
                                    break;
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                try
                {
                    registerReceiver(bcReceiver, filter);
                    broadCastActive = true;
                    onBroadcastOpen(ReceiverConnectionService.socketConnected);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    //broadCastActive = false;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onNseResult(ArrayList<String> al)
    {

    }

    public boolean broadcastEnabled()
    {
        return true;
    }

    private boolean isAnalyticsEnabled = false;

    public void enableAnalytics()
    {
        try
        {
            if (!BuildConfig.DEBUG && !isAnalyticsEnabled)
            {
                analytics = GoogleAnalytics.getInstance(this);
                analytics.setLocalDispatchPeriod(1800);
                tracker = analytics.newTracker("UA-59925624-1");
                tracker.enableExceptionReporting(true);
                tracker.enableAdvertisingIdCollection(true);
                tracker.enableAutoActivityTracking(true);
                tracker.setScreenName(this.getClass().getSimpleName());
                userId = ReturnClass.getUserId(this);
                tracker.setClientId(userId);
                isAnalyticsEnabled = true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sendTrackMsg(String m)
    {
        try
        {
            if (!BuildConfig.DEBUG)
                tracker.send(new HitBuilders.EventBuilder().setCategory(userId).setAction(m).build());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void bindViews()
    {
        ButterKnife.bind(this);
    }

    public void startTelnetService(ReceiverStored rc, boolean testConn)
    {
        try
        {
            if (!ReceiverConnectionService.serviceActive)
            {
                Intent si = new Intent(getApplicationContext(), ReceiverConnectionService.class);
                si.putExtra(INTENT_RCR_OBJ, ReceiverTools.ReceiverStoredToJson(rc));
                si.putExtra("TEST", testConn);
                startService(si);
                serviceStartedByThis = true;
            } else
            {
                serviceStartedByThis = false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean stopTelnetService()
    {
        try
        {
            ReceiverConnectionService.showNotifyConnection(false, getApplicationContext());
            stopService(new Intent(this, ReceiverConnectionService.class));
            serviceStartedByThis = false;
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public void onConnecting()
    {

    }

    public void onConnected(String hostName, String ipAddress)
    {

    }

    public void onConnectionError(int err, String ip)
    {

    }

    public void onTelnetResult(String msg)
    {
        Log.d(TAG, "onTelnetResult: " + msg);
    }

    public void onBroadcastOpen(boolean isConn)
    {

    }

    public void onBrinActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == reqVoice)
        {
            onVoiceAssistantResult(resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onVoiceAssistantResult(int status, Intent data)
    {
        try
        {
            Log.d(TAG, "onVoiceAssistantResult: " + status);
            switch (status)
            {
                case RESULT_OK:
                    ArrayList<String> c = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (c.size() > 0)
                    {
                        final String com = c.get(0).toLowerCase();
                        if (getStoredVoiceCommands().contains(com))
                        {
                            Toast.makeText(getApplicationContext(), "Command found!", Toast.LENGTH_LONG).show();
                        } else
                        {
                            Toast.makeText(getApplicationContext(), MessageFormat.format("Command ({0}) not found", com), Toast.LENGTH_LONG).show();
                        }
                    }
                    return;
                case RecognizerIntent.RESULT_AUDIO_ERROR:
                    Toast.makeText(getApplicationContext(), "RESULT_AUDIO_ERROR", Toast.LENGTH_LONG).show();
                    break;
                case RecognizerIntent.RESULT_CLIENT_ERROR:
                    Toast.makeText(getApplicationContext(), "RESULT_CLIENT_ERROR", Toast.LENGTH_LONG).show();
                    break;
                case RecognizerIntent.RESULT_NETWORK_ERROR:
                    Toast.makeText(getApplicationContext(), "RESULT_NETWORK_ERROR", Toast.LENGTH_LONG).show();
                    break;
                case RecognizerIntent.RESULT_NO_MATCH:
                    Toast.makeText(getApplicationContext(), "RESULT_NO_MATCH", Toast.LENGTH_LONG).show();
                    break;
                case RecognizerIntent.RESULT_SERVER_ERROR:
                    Toast.makeText(getApplicationContext(), "RESULT_SERVER_ERROR", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private ArrayList<VoiceCommand> alVoiceCommands = null;

    public ArrayList<VoiceCommand> getStoredVoiceCommands()
    {
        try
        {
            if (alVoiceCommands != null) return alVoiceCommands;

            Set<String> s = prefs.getVoiceCommands();
            ArrayList<VoiceCommand> alV = new ArrayList<>();
            for (String i : s)
            {
                alV.add(VoiceCommands.FromJson(i));
            }
            alVoiceCommands = alV;
            return alV;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void storeVoiceCommands(ArrayList<VoiceCommand> alV)
    {
        try
        {
            Set<String> s = new HashSet<>();
            alVoiceCommands = alV;
            for (VoiceCommand i : alV)
            {
                s.add(VoiceCommands.ToJson(i));
            }
            prefs.setVoiceCommands(s);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void closeBroadcast()
    {
        if (broadcastEnabled())
        {
            try
            {
                Log.d(TAG, "closeBroadcast: ");
                if (bcReceiver != null && broadCastActive) unregisterReceiver(bcReceiver);
                broadCastActive = false;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public boolean isServiceStartedByThis()
    {
        return serviceStartedByThis;
    }

    public void sendTelnetCom(String c)
    {
        ReceiverConnectionService.sendCom(c);
    }

    public void sendTelnetCom()
    {
    }

    public void sendDirectTelnetCom(String c)
    {
        ReceiverConnectionService.pushCom(c);
    }
}
