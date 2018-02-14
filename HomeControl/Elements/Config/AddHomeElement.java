package com.brin.denonremotefree.HomeControl.Elements.Config;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brin.denonremotefree.DashboardActivity;
import com.brin.denonremotefree.BrinObj.AddableElement;
import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.BrinObj.DragElement;
import com.brin.denonremotefree.BrinObj.ElementTool;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.widgets.BrinToolbar;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddHomeElement extends BrinActivity
{

    private static final String TAG = "ADD.ELEMENT";
    private int slctTab = 0;
    private ArrayList<ArrayList<AddableElement>> alList;
    private RecyclerView.Adapter rvAdapter;
    private RecyclerView.LayoutManager rvManager;

    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinTabLayout) TabLayout tlMain;
    @Bind(R.id.brinRecycler) SuperRecyclerView srvMain;

    @Override
    public boolean broadcastEnabled()
    {
        return false;
    }

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.activity_add_home_element);
        enableAnalytics();
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

        alList = new ArrayList<>();
        alList.add(PrefsElements.returnList1(getApplicationContext()));

        srvMain.setAnimation(AnimationUtils.loadAnimation(this, R.anim.window_fade_in));
        rvManager = new GridLayoutManager(this, 1);
        srvMain.setLayoutManager(rvManager);
        rvAdapter = new AddHomeAdapter();
        srvMain.setAdapter(rvAdapter);

        ArrayList<TabLayout.Tab> tabs = new ArrayList<>();
        tabs.add(tlMain.newTab().setText(getString(R.string.control)).setTag(0));
        //tabs.add(tlMain.newTab().setText("info").setTag(1));
        //tabs.add(tlMain.newTab().setText("setup").setTag(2));

        for (TabLayout.Tab tab : tabs)
        {
            tlMain.addTab(tab);
        }

        tlMain.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.accent_color));
        tlMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                setList(Integer.valueOf(String.valueOf(tab.getTag())));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
            }
        });
        //alList.add(PrefsElements.returnList2());
        //alList.add(PrefsElements.returnList3());
        Log.d(TAG, "onCreate: LIST.1.SIZE" + alList.get(0).size());
        setList(slctTab);
    }

    private void setList(int i)
    {
        Log.d(TAG, "setList: " + i);
        slctTab = i;
        rvAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        setResult(DashboardActivity.ELEMENT_INTENT_CANCELLED);
    }

    private class AddHomeAdapter extends RecyclerView.Adapter<AddHomeAdapter.ViewHolderClass>
    {
        class ViewHolderClass extends RecyclerView.ViewHolder
        {
            // VIEWS
            private TextView tv1, tv2;
            private ImageView iv1;
            private FrameLayout flRoot;
            private FrameLayout flBlock;

            ViewHolderClass(View v)
            {
                super(v);
                // IMPORT VIEWS
                tv1 = (TextView) v.findViewById(android.R.id.text1);
                tv2 = (TextView) v.findViewById(android.R.id.text2);
                iv1 = (ImageView) v.findViewById(android.R.id.icon);
                flRoot = (FrameLayout) v;
                flBlock = (FrameLayout) v.findViewById(android.R.id.widget_frame);
            }
        }

        @Override
        public ViewHolderClass onCreateViewHolder(ViewGroup vg, int i)
        {
            View v = LayoutInflater.from(vg.getContext()).inflate(R.layout.item_add_home_element, vg, false);
            return new ViewHolderClass(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolderClass vhc, final int i)
        {
            try
            {
                // vhc.view
                final AddableElement element = alList.get(slctTab).get(i);
                vhc.iv1.setImageResource(element.getElementIcon());
                vhc.tv1.setText(element.getElementTitle());
                if (element.getElementDesc().length() <= 1)
                {
                    vhc.tv2.setVisibility(View.GONE);
                } else vhc.tv2.setText(element.getElementDesc());
                vhc.flRoot.setTag(i);
                vhc.flBlock.setVisibility(View.GONE);
                vhc.flRoot.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        final AddableElement e = alList.get(slctTab).get(Integer.valueOf(String.valueOf(v.getTag().toString())));
                        if (e.isElementZoneCompatible() && prefs.deviceZones() > 1)
                        {
                            askForZone(e);
                        } else
                        {
                            onSelect(e, 1);
                        }
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
            try
            {
                return alList.get(slctTab).size();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return 0;
            }
        }
    }

    private void askForZone(final AddableElement e)
    {
        try
        {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.layout_dialog_zones);

            LinearLayout llList = (LinearLayout) dialog.findViewById(R.id.brinLinearLayout1);
            TextView tvTitle = (TextView) llList.getChildAt(0);

            tvTitle.setText(getString(R.string.select_zone));

            for (int z = 1; z <= prefs.deviceZones(); z++)
            {
                FrameLayout flItem = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.item_dialog_singlechoice_zone, llList, false);
                CheckedTextView ctv = (CheckedTextView) flItem.getChildAt(1);
                flItem.getChildAt(0).setVisibility(View.GONE);
                ctv.setText(prefs.getDeviceZoneName(z));

                llList.addView(flItem);
                flItem.setTag(z);
                flItem.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        int i = Integer.valueOf(view.getTag().toString());
                        onSelect(e, i);
                        dialog.dismiss();
                    }
                });
            }

            dialog.setCancelable(true);
            dialog.show();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            onSelect(e, 1);
            sendTrackMsg("ERROR_ADD_EL_AFTER/" + ex.toString());
        }
    }

    public void onSelect(AddableElement e, int slctZone)
    {
        DragElement element = new DragElement(e.getElementType());
        element.elementZone = slctZone;
        element.elementZoneSupport = e.isElementZoneCompatible() ? 1 : 0;
        element.elementTitle = ElementTool.GenerateElementTitle(element, e, prefs);

        String jsonElement = ElementTool.DragElementToJson(element);
        if (jsonElement == null)
        {
            this.setResult(DashboardActivity.ELEMENT_INTENT_ERR);
        } else
        {
            Log.d(TAG, "onSelect: ELEMENT: " + jsonElement);
            Intent intent = new Intent();
            intent.putExtra(PrefsElements.intentDataAdd(), jsonElement);
            this.setResult(DashboardActivity.ELEMENT_INTENT_OK, intent);
        }
        finish();
    }

}