package com.brin.denonremotefree.setup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.activation.BuyKeyActivity;
import com.brin.denonremotefree.widgets.BrinToolbar;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;


public class SetupGeneralActivity extends BrinActivity
{
    private ArrayList<String> al1, al2, al3_1, al3_2, al4, al5_1, al5_2, al6, al8, al10, alGet;

    @Bind(R.id.brinViewFlipper) ViewFlipper vfMain;
    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinToolbarSec) BrinToolbar tbSec;
    @Bind(R.id.brinLinearLayout1) LinearLayout llMain;

    @Bind(R.id.rgSetupGen1) RadioGroup rg1;
    @Bind(R.id.rgSetupGen2) RadioGroup rg2;
    @Bind(R.id.rgSetupGen3_1) RadioGroup rg3_1;
    @Bind(R.id.rgSetupGen3_2) RadioGroup rg3_2;
    @Bind(R.id.rgSetupGen4) RadioGroup rg4;
    @Bind(R.id.rgSetupGen5_1) RadioGroup rg5_1;
    @Bind(R.id.rgSetupGen5_2) RadioGroup rg5_2;
    @Bind(R.id.rgSetupGen6) RadioGroup rg6;
    @Bind(R.id.rgSetupGen8) RadioGroup rg8;
    @Bind(R.id.rgSetupGen10) RadioGroup rg10;

    @Bind(R.id.etGenRen1) EditText etRen1;
    @Bind(R.id.etGenRen2) EditText etRen2;
    @Bind(R.id.etGenRen3) EditText etRen3;
    @Bind(R.id.etSetupGenRenReceiver) EditText etRenReceiver;

    private String TAG = "SETUP.GENERAL.";

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setCrashReporter();
        setContentView(R.layout.activity_setup_general);
        ButterKnife.bind(this);

        setToolbar(tbMain);
        setSecToolbar(tbSec);
        enableTitleMarquee(true);
        enableSecTitleMarquee(true);
        setNavBack(true);
        setSecNavBack(true);
        setTitle(getString(R.string.setup_general_title));
        getPref();

        al1 = new ArrayList<>();
        al2 = new ArrayList<>();
        al3_1 = new ArrayList<>();
        al3_2 = new ArrayList<>();
        al4 = new ArrayList<>();
        al5_1 = new ArrayList<>();
        al5_2 = new ArrayList<>();
        al6 = new ArrayList<>();
        al8 = new ArrayList<>();
        al10 = new ArrayList<>();
        alGet = new ArrayList<>();

        if (prefs.deviceManufacturer() == 0)
        {
            al1.addAll(Arrays.asList("SSASB OFF", "SSASB 15M", "SSASB 30M", "SSASB 60M"));
            al3_1.addAll(Arrays.asList("SSDIM OFF", "SSDIM DAR", "SSDIM DIM", "SSDIM BRI"));
            al3_2.addAll(Arrays.asList("SSDIMCNL IN", "SSDIMCNL OUT"));
            alGet.addAll(Arrays.asList("SSASB ?", "", "SSDIM ?", "", "TR?", "", "", "SSSUD ?", "", "SSLAN ?"));
        } else
        {
            al1.addAll(Arrays.asList("STBYOFF", "STBY15M", "STBY30M", "STBY60M"));
            al3_1.addAll(Arrays.asList("DIM　OFF", "DIM　DAR", "DIM　DIM", "DIM　BRI"));
            al3_2.addAll(Arrays.asList("SSDIMCNL IN", "SSDIMCNL OUT"));
            alGet.addAll(Arrays.asList("STBY?", "ECO?", "DIM　?", "SSOSD ?", "TR?", "", "", "SSSUD ?", "", "SSLAN ?"));
            rg3_2.setVisibility(View.GONE);
        }

        al2.addAll(Arrays.asList("ECOOFF", "ECOAUTO", "ECOON"));
        al4.addAll(Arrays.asList("SSOSDSCR OFF", "SSOSDSCR ON"));
        al5_1.addAll(Arrays.asList("TR1 OFF", "TR1 ON"));
        al5_2.addAll(Arrays.asList("TR2 OFF", "TR2 ON"));
        al6.addAll(Arrays.asList("SYREMOTE LOCK OFF", "SYREMOTE LOCK ON", "SYPANEL LOCK OFF", "SYPANEL LOCK ON", "SYPANEL+V LOCK ON"));
        al8.addAll(Arrays.asList("SSSUD NO", "SSSUD YES"));
        al10.addAll(Arrays.asList("SSLAN ENG", "SSLAN DEU", "SSLAN FRA", "SSLAN ITA", "SSLAN ESP", "SSLAN NER", "SSLAN SVE", "SSLAN JPN", "SSLAN CHI", "SSLAN POL", "SSLAN RUS"));

        etRen1.setHint(prefs.getDeviceZoneName(1));
        etRen2.setHint(prefs.getDeviceZoneName(2));
        etRen3.setHint(prefs.getDeviceZoneName(3));

        //etRenReceiver.setHint(sp.getString("friendlyName",""));

        etRen1.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO)
                {
                    View view = new View(getApplicationContext());
                    view.setTag("1");
                    onRename(view);
                    handled = true;
                }
                return handled;
            }
        });
        etRen2.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO)
                {
                    View view = new View(getApplicationContext());
                    view.setTag("2");
                    onRename(view);
                    handled = true;
                }
                return handled;
            }
        });
        etRen3.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO)
                {
                    View view = new View(getApplicationContext());
                    view.setTag("3");
                    onRename(view);
                    handled = true;
                }
                return handled;
            }
        });
        addSetupItem(getString(R.string.auto_standby), R.drawable.standby_100_white, false);
        addSetupItem(getString(R.string.eco_mode), R.drawable.eco_100_white, false);
        addSetupItem(getString(R.string.display), R.drawable.osd_100_white, false);
        addSetupItem(getString(R.string.screen_saver), R.drawable.screen_saver_100_white, false);
        addSetupItem(getString(R.string.trigger), R.drawable.trigger_100_white, false);
        addSetupItem(getString(R.string.control_lock), R.drawable.lock2_100_white, false);
        addSetupItem(getString(R.string.zone_rename), R.drawable.rename_100_white, false);
        addSetupItem(getString(R.string.usage_data), R.drawable.usage_100_white, false);
        addSetupItem(getString(R.string.rename_receiver), R.drawable.rename_receiver_100_white, false);
        addSetupItem(getString(R.string.language), R.drawable.language_100_white, false);
    }

    private int setupItemCount = 0;

    private void addSetupItem(String title, int icon, boolean showOpenAct)
    {
        setupItemCount++;
        FrameLayout flItem = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.item_setup_list, llMain, false);
        if (!showOpenAct)
            ((LinearLayout) flItem.getChildAt(0)).removeViewAt(2);
        TextView tvItem = (TextView) flItem.findViewById(R.id.brinText1);
        ImageView ivIcon = (ImageView) flItem.findViewById(R.id.brinIcon);
        tvItem.setText(title);
        ivIcon.setImageResource(icon);
        flItem.setTag(setupItemCount);
        llMain.addView(flItem);
    }

    private void setDisChild(int t)
    {
        vfMain.setVisibility(View.GONE);
        vfMain.setDisplayedChild(t);
        tbSec.setVisibility(t == 0 ? View.GONE : View.VISIBLE);
        vfMain.setVisibility(View.VISIBLE);
    }

    private void VolleySetSetting8(String renameZone, String renameTo)
    {
        try
        {
            final String postData = "textZoneRename" + renameZone + "=" + renameTo + "&setZoneRename" + renameZone + "=on";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + prefs.deviceHostname() + "/SETUP/GENERAL/ZONERENAME/s_general.asp", new Response.Listener<String>()
            {
                @Override
                public void onResponse(String res)
                {
                    //pbWait.setVisibility(View.GONE);
                    Log.d("VolleySetSetting8", res);
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
                }
            })
            {
                /*
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                */
                @Override
                public byte[] getBody() throws AuthFailureError
                {
                    try
                    {
                        return postData == null ? null : postData.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee)
                    {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", postData, "utf-8");
                        return null;
                    }
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(stringRequest);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onRename(View v)
    {
        if (lic())
        {
            String renameZone = "";
            String renameTo = "";
            Integer to = Integer.valueOf(v.getTag().toString());
            switch (to)
            {
                case 1:
                    renameZone = "MainZone";
                    renameTo = etRen1.getText().toString();
                    break;
                case 2:
                    renameZone = "Zone2";
                    renameTo = etRen2.getText().toString();
                    break;
                case 3:
                    renameZone = "Zone3";
                    renameTo = etRen3.getText().toString();
                    break;
            }
            //sp.edit().putString("nameZone"+to.toString(),renameTo).apply();
            VolleySetSetting8(renameZone, renameTo);
        }
    }

    public void onSetting1(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al1.get(id));
    }

    public void onSetting2(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al2.get(id));
    }

    public void onSetting3_1(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al3_1.get(id));
    }

    public void onSetting3_2(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al3_2.get(id));
    }

    public void onSetting4(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al4.get(id));
    }

    public void onSetting5_1(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al5_1.get(id));
    }

    public void onSetting5_2(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al5_2.get(id));
    }

    public void onSetting6(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al6.get(id));
    }

    public void onSetting8(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al8.get(id));
    }

    @Override
    public void onTelnetResult(String res)
    {
        try
        {
            if (al1.contains(res))
            {
                ((RadioButton) rg1.getChildAt(al1.indexOf(res) )).setChecked(true);
            } else if (al2.contains(res))
            {
                ((RadioButton) rg2.getChildAt(al2.indexOf(res) )).setChecked(true);
            } else if (al3_1.contains(res))
            {
                ((RadioButton) rg3_1.getChildAt(al3_1.indexOf(res) )).setChecked(true);
            } else if (al3_2.contains(res))
            {
                ((RadioButton) rg3_2.getChildAt(al3_2.indexOf(res) )).setChecked(true);
            } else if (al4.contains(res))
            {
                ((RadioButton) rg4.getChildAt(al4.indexOf(res) )).setChecked(true);
            } else if (al5_1.contains(res))
            {
                ((RadioButton) rg5_1.getChildAt(al5_1.indexOf(res) )).setChecked(true);
            } else if (al5_2.contains(res))
            {
                ((RadioButton) rg5_2.getChildAt(al5_2.indexOf(res) )).setChecked(true);
            } else if (al8.contains(res))
            {
                ((RadioButton) rg8.getChildAt(al8.indexOf(res) )).setChecked(true);
            } else if (al10.contains(res))
            {
                ((RadioButton) rg10.getChildAt(al10.indexOf(res) )).setChecked(true);
            } else if (res.contains("R1") || res.contains("R2") || res.contains("R3") || res.contains("NSFRN "))
            {
                renameSucces();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void renameSucces()
    {
        Toast.makeText(getApplicationContext(), getString(R.string.zone_rename_success), Toast.LENGTH_LONG).show();
    }

    private boolean lic()
    {
        if (!prefs.isAppActivated())
        {
            Toast.makeText(getApplicationContext(), getString(R.string.pro_required), Toast.LENGTH_SHORT).show();
        }
        return prefs.isAppActivated();
    }

    public void onItem(View v)
    {
        String text = ((TextView) ((LinearLayout) (((FrameLayout) v).getChildAt(0))).getChildAt(1)).getText().toString();
        int id = Integer.valueOf(v.getTag().toString());
        setSecTitle(text);
        setChild(id);
    }

    public void setChild(int id)
    {
        try
        {
            sendTelnetCom(alGet.get(id - 1));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        setDisChild(id);
    }

    public void onRenameReceiver(View v)
    {
        String t = etRenReceiver.getText().toString();
        if (t.length() > 1)
        {
            sendTelnetCom("NSFRN " + t);
            //sp.edit().putString("friendlyName", t).apply();
        }
    }

    @Override
    public void onBackPressed(boolean h)
    {
        if (h)
        {
            goBack();
        } else
        {
            finish();
        }
    }

    private void goBack()
    {
        if (vfMain.getDisplayedChild() != 0)
        {
            setDisChild(0);
        } else
        {
            finish();
        }
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
        if (!prefs.isAppActivated())
            getMenuInflater().inflate(R.menu.menu_show_buy, menu);
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
        }
        return super.onOptionsItemSelected(item);
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
