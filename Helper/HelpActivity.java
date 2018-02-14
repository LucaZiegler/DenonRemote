package com.brin.denonremotefree.Helper;

import android.os.Bundle;

import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.widgets.BrinToolbar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HelpActivity extends BrinActivity
{

    @Override
    public boolean broadcastEnabled()
    {
        return false;
    }

    @Bind(R.id.brinToolbar) BrinToolbar tbMain;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ButterKnife.bind(this);
        setToolbar(tbMain);
        setNavBack(true);
        setTitle(getString(R.string.help_title));
        enableTitleMarquee(true);
    }
}
