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

public class SetupVideoHdmiActivity extends BrinActivity
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
        setContentView(R.layout.activity_setup_video_hdmi);
        ButterKnife.bind(this);

        setToolbar(tbMain);
        setSecToolbar(tbSec);
        enableTitleMarquee(true);
        enableSecTitleMarquee(true);
        setNavBack(true);
        setSecNavBack(true);
        setTitle(getString(R.string.setup_video_hdmi_title));
        getPref();

        alArray.add(new ArrayList<>(Arrays.asList("VSMONI ?", "VSSC ?", "VSSCH ?", "VSVST ?", "VSAUDIO ?", "VSASP ?", "VSVPM ?")));
        alArray.add(new ArrayList<>(Arrays.asList("VSMONIAUTO", "VSMONI1", "VSMONI2")));
        alArray.add(new ArrayList<>(Arrays.asList("VSSC48P", "VSSC10I", "VSSC72P", "VSSC10P", "VSSC10P24", "VSSC4K", "VSSC4KF", "VSSCAUTO")));
        alArray.add(new ArrayList<>(Arrays.asList("VSSCH48P", "VSSCH10I", "VSSCH72P", "VSSCH10P", "VSSCH10P24", "VSSCH4K", "VSSCH4KF", "VSSCHAUTO")));
        alArray.add(new ArrayList<>(Arrays.asList("VSVST OFF", "VSVST ON")));
        alArray.add(new ArrayList<>(Arrays.asList("VSAUDIO AMP", "VSAUDIO TV")));
        alArray.add(new ArrayList<>(Arrays.asList("VSASPNRM", "VSASPFUL")));
        alArray.add(new ArrayList<>(Arrays.asList("VSVPMAUTO", "VSVPMGAME", "VSVPMMOVI")));


        addSetupItem(getString(R.string.hdmi_out), R.drawable.tv_100_white, false);
        addSetupItem(getString(R.string.hdmi_output), R.drawable.hdmi_100_white, false);
        addSetupItem(getString(R.string.hdmi_resolution), R.drawable.hdmi_100_white, false);
        addSetupItem(getString(R.string.vertical_stetch), R.drawable.vertical_stretch_100_white, false);
        addSetupItem(getString(R.string.hdmi_audio_decode), R.drawable.cpu_100_white, false);
        addSetupItem(getString(R.string.aspect), R.drawable.tv_100_white, false);
        addSetupItem(getString(R.string.video_process), R.drawable.tv_100_white, false);
    }

    public void onSetting1(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(alArray.get(1).get(id));
    }

    public void onSetting2(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(alArray.get(2).get(id));
    }

    public void onSetting3(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(alArray.get(3).get(id));
    }

    public void onSetting4(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(alArray.get(4).get(id));
    }

    public void onSetting5(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(alArray.get(5).get(id));
    }

    public void onSetting6(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(alArray.get(6).get(id));
    }

    public void onSetting7(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(alArray.get(7).get(id));
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
                        ((RadioButton) ((RadioGroup)vfMain.getChildAt(i)).getChildAt(al.indexOf(res) + 1)).setChecked(true);
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

    private void setChild(int id)
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

    private boolean lic()
    {
        if (!prefs.isAppActivated())
        {
            Toast.makeText(getApplicationContext(), getString(R.string.pro_required), Toast.LENGTH_SHORT).show();
        }
        return prefs.isAppActivated();
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

    public void onItem(View v)
    {
        String text = ((TextView) ((LinearLayout) (((FrameLayout) v).getChildAt(0))).getChildAt(1)).getText().toString();
        int id = Integer.valueOf(v.getTag().toString());
        setSecTitle(text);
        setChild(id);
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
