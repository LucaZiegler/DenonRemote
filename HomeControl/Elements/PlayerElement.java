package com.brin.denonremotefree.HomeControl.Elements;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.brin.denonremotefree.BrinObj.AddableElement;
import com.brin.denonremotefree.BrinObj.BrinDashFragment;
import com.brin.denonremotefree.DashboardActivity;
import com.brin.denonremotefree.Helper.SetView;
import com.brin.denonremotefree.HomeControl.Elements.Config.PrefsElements;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.db.coms;
import com.brin.denonremotefree.db.url;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Luca on 21.06.2016.
 */
public class PlayerElement extends BrinDashFragment
{
    private String TAG = "PLAYER.ELEMENT";
    @Bind(R.id.brinProgressBar) ProgressBar pbMain;
    @Bind(R.id.tvElementPlayerTitle1) TextView tvTitle1;
    @Bind(R.id.tvElementPlayerTitle2) TextView tvTitle2;
    @Bind(R.id.flElementPlayerNav) FrameLayout flNav;
    @Bind(R.id.ivElementPlayerCover) ImageView ivCover;

    @Bind(R.id.ibElementPlayerNav1) ImageButton ibNav1;
    @Bind(R.id.ibElementPlayerNav2) ImageButton ibNav2;
    @Bind(R.id.ibElementPlayerNav3) ImageButton ibNav3;
    @Bind(R.id.ibElementPlayerNav4) ImageButton ibNav4;

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup c, Bundle bRoot)
    {
        Log.d(TAG, "onCreateView: ");
        View view = getLayout(i, c, R.layout.element_player);
        bindButterKnife();
        setProgressBar(pbMain);
        getPrefs();
        getIntentElement();
        if (curElement == null || prefs == null)
        {
            Log.e(TAG, "Element is null");
            updateStatus(BROAD_CON_DIS);
        } else
        {
            updateStatus(BROAD_CON_WAIT);
            getZone();
        }
        return view;
    }

    public AddableElement getAddableElement(Context c)
    {
        return new AddableElement(
                c.getString(R.string.element_player_title),
                c.getString(R.string.element_player_desc),
                false,
                1,
                PrefsElements.TYPE_PLAYER,
                2,
                R.drawable.play_100_white
        );
    }

    public void updateStatus(int s)
    {
        switch (s)
        {
            case BROAD_CON_WAIT:
                showProgress(true);
                flNav.setVisibility(View.GONE);
                ivCover.setVisibility(View.GONE);
                tvTitle2.setVisibility(View.GONE);
                tvTitle1.setVisibility(View.GONE);
                break;
            case BROAD_CON_SUCC:
                showProgress(true);
                flNav.setVisibility(View.GONE);
                ivCover.setVisibility(View.GONE);
                tvTitle2.setVisibility(View.GONE);
                tvTitle1.setVisibility(View.GONE);
                break;
            case BROAD_CON_DIS:
                showProgress(true);
                flNav.setVisibility(View.GONE);
                ivCover.setVisibility(View.GONE);
                tvTitle2.setVisibility(View.GONE);
                tvTitle1.setVisibility(View.GONE);
                break;
            case BROAD_COM_SUCC:
                //showProgress(false);
                break;
        }
    }

    @OnClick(R.id.ibElementPlayerNav1)
    public void onNav1(View v)
    {
        sendTelnetCom(coms.mediaSkipBack);
    }

    @OnClick(R.id.ibElementPlayerNav2)
    public void onNav2(View v)
    {
        sendTelnetCom(coms.mediaStop);
    }

    @OnClick(R.id.ibElementPlayerNav3)
    public void onNav3(View v)
    {
        sendTelnetCom(coms.mediaPause);
    }

    @OnClick(R.id.ibElementPlayerNav4)
    public void onNav4(View v)
    {
        sendTelnetCom(coms.mediaSkipFor);
    }

    @Override
    public void onNseResult(String n)
    {
        Log.e(TAG, "onTelnetNseDone: "+n);
        ArrayList<String> al = new Gson().fromJson(n,new TypeToken<ArrayList<String>>(){}.getType());
        Log.d(TAG, "onTelnetNseDone: " + al.size());
        if (al.size() == 0)
        {
            Log.e(TAG, "onTelnetNseDone: NSE LIST IS EMPTY");
            return;
        }
        updateStatus(BROAD_COM_SUCC);
        if (al.get(0).toLowerCase().startsWith("now playing"))
        {
            SetView.setText(tvTitle1, al.get(1), null);
            SetView.setText(tvTitle2, al.get(2), null);
            flNav.setVisibility(View.VISIBLE);
            tvTitle2.setVisibility(View.VISIBLE);
            getPlayerCover();
        } else
        {
            SetView.setText(tvTitle1, al.get(0), null);
            flNav.setVisibility(View.GONE);
            tvTitle1.setVisibility(View.VISIBLE);
            tvTitle2.setVisibility(View.GONE);
            ivCover.setVisibility(View.GONE);
        }
        showProgress(false);
    }

    private void getPlayerCover()
    {
        ImageRequest ir = new ImageRequest("http://" + prefs.deviceHostname() + url.coverUrl, new Response.Listener<Bitmap>()
        {
            @Override
            public void onResponse(Bitmap result)
            {
                Log.d(TAG, "onResponse: DOWNLOAD COMPLETE");
                try
                {
                    if (result != null)
                    {
                        ivCover.setImageBitmap(result);
                        ivCover.setVisibility(View.VISIBLE);
                    } else
                    {
                        ivCover.setVisibility(View.GONE);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    ivCover.setVisibility(View.GONE);
                }
            }

        }, 0, 0, null, null);

        DashboardActivity.rqMain.add(ir);
        Log.d(TAG, "getPlayerCover: DOWNLOAD INITIALIZED");
    }

    @Override
    public void onConnectionError(int err, String ip)
    {
        Log.d(TAG, "onConnectionError: " + err);
    }

    @Override
    public void onBroadcastOpen(boolean isConn)
    {
        if (isConn)
        {
            updateStatus(BROAD_CON_SUCC);
            getPlayerStatus();
        } else
        {
            updateStatus(BROAD_CON_DIS);
        }
    }

    private void getPlayerStatus()
    {
        sendTelnetCom("NSE");
        //sendTelnetCom("NSLASSTA ?");
    }
}
