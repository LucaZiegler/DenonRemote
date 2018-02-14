package com.brin.denonremotefree.binding;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.brin.denonremotefree.BuildConfig;
import com.brin.denonremotefree.DashboardActivity;
import com.brin.denonremotefree.Helper.ConnectActivity;
import com.brin.denonremotefree.Helper.Prefs;
import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.BrinObj.BrinMulticast;
import com.brin.denonremotefree.BrinObj.ReceiverStored;
import com.brin.denonremotefree.BrinObj.ReceiverTools;
import com.brin.denonremotefree.PrepareActivity;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.service.ReceiverConnectionService;
import com.brin.denonremotefree.solve.CrashReporterActivity;
import com.brin.denonremotefree.widgets.BrinToolbar;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class ReceiverListActivity extends BrinActivity
{
    //@Bind(R.id.vfRcrScan) ViewFlipper vfMain;
    @Bind(R.id.brinList) SuperRecyclerView srvMain;
    @Bind(R.id.brinToolbar) BrinToolbar tb1;
    @Bind(R.id.brinToolbarSec) BrinToolbar tb2;
    @Bind(R.id.brinCoordinatorLayout) CoordinatorLayout clRoot;

    @BindString(R.string.receiver_list_subtitle) String defSubtitle;
    @BindString(R.string.receiver_list_title) String defTitle;

    private static RecyclerView.Adapter rvAdapter;
    private RecyclerView.LayoutManager rvManager;
    private static final String TAG = "RCR.SCAN ";
    private static ArrayList<ReceiverStored> alReceiver = new ArrayList<>();
    private boolean manuallyDisconnected = false;
    private final String demoDeviceName = "demo denon receiver";
    public static final String INTENT_RECEIVER = "A", INTENT_AUTO_CONN = "B";

    @Override
    public boolean broadcastEnabled()
    {
        return false;
    }

    @Override
    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        CustomActivityOnCrash.install(this);
        CustomActivityOnCrash.setErrorActivityClass(CrashReporterActivity.class);
        setContentView(R.layout.activity_receiver_list);
        ButterKnife.bind(this);

        enableAnalytics();

        prefs = new Prefs(this);
        isActivityVisible = true;
        isActivityActive = true;
        Intent intent = getIntent();
        manuallyDisconnected = intent.getBooleanExtra("DISCONNECT", false);
        Log.d(TAG, "onCreate: DIS: " + manuallyDisconnected);

        setupToolbar();

        srvMain.setAnimation(AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_top));
        rvManager = new GridLayoutManager(this, 1);
        srvMain.setLayoutManager(rvManager);
        rvAdapter = new ReceiverListAdapter();
        srvMain.setAdapter(rvAdapter);

        checkStart();
        loadReceiverSet();

        if (manuallyDisconnected)
        {
            showSnackBar(getString(R.string.error_disconnected), 2500);
        }

        brinMulticast.sendMsg("BRIN_ALL_GET");
        checkRunning(null);
    }

    private void checkStart()
    {
        if (prefs.isFirstStart())
        {
            addReceiver();
        }
    }

    private boolean checkRunning(ReceiverStored rc)
    {
        try
        {
            if (ReceiverConnectionService.serviceActive)
            {
                long storedTimeConnected = ReceiverConnectionService.getReceiverStored().storedTime;
                if (rc == null)
                {
                    for (ReceiverStored rcSearch : alReceiver)
                    {
                        if (rcSearch.storedTime == storedTimeConnected)
                        {
                            rc = rcSearch;
                            break;
                        }
                    }
                }
                if (rc != null && storedTimeConnected == rc.storedTime && ReceiverConnectionService.socketConnected)
                {
                    openApp(rc.storedTime);
                    return true;
                } else
                {
                    stopTelnetService();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static void onNetworkChanged(Context c, boolean wifiConnected)
    {
        Log.d(TAG, "onNetworkChanged: " + wifiConnected);
        //showSnackBar(wifiConnected ? "Wifi connected" : "Wifi disconnected", 2000);
        if (!isActivityActive()) return;
        try
        {
            routerMac = null;
            getMacFromRouter(c);
            rvAdapter.notifyItemRangeChanged(0, alReceiver.size());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private BrinMulticast brinMulticast = new BrinMulticast(new BrinMulticast.BrinMulticastListener()
    {
        @Override
        public void onMessageReceived(BrinMulticast context, String msg)
        {
            if (msg.startsWith("BRIN_"))
            {
                if (msg.endsWith("GET_ANSW"))
                {

                }
            }
        }

        @Override
        public void onServerError()
        {
        }

        @Override
        public void onClientError()
        {
        }
    });

    private void setupToolbar()
    {
        setToolbar(tb1);
        setTitle(defTitle);
        setSecToolbar(tb2);
        setSecTitle(defSubtitle);
    }

    public void onAdd(View v)
    {
        addReceiver();
    }

    private void loadReceiverSet()
    {
        try
        {
            int autoCnctId = -1;
            alReceiver = prefs.loadReceiver();
            if (alReceiver.size() > 0)
            {
                int i = 0;
                for (ReceiverStored r : alReceiver)
                {
                    if (r.receiverPrimary)
                    {
                        autoCnctId = i;
                    }
                    i++;
                }
                rvAdapter.notifyItemRangeInserted(0, i);

                if (autoCnctId != -1)
                {
                    if (manuallyDisconnected)
                    {
                        stopTelnetService();
                    } else
                    {
                        Log.d(TAG, "loadReceiverSet: onCntWithReceiver");
                        onCntWithReceiver(autoCnctId);
                        return;
                    }
                }
            }

            if (prefs.isDevOptionsActivated())
            {
                ReceiverStored rsDemo = new ReceiverStored();
                rsDemo.storedTime = System.nanoTime();
                rsDemo.receiverPrimary = false;
                rsDemo.receiverHostName = demoDeviceName;
                rsDemo.receiverIp = "0.0.0.0";
                rsDemo.receiverZones = 4;
                rsDemo.receiverManufacturer = 1;
                rsDemo.deviceDemo = true;
                addListItem(rsDemo, false);
            }

            new CheckAppUpdate(prefs.checkBetaUpdate()).execute();
        }
        catch (Exception e)
        {
            printExceptionWithToast(e);
        }
    }

    @Override
    public void onAppUpdateAvailable(boolean isBeta, String desc, Integer updCode)
    {
        if (isActivityVisible()) super.onAppUpdateAvailable(isBeta, desc, updCode);
    }

    private void sortReceiver(ArrayList<ReceiverStored> al)
    {
        Collections.sort(al, new Comparator<ReceiverStored>()
        {
            @Override
            public int compare(ReceiverStored rc1, ReceiverStored rc2)
            {
                return String.valueOf(rc2.storedTime).compareTo(String.valueOf(rc1.storedTime)) * -1;
            }
        });
    }

    private static String routerMac = null;

    private static String getMacFromRouter(Context c)
    {
        try
        {
            if (routerMac == null)
            {
                WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                routerMac = wifiInfo.getBSSID();
                Log.d(TAG, "getMacFromRouter: routerMac = " + routerMac);
                return routerMac;
            } else
            {
                Log.d(TAG, "getMacFromRouter: routerMac = " + routerMac);
                return routerMac;
            }
        }
        catch (Exception e)
        {
            routerMac = null;
            e.printStackTrace();
            return null;
        }
    }

    private void makeToast(String s)
    {
        if (BuildConfig.DEBUG) Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        Log.d(TAG, "makeToast: " + s);
    }

    private void printExceptionWithToast(Exception e)
    {
        e.printStackTrace();
        if (BuildConfig.DEBUG) Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    /*
        private void storeReceiverSet()
        {
            stIps = new HashSet<>();
            for (ReceiverStored r : alSavedReceiver)
            {
                if (!r.deviceDemo) stIps.add(ReceiverTools.ReceiverStoredToJson(r));
                Log.d(TAG, "storeReceiverSet: size " + stIps.size());
            }
            if (stIps.size() == 0) spMain.edit().putStringSet(Prefs.SAV_RCR, null).apply();
            else spMain.edit().putStringSet(Prefs.SAV_RCR, stIps).apply();
            Log.d(TAG, "storeReceiverSet: " + stIps);
        }
    */
    private void addListItem(ReceiverStored r, boolean store)
    {
        alReceiver.add(r);
        if (store)
        {
            prefs.storeReceiver(alReceiver);
        }
        rvAdapter.notifyItemInserted(alReceiver.size() - 1);
    }

    private static final int AddReceiverToken = 744;
    private static final int ConnectReceiverToken = 838;

    private void addReceiver()
    {
        if (prefs.isAppActivated() || alReceiver.size() == 0)
        {
            stopTelnetService();
            Intent i = new Intent(this, ReceiverScanActivity.class);
            startActivityForResult(i, AddReceiverToken);
        } else
        {
            showSnackBar(getString(R.string.error_receiver_key), 3000);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode);
        isActivityVisible = true;
        try
        {
            switch (requestCode)
            {
                case AddReceiverToken:
                    if (resultCode == RESULT_OK)
                    {
                        String jsonReceiverStored = data.getStringExtra(INTENT_RECEIVER);
                        sendTrackMsg("RCR_ADDED/"+jsonReceiverStored);
                        boolean autoConn = data.getBooleanExtra(INTENT_AUTO_CONN, false);
                        if (jsonReceiverStored != null)
                        {
                            addListItem(ReceiverTools.ReceiverStoredFromJson(jsonReceiverStored), true);
                            if (autoConn) setReceiverAsPrimary(alReceiver.size() - 1, true);
                            onCntWithReceiver(alReceiver.size() - 1);
                        }
                    }
                    break;
                case ConnectReceiverToken:
                    if (resultCode == RESULT_OK)
                    {
                        openApp(data.getLongExtra(INTENT_RCR_OBJ, -1));
                    }
                    break;
            }
        }
        catch (Exception e)
        {
            showSnackBar(e.getMessage(), 2000);
        }
    }


    class ReceiverListAdapter extends RecyclerView.Adapter<ReceiverListAdapter.ViewHolderClass>
    {
        class ViewHolderClass extends RecyclerView.ViewHolder
        {
            private final View ivIndiPrim;
            private TextView tvTitle1;
            private TextView tvTitle2;
            private ImageButton ibMore;
            private ImageView ivWarn;
            private FrameLayout flMain;

            ViewHolderClass(View v)
            {
                super(v);
                ivIndiPrim = v.findViewById(R.id.brinIcon);
                tvTitle1 = (TextView) v.findViewById(R.id.brinText1);
                tvTitle2 = (TextView) v.findViewById(R.id.brinText2);
                ibMore = (ImageButton) v.findViewById(R.id.brinButton);
                ivWarn = (ImageView) v.findViewById(R.id.brinIcon2);
                flMain = (FrameLayout) v;
            }
        }

        @Override
        public ViewHolderClass onCreateViewHolder(ViewGroup vg, int i)
        {
            View v = LayoutInflater.from(vg.getContext()).inflate(R.layout.item_receiver_list, vg, false);
            return new ViewHolderClass(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolderClass vhc, final int i)
        {
            try
            {
                Log.d(TAG, "onBindViewHolder: pos = " + i);
                final ReceiverStored rc = alReceiver.get(i);
                vhc.tvTitle1.setText(rc.receiverAltName == null ? rc.receiverHostName : rc.receiverAltName);

                if (rc.receiverZones > 1)
                {
                    vhc.tvTitle2.setText(MessageFormat.format("{0} {1}", rc.receiverZones, getString(R.string.zones)));
                } else
                {
                    vhc.tvTitle2.setText(MessageFormat.format("{0} {1}", rc.receiverZones, getString(R.string.zone)));
                }

                if (rc.receiverPrimary)
                {
                    vhc.ivIndiPrim.setVisibility(View.VISIBLE);
                } else
                {
                    vhc.ivIndiPrim.setVisibility(View.GONE);
                }

                try
                {
                    if (rc.routerMac != null)
                    {
                        if (rc.routerMac.equals(getMacFromRouter(getApplicationContext())))
                            vhc.ivWarn.setImageResource(R.drawable.av_receiver_100_white);
                        else
                        {
                            vhc.ivWarn.setImageResource(R.drawable.av_receiver_error_100_white);
                            vhc.ivWarn.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Toast.makeText(getApplicationContext(), getString(R.string.receiver_list_mac_warn), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        Log.d(TAG, "onBindViewHolder: routerMac = " + rc.routerMac);
                    } else
                    {
                        vhc.ivWarn.setVisibility(View.GONE);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                vhc.flMain.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Log.d(TAG, "onClick: onCntWithReceiver");
                        onCntWithReceiver(i);
                    }
                });

                vhc.ibMore.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        try
                        {
                            setReceiverElmtExp(i);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
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
            return alReceiver.size();
        }
    }

    private void setReceiverElmtExp(int p)
    {
        try
        {
            ReceiverStored rc = alReceiver.get(p);
            showReceiverEditDialog(p, rc);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            showSnackBar("Can not show dialog :(", 1500);
        }
    }

    private void showReceiverEditDialog(final int p, final ReceiverStored rc)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_receiver_edit);
        final String label = rc.receiverAltName == null ? rc.receiverHostName : rc.receiverAltName;

        final FrameLayout ll1 = (FrameLayout) dialog.findViewById(R.id.brinButton);
        final FrameLayout ll2 = (FrameLayout) dialog.findViewById(R.id.brinButton2);
        final FrameLayout ll3 = (FrameLayout) dialog.findViewById(R.id.brinButton3);
        final ViewFlipper vf = (ViewFlipper) dialog.findViewById(R.id.brinViewFlipper);
        final EditText et = (EditText) dialog.findViewById(R.id.brinEditText);
        final ImageButton ibDone = (ImageButton) dialog.findViewById(R.id.brinButtonSave);
        final CheckBox cbAutoConn = (CheckBox) dialog.findViewById(R.id.brinCheckBox1);
        final BrinToolbar tbDia = (BrinToolbar) dialog.findViewById(R.id.brinToolbar);

        tbDia.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        tbDia.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (vf.getDisplayedChild() > 0)
                {
                    vf.setVisibility(View.GONE);
                    vf.setDisplayedChild(0);
                    vf.setVisibility(View.VISIBLE);
                    et.setText(null);
                } else
                {
                    dialog.dismiss();
                }
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(et.getWindowToken(), 0);
            }
        });
        tbDia.setTitle(label);
        tbDia.enableTitleMarquee(true);

        et.setHint(label);
        cbAutoConn.setChecked(rc.receiverPrimary);

        ll1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean checked = !cbAutoConn.isChecked();
                cbAutoConn.setChecked(checked);
                setReceiverAsPrimary(p, checked);
            }
        });

        ll2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                vf.setVisibility(View.GONE);
                vf.setDisplayedChild(1);
                vf.setVisibility(View.VISIBLE);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        });

        ll3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                removeListItem(p);
                dialog.dismiss();
            }
        });

        ibDone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(et.getWindowToken(), 0);
                renameReceiver(p, et.getText() == null || et.getText().length() == 0 ? rc.receiverHostName : et.getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void renameReceiver(int p, String s)
    {
        try
        {
            ReceiverStored rc = alReceiver.get(p);
            rc.receiverAltName = s;
            alReceiver.set(p, rc);
            rvAdapter.notifyItemChanged(p);
            prefs.storeReceiver(alReceiver);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void onCntWithReceiver(int i)
    {
        try
        {
            if (!isActivityVisible()) return;
            Log.d(TAG, "onCntWithReceiver: " + i);
            ReceiverStored rc = alReceiver.get(i);
            prefs = new Prefs(this, rc);
            if (checkRunning(rc) || ConnectActivity.isActivityVisible()) return;
            brinMulticast.sendMsg(MessageFormat.format("BRIN_RCR_{0}_DIS", prefs.deviceHostname()));
            Intent intent = new Intent(this, ConnectActivity.class);
            intent.putExtra(INTENT_RCR_OBJ, rc.toJson());
            startActivityForResult(intent, ConnectReceiverToken);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            printExceptionWithToast(e);
        }
    }

    private void setReceiverAsPrimary(int p, boolean to)
    {
        try
        {
            boolean listChanged = false;
            int c = 0;
            for (ReceiverStored rcScan : alReceiver)
            {
                if (c == p)
                {
                    if (rcScan.receiverPrimary != to)
                    {
                        rcScan.receiverPrimary = to;
                        alReceiver.set(c, rcScan);
                        rvAdapter.notifyItemChanged(c);
                        listChanged = true;
                    }
                } else
                {
                    if (rcScan.receiverPrimary)
                    {
                        rcScan.receiverPrimary = false;
                        alReceiver.set(c, rcScan);
                        rvAdapter.notifyItemChanged(c);
                        listChanged = true;
                    }
                }
                c++;
            }
            if (listChanged) prefs.storeReceiver(alReceiver);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void removeListItem(int i)
    {
        try
        {
            ReceiverStored rc = alReceiver.get(i);
            Prefs prefs = new Prefs(this, rc.storedTime);
            prefs.deleteReceiver();
            alReceiver.remove(i);
            rvAdapter.notifyDataSetChanged();
            prefs.storeReceiver(alReceiver);
            showSnackBar(getString(R.string.msg_receiver_deleted), 3000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_receiver_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuItem1:
                addReceiver();
                break;
            case R.id.menuItem2:
                openSettings();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackBar(String t, int l)
    {
        Snackbar s = Snackbar.make(clRoot, t, l);
        View sView = s.getView();
        sView.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_primary_color));
        s.show();
    }

    @Override
    public void openApp(long tag)
    {
        if (tag == -1)
        {
            showSnackBar("CRITICAL INTENT ERROR", 3000);
            return;
        }
        Log.d(TAG, "openApp: " + isActivityVisible());
        if (isActivityVisible())
        {
            prefs = new Prefs(this, tag);
            boolean firstStart = prefs.isDashboardFirstOpened();
            Intent i = new Intent(getApplicationContext(), firstStart ? PrepareActivity.class : DashboardActivity.class);
            Log.d(TAG, "openApp: " + firstStart);
            i.putExtra(INTENT_RCR_OBJ, tag);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
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
