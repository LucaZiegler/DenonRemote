package com.brin.denonremotefree.binding;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.BrinObj.ReceiverStored;
import com.brin.denonremotefree.BrinObj.ReceiverTools;
import com.brin.denonremotefree.BrinObj.ScanItem;
import com.brin.denonremotefree.BuildConfig;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.connection.Helper;
import com.brin.denonremotefree.db.url;
import com.brin.denonremotefree.service.ReceiverConnectionService;
import com.brin.denonremotefree.views.BrinViewFlipper;
import com.brin.denonremotefree.widgets.BrinToolbar;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by Luca on 08.05.2016.
 */
public class ReceiverScanActivity extends BrinActivity
{
    @Bind(R.id.brinViewPager) BrinViewFlipper vfMain;
    @Bind(R.id.srvReceiverScan) SuperRecyclerView srvMain;
    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinProgressBar) ProgressBar pbMain;
    @Bind(R.id.srRcrManu) Spinner srManu;
    @Bind(R.id.srRcrZones) Spinner srZones;
    @Bind(R.id.etRcrScanIp) EditText etIp;
    @Bind(R.id.ibRcrScanIpOk) ImageButton btIpGo;
    @Bind(R.id.ibRcrScanIpOk2) ImageButton ibIpGo2;
    @Bind(R.id.llRcrScanRoot) LinearLayout llRoot;
    @Bind(R.id.rlRcrScanKeyboard) RelativeLayout rlKeyBoard;
    @Bind(R.id.brinSwitch) Switch swAutoConn;
    @BindString(R.string.receiver_scan_title) String defTitle;

    private ReceiverStored receiverStored = null;
    private ArrayList<ScanItem> alReceiver = new ArrayList<>();
    private ArrayList<ScanItem> alScanResults = new ArrayList<>();
    private RecyclerView.Adapter rvAdapter;
    private String[] localIp = null;
    private boolean scanRunning = false;
    private Handler handler = new Handler();
    private int scanNum = 0;
    private String TAG = "RCR.SCAN ";
    private final int CHILD_WAIT = 0;
    private final int CHILD_LIST = 0;
    private final int CHILD_CONF = 1;
    private final int CHILD_WIFI_ERR = 2;
    private final int CHILD_ENTER_IP = 3;
    private final int CHILD_DONE = 4;

    @Override
    public void onCreate(Bundle b)
    {
        super.onCreate(b);
        setCrashReporter();
        setContentView(R.layout.activity_receiver_scan);
        ButterKnife.bind(this);

        setToolbar(tbMain);
        setTitle(defTitle);
        setNavBack(true);
        enableCustomProgress(pbMain);
        enableTitleMarquee(true);
        enableSubTitleMarquee(true);
        setProgressMax(254);
        enableAnalytics();

        srvMain.setAnimation(AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_top));
        RecyclerView.LayoutManager rvManager = new GridLayoutManager(this, 1);
        srvMain.setLayoutManager(rvManager);
        rvAdapter = new ReceiverScanAdapter();
        srvMain.setAdapter(rvAdapter);

        setListener();

        if (isConnected())
        {
            if (getLocalIp())
            {
                runScan();
            } else
            {
                setChild(CHILD_ENTER_IP);
            }
        }
        etIp.requestFocus();
    }

    public void onPos(View v)
    {
        if (receiverStored != null)
        {
            if (receiverStored.receiverHostName != null && receiverStored.receiverManufacturer != -1 && receiverStored.receiverIp != null)
            {
                closeActivityWithReceiver(receiverStored, swAutoConn.isChecked());
            } else
            {
                sendTrackMsg("RCR_SCAN_ERROR/" + ReceiverTools.ReceiverStoredToJson(receiverStored));
                Toast.makeText(getApplicationContext(),"ERROR! CAN NOT ADD RECEIVER",Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        } else
        {
            onBackPressed();
        }
    }

    public void onNeg(View v)
    {
        onBackPressed();
    }

    private void setListener()
    {
        srManu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                if (receiverStored != null)
                {
                    receiverStored.receiverManufacturer = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
            }

        });
        srZones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int zones, long id)
            {
                zones++;
                if (receiverStored != null)
                {
                    receiverStored.receiverZones = zones;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
            }

        });
    }

    boolean etPrepared = false;

    public void onIpOk(View v)
    {
        Log.d(TAG, "onIpOk: ");
        String ip = etIp.getText().toString();
        if (Helper.validIP(ip))
        {
            //startTelnetService(ip, getString(R.string.receiver_scan_ip_try_conct));
            retriedCount = 0;
            setProgress(true, 1);
            setSubTitle(getString(R.string.receiver_scan_ip_try_conct));
            ReceiverStored rc = new ReceiverStored();
            rc.receiverIp = ip;
            rc.receiverHostName = ip;
            startTelnetService(rc, true);
        }
    }

    private String ipBefore = "";

    private void prepareManEt()
    {
        Log.d(TAG, "prepareManEt: ");
        if (!etPrepared)
        {
            etIp.setOnEditorActionListener(new TextView.OnEditorActionListener()
            {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
                {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_SEARCH)
                    {
                        onIpOk(new View(getApplicationContext()));
                        handled = true;
                    }
                    return handled;
                }
            });
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter()
            {
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
                {
                    if (end > start)
                    {
                        String destTxt = dest.toString();
                        String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                        if (!resultingTxt.matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?"))
                        {
                            return ipBefore;
                        } else
                        {
                            String[] splits = resultingTxt.split("\\.");
                            for (String split : splits)
                            {
                                if (Integer.valueOf(split) > 255)
                                {
                                    return ipBefore;
                                }
                            }
                        }
                    }
                    return null;
                }
            };
            etIp.setFilters(filters);
            etIp.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after)
                {
                    ipBefore = s.toString();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    if (!ReceiverConnectionService.serviceConnecting)
                    {
                        boolean valid = Helper.validIP(etIp.getText().toString());
                        btIpGo.setEnabled(valid);
                        ibIpGo2.setEnabled(valid);
                        setSubTitle(null);
                    }
                }

                @Override
                public void afterTextChanged(Editable s)
                {
                }
            });
        }
        if (localIp != null)
            etIp.setText(MessageFormat.format("{0}.{1}.{2}.", localIp[0], localIp[1], localIp[2]));
        etIp.setSelection(etIp.getText().length());
        etPrepared = true;
    }

    private String scanLog = "";

    private synchronized void runScan()
    {
        showSnackBar(getString(R.string.msg_no_other_connected), 3000);
        if (!scanRunning)
        {
            onScanStart();

            new Thread(new Runnable()
            {
                public void run()
                {
                    for (int ip = 1; ip < 256; ip++)
                    {
                        final int finalIp = ip;

                        new Thread(new Runnable()
                        {
                            public void run()
                            {
                                if (!scanCancelled)
                                {
                                    final ScanItem item = ScanItemRequest(MessageFormat.format("{0}.{1}.{2}.{3}", localIp[0], localIp[1], localIp[2], finalIp));
                                    handler.post(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            onScanResult(item);
                                        }
                                    });
                                }
                            }
                        }).start();
                        try
                        {
                            Object obj = new Object();
                            synchronized (obj)
                            {
                                obj.wait(50);
                            }
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        switch (ip)
                        {
                            case 20:
                                ip = 99;
                                break;
                            case 120:
                                ip = 20;
                                break;
                            case 99:
                                ip = 120;
                                break;
                        }
                    }
                }
            }).start();
        }
    }


    private void onScanResult(ScanItem scanItem)
    {
        try
        {
            if (scanItem != null && !scanCancelled)
            {
                scanItem.itemGatewaymac = getMacFromRouter();
                alScanResults.add(scanItem);

                if (scanItem.itemHostName != null)
                {
                    //Log.d(TAG, "onScanResult: " + scanItem.itemMac);
                    if (scanItem.itemSuprt)
                    {
                        addListItem(scanItem);
                    }
                }
                if (alScanResults.size() >= 254)
                {
                    onScanDone();
                } else
                {
                    setProgress(false, alScanResults.size());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void onScanStart()
    {
        scanLog = "";
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                scanCancelled = false;
                getLocalIp();
                alReceiver.clear();
                alScanResults.clear();
                rvAdapter.notifyDataSetChanged();
                scanRunning = true;
                scanNum = 0;
                setProgress(true, 1);
                try
                {
                    setSubTitle(getString(R.string.receiver_scan_scanning));
                }
                catch (Exception e)
                {
                    sendTrackMsg("SCAN_ERR/" + e.getMessage());
                }
                setChild(CHILD_WAIT);
            }
        });
    }

    private void onScanDone()
    {
        Log.d(TAG, "onScanDone: LOG| " + scanLog);
        showEmptySnackBar();
        scanRunning = false;
        scanNum = 0;
        //progressBar.setVisibility(View.INVISIBLE);
        setProgress(true, 0);
        Log.d(TAG, "onScanDone: " + alReceiver.size());
        switch (alReceiver.size())
        {
            case 0:
                setSubTitle(String.valueOf(getString(R.string.scan_done_nothing)));
                setChild(CHILD_ENTER_IP);
                break;
            case 1:
                setSubTitle(String.valueOf(getString(R.string.scan_done_device)));
                break;
            default:
                setSubTitle(MessageFormat.format(getString(R.string.scan_done_devices), alReceiver.size()));
                break;
        }
    }

    private Snackbar snackbar;

    private void showEmptySnackBar()
    {
        if (!snackBarVisible)
        {
            snackbar = Snackbar.make(llRoot, getString(R.string.receiver_scan_not_list_quest), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.no), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    snackbar.dismiss();
                    setChild(CHILD_ENTER_IP);
                }
            });
            snackbar.setCallback(new Snackbar.Callback()
            {
                @Override
                public void onDismissed(Snackbar snackbar, int event)
                {
                    snackBarVisible = false;
                    super.onDismissed(snackbar, event);
                }

                @Override
                public void onShown(Snackbar snackbar)
                {
                    snackBarVisible = true;
                    super.onShown(snackbar);
                }
            });
            snackbar.show();
        }
    }

    private boolean getMacAndCheck(String ip)
    {
        String mac = getMacFromArpCache(ip);
        if (mac == null) return true;
        if (mac.equals("00:00:00:00:00:00")) return true;
        return checkMacAddress(mac);
    }

    private static String getMacFromArpCache(String ip)
    {
        if (ip == null) return null;
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && ip.equals(splitted[0]))
                {
                    // Basic sanity check
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:.."))
                    {
                        return mac;
                    } else
                    {
                        return null;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (br != null)
                {
                    br.close();
                }

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    private ScanItem ScanItemRequest(final String itemIp)
    {
        Log.d(TAG, "WEIKUG1: " + itemIp);
        String itemHostName = null;
        final String itemMac = null;
        boolean itemOnline = false;
        boolean itemSuprt = false;

        boolean exception1 = false;
        boolean exception2 = false;

        try
        {
            InetAddress inetAddress = InetAddress.getByName(itemIp);
            itemHostName = inetAddress.getCanonicalHostName();
            itemOnline = inetAddress.isReachable(5000);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            itemHostName = null;
            exception1 = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            exception2 = true;
            itemOnline = false;
        }

        if (!exception1 && !exception2)
        {
            try
            {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(itemIp, 23), 10000);
                if (socket.isConnected())
                {
                    itemSuprt = true;
                    socket.close();
                } else
                {
                    itemSuprt = false;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                itemSuprt = false;
            }
        }

        scanLog = MessageFormat.format("{0}\n{1}|{2}|{3}|{4}", scanLog, itemIp, itemHostName, itemOnline, itemSuprt);

        ScanItem scanItem = new ScanItem();
        scanItem.itemIp = itemIp;
        scanItem.itemHostName = itemHostName;
        scanItem.itemMac = itemMac;
        scanItem.itemOnline = itemOnline;
        scanItem.itemSuprt = itemSuprt;

        if (scanCancelled) return null;
        return scanItem;
    }

    public boolean isConnected()
    {
        Boolean connected = Helper.isConnected(this);
        if (!connected)
        {
            setProgress(false, 0);
            setSubTitle(getString(R.string.receiver_scan_no_wifi));
            setChild(CHILD_WIFI_ERR);
        }
        return connected;
    }

    private boolean scanCancelled = false;
    private boolean canGoBack = true;

    private void cancelScan()
    {
        canGoBack = false;
        Log.d(TAG, "cancelScan: ");
        scanCancelled = true;
        setProgress(false, 0);
        scanRunning = false;
        scanNum = 0;
    }

    private void onSelectSrvItem(int i)
    {
        receiverStored = null;
        String hostName = null;
        String ipAddress = null;
        String routerMac = null;
        boolean available;
        try
        {
            hostName = alReceiver.get(i).itemHostName;
            available = alReceiver.get(i).itemSuprt;
            ipAddress = alReceiver.get(i).itemIp;
            routerMac = alReceiver.get(i).itemGatewaymac;
        }
        catch (Exception e)
        {
            available = false;
        }
        if (available)
        {
            setChild(CHILD_CONF);
            setProgress(false, 0);
            setSubTitle(null);
            setTitle(hostName);
            receiverStored = new ReceiverStored();
            receiverStored.receiverHostName = hostName;
            receiverStored.receiverIp = ipAddress;
            receiverStored.routerMac = routerMac;
            if (hostName.toLowerCase().contains("denon")) srManu.setSelection(0);
            if (hostName.toLowerCase().contains("marantz")) srManu.setSelection(1);
        } else
        {
            Toast.makeText(this, getString(R.string.device_unrechable), Toast.LENGTH_LONG).show();
        }
    }

    private void addListItem(ScanItem r)
    {
        if (vfMain.getDisplayedChild() != CHILD_CONF || vfMain.getDisplayedChild() != CHILD_DONE)
            setChild(CHILD_LIST);

        alReceiver.add(r);
        Collections.sort(alReceiver, new Comparator<ScanItem>()
        {
            @Override
            public int compare(ScanItem lhs, ScanItem rhs)
            {
                return String.valueOf(rhs.itemSuprt).compareTo(String.valueOf(lhs.itemSuprt));
            }
        });
        rvAdapter.notifyItemInserted(alReceiver.size() - 1);
    }

    private String routerMac = null;

    public String getMacFromRouter()
    {
        try
        {
            if (routerMac == null)
            {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                routerMac = wifiInfo.getBSSID();
                return routerMac;
            } else
            {
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

    class ReceiverScanAdapter extends RecyclerView.Adapter<ReceiverScanAdapter.ViewHolderClass>
    {

        class ViewHolderClass extends RecyclerView.ViewHolder
        {
            private TextView tvItem1;
            private TextView tvItem2;
            private FrameLayout rlBlock;
            private FrameLayout flElement;

            ViewHolderClass(View itemView)
            {
                super(itemView);
                tvItem1 = (TextView) itemView.findViewById(R.id.brinText1);
                tvItem2 = (TextView) itemView.findViewById(R.id.brinText2);
                rlBlock = (FrameLayout) itemView.findViewById(R.id.flBlock);
                flElement = (FrameLayout) itemView;
            }
        }

        @Override
        public ViewHolderClass onCreateViewHolder(ViewGroup viewGroup, int i)
        {
            View itemView1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_receiver_scan, viewGroup, false);
            return new ViewHolderClass(itemView1);
        }

        @Override
        public void onBindViewHolder(final ViewHolderClass vhc, final int i)
        {
            try
            {
                vhc.tvItem1.setText(alReceiver.get(i).itemHostName);
                vhc.tvItem2.setText(alReceiver.get(i).itemIp);
                boolean available = alReceiver.get(i).itemSuprt;

                if (available)
                {
                    vhc.rlBlock.setVisibility(View.GONE);
                    vhc.flElement.setTag(i);
                    vhc.flElement.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            onSelectSrvItem(Integer.valueOf(v.getTag().toString()));
                        }
                    });
                } else
                {
                    vhc.rlBlock.setVisibility(View.VISIBLE);
                    vhc.rlBlock.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (!alReceiver.get(i).itemOnline)
                                Toast.makeText(getApplicationContext(), getString(R.string.device_offline), Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getApplicationContext(), getString(R.string.device_unrechable), Toast.LENGTH_LONG).show();
                        }
                    });
                }
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

    private boolean checkMacAddress(String m)
    {
        Log.d(TAG, "checkMacAddress: ");
        return m != null && (m.replaceAll(":", "").toLowerCase().startsWith("0005cd") || m.replaceAll(":", "").toLowerCase().startsWith("000678"));
    }

    private void closeActivityWithReceiver(ReceiverStored stored, boolean autoConn)
    {
        stored.storedTime = System.nanoTime();
        Intent i = new Intent();
        i.putExtra(ReceiverListActivity.INTENT_RECEIVER, ReceiverTools.ReceiverStoredToJson(stored));
        i.putExtra(ReceiverListActivity.INTENT_AUTO_CONN, autoConn);
        setResult(RESULT_OK, i);
        setChild(CHILD_DONE);
    }

    private void closeActivityWithResult(int RES)
    {
        setResult(RES);
        finish();
    }

    public void onFinish(View v)
    {
        closeActivityWithResult(RESULT_CANCELED);
    }

    @Override
    public void onBackPressed()
    {
        receiverStored = null;
        if (vfMain.getDisplayedChild() != CHILD_CONF)
        {
            closeActivityWithResult(RESULT_CANCELED);
        } else
        {
            if (canGoBack)
            {
                //setTitle(getString(R.string.mr_media_route_chooser_title));
                setChild(CHILD_LIST);
            } else
            {
                closeActivityWithResult(RESULT_CANCELED);
            }
        }
    }

    // REGION KEYBOARD

    public void onKey(View v)
    {
        Button b = (Button) v;
        //String t = MessageFormat.format("{0}{1}", etIp.getText(), b.getText());
        //etIp.setText();
        String c = b.getText().toString();
        etIp.setText(etIp.getText().toString() + c);
    }

    public void onIpBack(View v)
    {
        try
        {
            String t = etIp.getText().toString();
            etIp.setText(t.substring(0, t.length() - 1));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // END KEYBOARD

    private void setChild(int c)
    {
        //showKeyboard(c == CHILD_DIA_MAN);
        Log.d(TAG, "setChild: " + c);
        if (vfMain.setChild(c))
        {
            if (snackbar != null) snackbar.dismiss();

            if ((c != CHILD_WAIT || c != CHILD_LIST) && scanRunning) cancelScan();
        }
        setKeyboardVisible(true);
        switch (c)
        {
            case CHILD_DONE:
                try
                {
                    setNavBack(false);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    finish();
                }
                //setSubTitle(null);
                setProgress(true, 1);
                new CountDownTimer(2550, 10)
                {
                    public void onTick(long millisUntilFinished)
                    {
                        setProgress(false, 255 - ((int) millisUntilFinished / 10));
                    }

                    public void onFinish()
                    {
                        finish();
                    }
                }.start();
                break;
            case CHILD_CONF:
                scanCancelled = true;
                //setSubTitle(getString(R.string.receiver_scan_found_by_ip));
                break;
            case CHILD_ENTER_IP:
                sendTrackMsg("RCR_SCAN_LOG/" + scanLog);
                scanCancelled = true;
                prepareManEt();
                //tvIpDisc.setSelected(true);
                receiverStored = null;
                break;
            case CHILD_LIST:
                scanCancelled = false;
                receiverStored = null;
                break;
            /*
            case CHILD_WAIT:
                scanCancelled = false;
                break;
                */
            case CHILD_WIFI_ERR:
                cancelScan();
                break;
        }
    }

    private void setKeyboardVisible(boolean v)
    {
        if (v && vfMain.getDisplayedChild() == CHILD_ENTER_IP)
        {
            rlKeyBoard.setVisibility(View.VISIBLE);
        } else
        {
            rlKeyBoard.setVisibility(View.GONE);
        }
    }

    private boolean runInEmulator()
    {
        if (Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains("Android SDK built for x86") || Build.MANUFACTURER.contains("Genymotion") || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) || "google_sdk".equals(Build.PRODUCT))
            return true;
        SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (manager.getSensorList(Sensor.TYPE_ALL).isEmpty()) return true;
        if (android.os.Build.MODEL.equals("google_sdk")) return true;
        if (Formatter.formatIpAddress(((WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE)).getDhcpInfo().gateway).equals("10.0.3.2"))
            return true;
        Log.d(TAG, "runInEmulator: false");
        return false;
    }

    private boolean getLocalIp()
    {
        try
        {
            String ip = Formatter.formatIpAddress(((WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE)).getDhcpInfo().gateway);
            localIp = ip.split("\\.", -1);
            //Toast.makeText(this, Arrays.toString(localIp), Toast.LENGTH_LONG).show();
            if (runInEmulator())
            {
                if (!BuildConfig.DEBUG) return false;
                localIp = new String[]{"192", "168", "0", "0"};
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            localIp = null;
            return false;
        }
    }

    private boolean serviceStartedByThis = false;

    @Override
    public void onConnectionError(int i, String ip)
    {
        Log.d(TAG, "onConnectionError: " + i + "/" + ip);
        setProgress(false, 0);
        if (vfMain.getDisplayedChild() == CHILD_ENTER_IP)
        {
            switch (i)
            {
                case 0:
                    setSubTitle(getString(R.string.con_error_timeout));
                    break;
                case 2:
                    if (!VolleyRepairConnection(ip))
                    {
                        setSubTitle(getString(R.string.con_error_refused));
                        setProgress(false, 0);
                        showSnackBar(getString(R.string.msg_no_other_connected), 3000);
                        //retriedCount = 0;
                    }
                    break;
                case 4:
                    setSubTitle(getString(R.string.con_error_host));
                    break;
                default:
                    // SOCKET WAS CLOSED BY APP
                    setSubTitle(getString(R.string.con_error_host));
                    break;
            }
        } else
        {
            setSubTitle(null);
        }
        serviceStartedByThis = false;
        btIpGo.setEnabled(true);
        ibIpGo2.setEnabled(true);
    }

    private boolean snackBarVisible = false;

    private void showSnackBar(String t, int l)
    {
        if (!snackBarVisible)
        {
            Snackbar s = Snackbar.make(llRoot, t, l);
            View sView = s.getView();
            sView.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_primary_color));
            s.setCallback(new Snackbar.Callback()
            {
                @Override
                public void onDismissed(Snackbar snackbar, int event)
                {
                    snackBarVisible = false;
                    super.onDismissed(snackbar, event);
                }

                @Override
                public void onShown(Snackbar snackbar)
                {
                    snackBarVisible = true;
                    super.onShown(snackbar);
                }
            });
            snackBarVisible = true;
            s.show();
        }
    }

    private int retriedCount = 0;

    private boolean VolleyRepairConnection(final String i)
    {
        Log.d(TAG, "onConnectionError: " + retriedCount);
        if (retriedCount < 1)
        {
            try
            {
                //stopTelnetService();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            setSubTitle(getString(R.string.info_try_repair));
            setProgress(true, 1);
            retriedCount++;
            RequestQueue queue = Volley.newRequestQueue(this);
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + i + url.netStatus1, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String result)
                {
                    //updateStatus(getString(R.string.info_retry_connect));
                    setSubTitle(getString(R.string.info_retry_connect));
                    setProgress(true, 1);
                    ReceiverStored rc = new ReceiverStored();
                    rc.receiverIp = i;
                    rc.receiverHostName = i;
                    startTelnetService(rc, true);
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    setSubTitle(getString(R.string.con_error_refused));
                    setProgress(false, 0);
                }
            });
            queue.add(stringRequest);
            //retried = true;
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public void onConnecting()
    {
        super.onConnecting();
        setSubTitle(getString(R.string.contacting_device));
        setProgress(true, 1);
        btIpGo.setEnabled(false);
        ibIpGo2.setEnabled(false);
    }

    @Override
    public void onConnected(String hostName, String ipAddress)
    {
        //retried = false;
        receiverStored = null;

        setSubTitle(null);
        setTitle(hostName);
        receiverStored = new ReceiverStored();
        receiverStored.receiverHostName = hostName;
        receiverStored.receiverIp = ipAddress;
        receiverStored.routerMac = getMacFromRouter();
        if (hostName.toLowerCase().contains("denon")) srManu.setSelection(0);
        if (hostName.toLowerCase().contains("marantz")) srManu.setSelection(1);

        setChild(CHILD_CONF);

        setProgress(false, 0);

        stopService(new Intent(this, ReceiverConnectionService.class));
    }

    // endregion

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
