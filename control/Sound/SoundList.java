package com.brin.denonremotefree.control.Sound;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.RecyclerItemClickListener;
import com.brin.denonremotefree.db.SoundTools;
import com.brin.denonremotefree.widgets.BrinToolbar;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SoundList extends BrinActivity
{

    @Bind(R.id.brinRecycler) SuperRecyclerView srvMain;
    @Bind(R.id.brinRoot) CoordinatorLayout clRoot;
    @Bind(R.id.brinToolbar) BrinToolbar tbMain;

    private RecyclerView.Adapter rvAdapter;
    private RecyclerView.LayoutManager rvManager;
    private String TAG = "SOUND.LIST";

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.activity_sound_list);

        ButterKnife.bind(this);
        setToolbar(tbMain);
        setNavBack(true);
        setTitle(getString(R.string.sound_list_title));

        int col = 2;

        switch (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK){
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                col = 1;
                break;
        }

        srvMain.setAnimation(AnimationUtils.loadAnimation(this, R.anim.window_fade_in));
        rvManager = new GridLayoutManager(this, col);
        srvMain.setLayoutManager(rvManager);
        rvAdapter = new SoundListAdapter();
        srvMain.setAdapter(rvAdapter);
        srvMain.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        onSelect(position);
                    }
                })
        );
        //updateBooleanAl();
        //rvAdapter.notifyDataSetChanged();
    }

    private void onSelect(int i)
    {
        String souCom = SoundTools.soundComs().get(i);
        sendTelnetCom("MS" + souCom);
    }

    @Override
    public void onBroadcastOpen(boolean isConn)
    {
        super.onBroadcastOpen(isConn);
        sendTelnetCom("MS?");
    }

    @Override
    public void onTelnetResult(String l)
    {
        super.onTelnetResult(l);
        if(l.startsWith("MS"))
        {
            l = l.trim().replaceFirst("MS","");
            if(SoundTools.soundComs().contains(l))
            {
                setItemSelected(SoundTools.soundComs().indexOf(l));
            }
        }
    }

    private int modeLast = -1;
    private int modeCur = -1;

    private void setItemSelected(int i)
    {
        if (i != modeLast)
        {
            modeCur = i;
            rvAdapter.notifyItemChanged(modeLast);
            rvAdapter.notifyItemChanged(modeCur);
        }
        modeLast = i;
    }

    class SoundListAdapter extends RecyclerView.Adapter<SoundListAdapter.ViewHolderClass>
    {
        class ViewHolderClass extends RecyclerView.ViewHolder
        {
            private TextView tvItem;
            private ImageView ivItemCheck;

            ViewHolderClass(View itemView) {
                super(itemView);
                tvItem = (TextView) itemView.findViewById(android.R.id.text1);
                ivItemCheck = (ImageView) itemView.findViewById(android.R.id.icon);
            }
        }

        @Override
        public SoundListAdapter.ViewHolderClass onCreateViewHolder(ViewGroup vg, int i)
        {
            View v = LayoutInflater.from(vg.getContext()).inflate(R.layout.item_sound_list, vg, false);
            return new SoundListAdapter.ViewHolderClass(v);
        }

        @Override
        public void onBindViewHolder(final SoundListAdapter.ViewHolderClass vhc, final int i)
        {
            try
            {
                vhc.tvItem.setText(SoundTools.soundNames().get(i));
                if(modeCur == i)
                {
                    vhc.ivItemCheck.setVisibility(View.VISIBLE);
                } else
                {
                    vhc.ivItemCheck.setVisibility(View.GONE);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount()
        {
            return SoundTools.soundNames().size();
        }
    }
}
