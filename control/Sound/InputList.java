package com.brin.denonremotefree.control.Sound;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.BrinObj.Input;
import com.brin.denonremotefree.BrinObj.Zone;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.db.InputTools;
import com.brin.denonremotefree.widgets.BrinToolbar;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InputList extends BrinActivity
{

    @Bind(R.id.brinRecycler) SuperRecyclerView srvMain;
    @Bind(R.id.brinRoot) CoordinatorLayout clRoot;
    @Bind(R.id.brinToolbar) BrinToolbar tbMain;

    private RecyclerView.Adapter rvAdapter;
    private RecyclerView.LayoutManager rvManager;
    public static ArrayList<Input> alList;
    private String TAG = "INPUT.LIST";
    private Zone zone;
    private int lastInp = -1;

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.activity_sound_list);

        zone = Zone.ZoneFromJson(getIntent().getStringExtra("ZONE_OBJ"));
        if (zone == null) return;
        ButterKnife.bind(this);
        setToolbar(tbMain);
        setNavBack(true);
        getPref();
        setTitle(getString(R.string.input) + " " + prefs.getDeviceZoneName(zone.getZoneId()));

        alList = InputTools.inputList();
        for (Input in : alList)
        {
            String r = prefs.getInputRename(in.id);
            if (r != null)
            {
                in.setNameNew(r);
                alList.set(in.id - 1, in);
            }
        }

        int col = 2;

        switch (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
        {
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                col = 1;
                break;
        }

        srvMain.setAnimation(AnimationUtils.loadAnimation(this, R.anim.window_fade_in));
        rvManager = new GridLayoutManager(this, col);
        srvMain.setLayoutManager(rvManager);
        rvAdapter = new InputListAdapter();
        srvMain.setAdapter(rvAdapter);
        //updateBooleanAl();
        //rvAdapter.notifyDataSetChanged();
    }

    private void onSelect(int i)
    {
        sendTelnetCom(zone.setInput(alList.get(i).com));
    }

    @Override
    public void onBroadcastOpen(boolean isConn)
    {
        super.onBroadcastOpen(isConn);
        sendTelnetCom(zone.getInput());
    }

    @Override
    public void onTelnetResult(String l)
    {
        switch (zone == null ? 1 : zone.getZoneId())
        {
            case 1:
                if (l.startsWith("SI"))
                {
                    l = l.trim().substring(2);
                    int inpId = InputTools.getInpIdFromReceiver(l);
                    if (inpId > 0)
                    {
                        setItemSelected(inpId - 1);
                    }
                    return;
                }
                break;
            default:
                if (l.startsWith("Z" + zone.getZoneId()))
                {
                    Log.d(TAG, "onTelnetResult: 1");
                    String l2 = l.substring(2);
                    if (!l2.startsWith("ON") && !l2.startsWith("OFF"))
                    {
                        l = l.trim().substring(2);
                        int inpId = InputTools.getInpIdFromReceiver(l);
                        if (inpId > 0)
                        {
                            setItemSelected(inpId - 1);
                        }
                    }
                    return;
                }
                break;
        }
    }

    private int modeLast = -1;

    private void setItemSelected(int i)
    {
        if (i != modeLast)
        {
            Input inpCur = alList.get(i);
            inpCur.active = true;
            alList.set(i, inpCur);
            rvAdapter.notifyItemChanged(i);

            if (modeLast >= 0)
            {
                Input inpLast = alList.get(modeLast);
                inpLast.active = false;
                alList.set(modeLast, inpLast);
                rvAdapter.notifyItemChanged(modeLast);
            }
        }
        modeLast = i;
    }

    private class InputListAdapter extends RecyclerView.Adapter<InputListAdapter.ViewHolderClass>
    {
        class ViewHolderClass extends RecyclerView.ViewHolder
        {
            private TextView tvItem;
            private ImageView ivItemCheck;
            private ImageView ivItemIcon;
            private View item;

            ViewHolderClass(View itemView)
            {
                super(itemView);
                tvItem = (TextView) itemView.findViewById(R.id.brinText1);
                ivItemCheck = (ImageView) itemView.findViewById(R.id.brinCheckMark);
                ivItemIcon = (ImageView) itemView.findViewById(R.id.brinIcon);
                item = itemView;
            }
        }

        @Override
        public InputListAdapter.ViewHolderClass onCreateViewHolder(ViewGroup vg, int i)
        {
            View v = LayoutInflater.from(vg.getContext()).inflate(R.layout.item_input_list, vg, false);
            return new InputListAdapter.ViewHolderClass(v);
        }

        @Override
        public void onBindViewHolder(final InputListAdapter.ViewHolderClass vhc, final int i)
        {
            try
            {
                Input input = alList.get(i);
                vhc.ivItemIcon.setImageResource(input.iconResSmall);
                vhc.tvItem.setText(input.getName());
                if (!input.active)
                    vhc.ivItemCheck.setImageDrawable(null);
                else
                    vhc.ivItemCheck.setImageResource(R.drawable.check_100_white);
                vhc.item.setTag(i);
                vhc.item.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onSelect(Integer.valueOf(v.getTag().toString()));
                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount()
        {
            return alList.size();
        }
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
