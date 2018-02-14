package com.brin.denonremotefree.setup.audio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.activation.BuyKeyActivity;
import com.brin.denonremotefree.widgets.BrinToolbar;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SetupAudioSpeakerActivity extends BrinActivity
{
    private ArrayList<String> al1, al2, al3, al4, al5, al6, al7, al8, al9, al10, alGet;

    @Bind(R.id.brinViewFlipper) ViewFlipper vfMain;
    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinToolbarSec) BrinToolbar tbSec;
    @Bind(R.id.brinLinearLayout1) LinearLayout llMain;

    @Bind(R.id.rgSetupAudioSpeaker1) RadioGroup rg1;
    @Bind(R.id.rgSetupAudioSpeaker2) RadioGroup rg2;
    @Bind(R.id.rgSetupAudioSpeaker3) RadioGroup rg3;
    @Bind(R.id.rgSetupAudioSpeaker4) RadioGroup rg4;
    @Bind(R.id.rgSetupAudioSpeaker5) RadioGroup rg5;
    @Bind(R.id.rgSetupAudioSpeaker6) RadioGroup rg6;
    @Bind(R.id.rgSetupAudioSpeaker7) RadioGroup rg7;
    @Bind(R.id.rgSetupAudioSpeaker8) RadioGroup rg8;
    @Bind(R.id.rgSetupAudioSpeaker9) RadioGroup rg9;
    @Bind(R.id.rgSetupAudioSpeaker10) RadioGroup rg10;

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setCrashReporter();
        setContentView(R.layout.activity_setup_audio_speaker);
        ButterKnife.bind(this);

        setToolbar(tbMain);
        setSecToolbar(tbSec);
        enableTitleMarquee(true);
        enableSecTitleMarquee(true);
        setNavBack(true);
        setSecNavBack(true);
        setTitle(getString(R.string.setup_audio_speaker_title));

        getPref();

        al1 = new ArrayList<>();
        al2 = new ArrayList<>();
        al3 = new ArrayList<>();
        al4 = new ArrayList<>();
        al5 = new ArrayList<>();
        al6 = new ArrayList<>();
        al7 = new ArrayList<>();
        al8 = new ArrayList<>();
        al9 = new ArrayList<>();
        al10 = new ArrayList<>();
        alGet = new ArrayList<>();

        al1.addAll(Arrays.asList("PSFRONT SPA", "PSFRONT SPB", "PSFRONT A+B"));
        al2.addAll(Arrays.asList("PSSP:OFF", "PSSP:FR", "PSSP:HF", "PSSP:FL", "PSSP:BW", "PSSP:BH", "PSSP:HW", "PSSP:SB", "PSSP:FW", "PSSP:FH"));
        al3.addAll(Arrays.asList("PSSWR OFF", "PSSWR ON"));
        al4.addAll(Arrays.asList("PSTONE CTRL OFF", "PSTONE CTRL ON"));
        al5.addAll(Arrays.asList("PSLOM OFF", "PSLOM ON"));
        al6.addAll(Arrays.asList("PSDEH OFF", "PSDEH LOW", "PSDEH MED", "PSDEH HIGH"));
        al7.addAll(Arrays.asList("PSPAN OFF", "PSPAN ON"));
        al8.addAll(Arrays.asList("PSNEURAL OFF", "PSNEURAL ON"));
        al9.addAll(Arrays.asList("PSDSX OFF", "PSDSX ONW", "PSDSX ONH", "PSDSX ONHW"));
        al10.addAll(Arrays.asList("SSPAAMOD 91C", "SSPAAMOD NOR", "SSPAAMOD ZO2", "SSPAAMOD ZO3", "SSPAAMOD ZOM", "SSPAAMOD BIA", "SSPAAMOD FRB", "SSPAAMOD 2CH"));
        alGet.addAll(Arrays.asList("PSFRONT?", "PSSP: ?", "PSSWR ?", "PSTONE CTRL ?", "PSLOM ?", "PSDEH ?", "PSPAN ?", "PSNEURAL ?", "PSDSX ?", "SSPAA ?"));
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

    public void onStng1(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al1.get(id));
    }

    public void onStng2(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al2.get(id));
    }

    public void onStng3(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al3.get(id));
    }

    public void onStng4(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al4.get(id));
    }

    public void onStng5(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al5.get(id));
    }

    public void onStng6(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al6.get(id));
    }

    public void onStng7(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al7.get(id));
    }

    public void onStng8(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al8.get(id));
    }

    public void onStng9(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al9.get(id));
    }

    public void onStng10(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al10.get(id));
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
    public void onTelnetResult(String res)
    {
        try
        {
            Log.d("TELNET.RES/", res);


            if (al1.contains(res))
            {
                ((RadioButton) rg1.getChildAt(al1.indexOf(res))).setChecked(true);
            } else if (al2.contains(res))
            {
                ((RadioButton) rg2.getChildAt(al2.indexOf(res))).setChecked(true);
            } else if (al3.contains(res))
            {
                ((RadioButton) rg3.getChildAt(al3.indexOf(res))).setChecked(true);
            } else if (al4.contains(res))
            {
                ((RadioButton) rg4.getChildAt(al4.indexOf(res))).setChecked(true);
            } else if (al5.contains(res))
            {
                ((RadioButton) rg5.getChildAt(al5.indexOf(res))).setChecked(true);
            } else if (al6.contains(res))
            {
                ((RadioButton) rg6.getChildAt(al6.indexOf(res))).setChecked(true);
            } else if (al7.contains(res))
            {
                ((RadioButton) rg7.getChildAt(al7.indexOf(res))).setChecked(true);
            } else if (al8.contains(res))
            {
                ((RadioButton) rg8.getChildAt(al8.indexOf(res))).setChecked(true);
            } else if (al9.contains(res))
            {
                ((RadioButton) rg9.getChildAt(al9.indexOf(res))).setChecked(true);
            } else if (al10.contains(res))
            {
                ((RadioButton) rg10.getChildAt(al10.indexOf(res))).setChecked(true);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void openActivity(Intent b)
    {
        startActivity(b);
    }

    public void onItem(View v)
    {
        setSecTitle(((Button)v).getText().toString());
        int id = Integer.valueOf(v.getTag().toString());
        setChild(id);
    }

    public void setChild(int id)
    {
        sendTelnetCom(alGet.get(id - 1));
        setDisChild(id);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public void onSecBackPressed()
    {
        goBack();
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
