package com.brin.denonremotefree.setup.audio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.brin.denonremotefree.extras.LevelActivity;
import com.brin.denonremotefree.widgets.BrinToolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;


public class SetupAudioActivity extends BrinActivity
{
    private ArrayList<String> al1, al2, al3, alGet;

    @Bind(R.id.brinViewFlipper) ViewFlipper vfMain;
    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinToolbarSec) BrinToolbar tbSec;
    @Bind(R.id.brinLinearLayout1) LinearLayout llMain;

    @Bind(R.id.rgSetupAudio1) RadioGroup rg1;
    @Bind(R.id.rgSetupAudio2) RadioGroup rg2;
    @Bind(R.id.rgSetupAudio3) RadioGroup rg3;

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setCrashReporter();
        setContentView(R.layout.activity_setup_audio);
        ButterKnife.bind(this);

        setToolbar(tbMain);
        setSecToolbar(tbSec);
        enableTitleMarquee(true);
        enableSecTitleMarquee(true);
        setNavBack(true);
        setSecNavBack(true);
        setTitle(getString(R.string.setup_audio_title));
        getPref();

        addSetupItem(getString(R.string.speaker), R.drawable.speaker_100_white, true);
        addSetupItem(getString(R.string.volume), R.drawable.vol_up_100_white, true);
        addSetupItem(getString(R.string.eq), R.drawable.eq_100_white, true);
        addSetupItem(getString(R.string.input), R.drawable.input_100_white, false);
        addSetupItem(getString(R.string.decode_mode), R.drawable.cpu_100_white, false);
        addSetupItem(getString(R.string.decode_mode), R.drawable.sub_100_white, false);

        al1 = new ArrayList<>();
        al2 = new ArrayList<>();
        al3 = new ArrayList<>();
        alGet = new ArrayList<>();

        al1.addAll(Arrays.asList(
                "SDAUTO", "SDHDMI", "SDDIGITAL", "SDANALOG", "SDEXT.IN", "SD7.1IN", "SDNO"
        ));
        al2.addAll(Arrays.asList(
                "DCAUTO", "DCPCM", "DCDTS"
        ));
        al3.addAll(Arrays.asList(
                "SSBLN MAIN", "SSBLN SUB", "SSBLN M+S"
        ));
        alGet.addAll(Arrays.asList(
                "SD?", "DC?", "SSBLN?"
        ));
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

    public void onSetting3(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al3.get(id));
    }

    private boolean lic()
    {
        if (!prefs.isAppActivated())
        {
            Toast.makeText(getApplicationContext(), getString(R.string.pro_required), Toast.LENGTH_SHORT).show();
        }
        return prefs.isAppActivated();
    }

    public void onItem(View v) throws IOException
    {
        String text = ((TextView) ((LinearLayout) (((FrameLayout) v).getChildAt(0))).getChildAt(1)).getText().toString();
        int id = Integer.valueOf(v.getTag().toString());
        switch (id)
        {
            case 1:
                openActivity(SetupAudioSpeakerActivity.class);
                break;
            case 2:
                openActivity(LevelActivity.class);
                break;
            case 3:
                openActivity(SetupAudioEQActivity.class);
                break;
            case 4:
                setSecTitle(text);
                setChild(1);
                sendTelnetCom(alGet.get(id - 4));
                break;
            case 5:
                setSecTitle(text);
                setChild(2);
                sendTelnetCom(alGet.get(id - 4));
                break;
            case 6:
                setSecTitle(text);
                setChild(3);
                sendTelnetCom(alGet.get(id - 4));
                break;
        }
    }

    @Override
    public void onTelnetResult(String l)
    {
        try
        {
            if (al1.contains(l))
            {
                ((RadioButton) rg1.getChildAt(al1.indexOf(l))).setChecked(true);
            } else if (al2.contains(l))
            {
                ((RadioButton) rg2.getChildAt(al2.indexOf(l))).setChecked(true);
            } else if (al3.contains(l))
            {
                ((RadioButton) rg3.getChildAt(al3.indexOf(l))).setChecked(true);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setChild(int id)
    {
        setDisChild(id);
    }

    @Override
    public void onBackPressed()
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
