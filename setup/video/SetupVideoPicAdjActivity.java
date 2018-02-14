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

import com.brin.denonremotefree.BuildConfig;
import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.activation.BuyKeyActivity;
import com.brin.denonremotefree.widgets.BrinToolbar;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class SetupVideoPicAdjActivity extends BrinActivity
{

    private ArrayList<String> al1, al2, al3, al4, al5, al6, al7, al8, alGet;

    @Bind(R.id.brinViewFlipper) ViewFlipper vfMain;
    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinToolbarSec) BrinToolbar tbSec;
    @Bind(R.id.brinLinearLayout1) LinearLayout llMain;

    @Bind(R.id.rgSetupVideoPic1) RadioGroup rg1;
    @Bind(R.id.rgSetupVideoPic7) RadioGroup rg7;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) CustomActivityOnCrash.install(this);
        setContentView(R.layout.activity_setup_video_pic_adj);
        ButterKnife.bind(this);

        setToolbar(tbMain);
        setSecToolbar(tbSec);
        enableTitleMarquee(true);
        enableSecTitleMarquee(true);
        setNavBack(true);
        setSecNavBack(true);
        setTitle(getString(R.string.setup_video_picture_title));
        getPref();

        al1 = new ArrayList<>();
        al2 = new ArrayList<>();
        al3 = new ArrayList<>();
        al4 = new ArrayList<>();
        al5 = new ArrayList<>();
        al6 = new ArrayList<>();
        al7 = new ArrayList<>();
        al8 = new ArrayList<>();
        alGet = new ArrayList<>();

        al1.addAll(Arrays.asList("PVOFF", "PVSTD", "PVMOV", "PVVVD", "PVSTM", "PVCTM", "PVDAY", "PVNGT"));
        al2.addAll(Arrays.asList("PVCN DOWN", "PVCN UP"));
        al3.addAll(Arrays.asList("PVBR DOWN", "PVBR UP"));
        al4.addAll(Arrays.asList("PVCM DOWN", "PVCM UP"));
        al5.addAll(Arrays.asList("PVST DOWN", "PVST UP"));
        al6.addAll(Arrays.asList("PVHUE DOWN", "PVHUE UP"));
        al7.addAll(Arrays.asList("PVDNR OFF", "PVDNR LOW", "PVDNR MID", "PVDNR HI"));
        al8.addAll(Arrays.asList("PVENH DOWN", "PVENH UP"));
        alGet.addAll(Arrays.asList("PV?", "PVCN ?", "PVBR ?", "PVCM ?", "PVST ?", "PVHUE ?", "PVDNR ?", "PVENH ?"));

        addSetupItem(getString(R.string.picture_mode), R.drawable.picture_100_white, false);
        addSetupItem(getString(R.string.contrast), R.drawable.contrast_100_white, false);
        addSetupItem(getString(R.string.brightness), R.drawable.brightness_100_white, false);
        addSetupItem(getString(R.string.chroma), R.drawable.tv_100_white, false);
        addSetupItem(getString(R.string.saturation), R.drawable.saturation_100_white, false);
        addSetupItem(getString(R.string.hue), R.drawable.hue_100_white, false);
        addSetupItem(getString(R.string.noise_reduction), R.drawable.tv_100_white, false);
        addSetupItem(getString(R.string.enhancer), R.drawable.tv_100_white, false);
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

    public void onSetting4(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al4.get(id));
    }

    public void onSetting5(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al5.get(id));
    }

    public void onSetting6(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al6.get(id));
    }

    public void onSetting7(View v)
    {
        int id = Integer.valueOf(v.getTag().toString());
        if (lic())
            sendTelnetCom(al7.get(id));
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
                ((RadioButton) rg1.getChildAt(al1.indexOf(res) + 1)).setChecked(true);
            } else if (al7.contains(res))
            {
                ((RadioButton) rg7.getChildAt(al7.indexOf(res) + 1)).setChecked(true);
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
