package com.brin.denonremotefree.setup.video;

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
import com.brin.denonremotefree.widgets.BrinToolbar;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;


public class SetupVideoActivity extends BrinActivity
{
    private ArrayList<ArrayList<String>> alArray = new ArrayList<>();

    @Bind(R.id.brinViewFlipper) ViewFlipper vfMain;
    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinToolbarSec) BrinToolbar tbSec;
    @Bind(R.id.brinLinearLayout1) LinearLayout llMain;

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setCrashReporter();
        setContentView(R.layout.activity_setup_video);
        ButterKnife.bind(this);

        setToolbar(tbMain);
        setSecToolbar(tbSec);
        enableTitleMarquee(true);
        enableSecTitleMarquee(true);
        setNavBack(true);
        setSecNavBack(true);
        setTitle(getString(R.string.setup_video_title));
        getPref();

        alArray.add(new ArrayList<>(Arrays.asList("SSCVO ZMA", "SSCVO Z2S")));
        alArray.add(new ArrayList<>(Arrays.asList("SSOSDVOL OFF", "SSOSDVOL BOT", "SSOSDVOL TOP")));
        alArray.add(new ArrayList<>(Arrays.asList("SSOSDTXT OFF", "SSOSDTXT ON")));
        alArray.add(new ArrayList<>(Arrays.asList("SSOSDPBS OFF", "SSOSDPBS 30S", "SSOSDPBS ALW")));
        alArray.add(new ArrayList<>(Arrays.asList("SSCVO ?", "SSOSDVOL ?")));

        addSetupItem(getString(R.string.picture), R.drawable.picture_100_white, true);
        addSetupItem(getString(R.string.hdmi_setup), R.drawable.hdmi_100_white, true);
        addSetupItem(getString(R.string.component_video_out), R.drawable.scart_100_white, false);
        addSetupItem(getString(R.string.osd), R.drawable.osd_100_white, false);
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
            sendTelnetCom(alArray.get(1).get(id));
    }

    public void onSetting2_1(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(alArray.get(2).get(id));
    }

    public void onSetting2_2(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(alArray.get(3).get(id));
    }

    public void onSetting2_3(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(alArray.get(4).get(id));
    }

    @Override
    public void onTelnetResult(String res)
    {
        try
        {
            int i = 0;
            for (ArrayList<String> al : alArray)
            {
                if (i > 0)
                {
                    if (al.contains(res))
                    {
                        ((RadioButton) ((RadioGroup) vfMain.getChildAt(i)).getChildAt(al.indexOf(res) + 1)).setChecked(true);
                        break;
                    }
                }
                i++;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
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
        switch (id)
        {
            case 1:
                openActivity(SetupVideoPicAdjActivity.class);
                break;
            case 2:
                openActivity(SetupVideoHdmiActivity.class);
                break;
            case 3:
                setSecTitle(text);
                setChild(1);
                break;
            case 4:
                setSecTitle(text);
                setChild(2);
                break;
        }
    }

    public void setChild(int id)
    {
        sendTelnetCom(alArray.get(0).get(id - 1));
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
