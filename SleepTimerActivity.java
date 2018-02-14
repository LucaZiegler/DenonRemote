package com.brin.denonremotefree;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.BrinObj.ZoneSleepTimer;
import com.brin.denonremotefree.widgets.BrinToolbar;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SleepTimerActivity extends BrinActivity
{
    private ArrayList<ArrayList<ZoneSleepTimer>> alList = new ArrayList<>();

    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinLinearLayout1) LinearLayout llRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_timer);

        ButterKnife.bind(this);
        setToolbar(tbMain);
        setNavBack(true);
        setTitle(getString(R.string.title_add_element));

        prefs = DashboardActivity.getPrefs();
        if (prefs == null)
        {
            finish();
            return;
        }

        setResult(DashboardActivity.ELEMENT_INTENT_CANCELLED);
    }

    private void addTimerItem()
    {
        FrameLayout llItem = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.item_sleep_timer, llRoot, false);
        llRoot.addView(llItem);
    }
}
