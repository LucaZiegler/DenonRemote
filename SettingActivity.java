package com.brin.denonremotefree;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.activation.BuyKeyActivity;
import com.brin.denonremotefree.extras.LevelActivity;
import com.brin.denonremotefree.setup.SetupGeneralActivity;
import com.brin.denonremotefree.setup.SetupInputActivity;
import com.brin.denonremotefree.setup.SetupZoneActivity;
import com.brin.denonremotefree.setup.audio.SetupAudioActivity;
import com.brin.denonremotefree.setup.video.SetupVideoActivity;
import com.brin.denonremotefree.widgets.BrinToolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;


public class SettingActivity extends BrinActivity
{

    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinLinearLayout1) LinearLayout llMain;

    @Override
    public boolean broadcastEnabled()
    {
        return false;
    }

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        CustomActivityOnCrash.install(this);
        setContentView(R.layout.activity_setup);
        ButterKnife.bind(this);

        setToolbar(tbMain);
        setNavBack(true);
        setTitle("Setup");
        getPref();

        addSetupItem(getString(R.string.audio), R.drawable.music_100_white);
        addSetupItem(getString(R.string.video), R.drawable.tv_100_white);
        addSetupItem(getString(R.string.general), R.drawable.settings_100_white);
        addSetupItem(getString(R.string.speaker_control), R.drawable.speaker_levels_100_white);
        addSetupItem(getString(R.string.inputs), R.drawable.input_100_white);
        addSetupItem(getString(R.string.zones), R.drawable.speaker_100_white);
    }

    private int setupItems = 0;

    private void addSetupItem(String title, int icon)
    {
        LinearLayout llHori;
        boolean addHori;
        if (setupItems == 0 || setupItems % 2 == 0)
        {
            llHori = new LinearLayout(this);
            llHori.setOrientation(LinearLayout.HORIZONTAL);
            llHori.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            addHori = true;
        } else
        {
            llHori = (LinearLayout) llMain.getChildAt(llMain.getChildCount() - 1);
            addHori = false;
        }

        FrameLayout llItem = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.item_setup_main, llMain, false);
        TextView tvItem = (TextView) llItem.findViewById(R.id.brinText1);
        ImageView ivIcon = (ImageView) llItem.findViewById(R.id.brinIcon);
        tvItem.setText(title);
        ivIcon.setImageResource(icon);
        llItem.setTag(setupItems);
        llHori.addView(llItem);
        if (addHori)
            llMain.addView(llHori);
        setupItems++;
    }

    public void openSetup(View v)
    {
        int s = Integer.valueOf(v.getTag().toString());
        switch (++s)
        {
            case 1:
                openActivity(SetupAudioActivity.class);
                break;
            case 2:
                openActivity(SetupVideoActivity.class);
                break;
            case 3:
                openActivity(SetupGeneralActivity.class);
                break;
            case 4:
                openActivity(LevelActivity.class);
                break;
            case 5:
                openActivity(SetupInputActivity.class);
                break;
            case 6:
                openActivity(SetupZoneActivity.class);
                break;
            default:
                Toast.makeText(this,"Invalid tag ("+s+")",Toast.LENGTH_SHORT).show();
                break;
        }
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

    private void onPro()
    {
        openActivity(BuyKeyActivity.class);
        finish();
    }

}
