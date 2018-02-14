package com.brin.denonremotefree.activation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.brin.denonremotefree.Helper.Prefs;
import com.brin.denonremotefree.Helper.SetView;
import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.BrinObj.GooglePlayProduct;
import com.brin.denonremotefree.R;
import com.brin.denonremotefree.widgets.BrinToolbar;

import java.text.MessageFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BuyKeyActivity extends BrinActivity implements BillingProcessor.IBillingHandler
{

    private static final String TAG = "BUY.KEY.ACT";
    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinFrameLayout1) FrameLayout flBuy;
    @Bind(R.id.brinLinearLayout1) LinearLayout llBenefits;
    @Bind(R.id.brinLinearLayout2) LinearLayout llDonations;
    @Bind(R.id.brinText1) TextView tvBuy;
    @Bind(R.id.brinCollapsingToolbar) CollapsingToolbarLayout ctMain;
    @Bind(R.id.brinScrollView) NestedScrollView svMain;
    @Bind(R.id.brinAppBarLayout) AppBarLayout blMain;
    @Bind(R.id.brinText3) TextView tvActStatus;
    @Bind(R.id.brinIcon) ImageView ivActStatus;
    @Bind(R.id.brinProgressBar) ProgressBar pbActStatus;
    @Bind(R.id.brinCardView1) CardView cvStatus;
    @Bind(R.id.brinCardView2) CardView cvDonation;
    @Bind(R.id.brinCardView3) CardView cvBenefits;
    @Bind(R.id.brinCardView4) CardView cvSuccess;

    private Animation animSlideBottomIn;
    private BillingProcessor bp;
    public static final String prodID = "activate";
    private GooglePlayProduct gpKey;
    private Prefs prefs;
    private boolean enableScroll = true;

    @Override
    public boolean broadcastEnabled()
    {
        return false;
    }

    private CountDownTimer ctTimeOut = null;

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(R.layout.activity_buy_key);
        enableAnalytics();
        ButterKnife.bind(this);
        setToolbar(tbMain);
        setTitle(getString(R.string.title_buy_key));
        setNavBack(true);
        prefs = new Prefs(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window w = getWindow(); // in Activity's onCreate() for instance
            //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        animSlideBottomIn = AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_bottom);
        flBuy.setVisibility(View.GONE);
        cvDonation.setVisibility(View.GONE);
        cvSuccess.setVisibility(View.GONE);
        cvBenefits.setVisibility(View.GONE);

        svMain.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                return !enableScroll;
            }
        });

        updateActStatus(true, getString(R.string.check_status), false);

        if (!prefs.isAppActivated())
            cvBenefits.setVisibility(View.VISIBLE);
/*
        final int goooglePlayStatus = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        switch (goooglePlayStatus)
        {
            case ConnectionResult.SUCCESS:
                bp = new BillingProcessor(this, getString(R.string.dev_key), this);
                ctTimeOut = new CountDownTimer(15000, 900)
                {

                    public void onTick(long millisUntilFinished)
                    {
                    }

                    public void onFinish()
                    {
                        updateActStatus(false, getString(R.string.error_gplay_timeout));
                    }
                }.start();
                updateActStatus(true, getString(R.string.connect_gplay));
                break;
            default:
                updateActStatus(false, GoogleApiAvailability.getInstance().getErrorString(goooglePlayStatus));
                break;
        }
        */
        if (BillingProcessor.isIabServiceAvailable(this))
        {
            bp = new BillingProcessor(this, getString(R.string.dev_key), this);
            ctTimeOut = new CountDownTimer(15000, 900)
            {

                public void onTick(long millisUntilFinished)
                {
                }

                public void onFinish()
                {
                    updateActStatus(false, getString(R.string.error_gplay_timeout), true);
                }
            }.start();
            updateActStatus(true, getString(R.string.connect_gplay), false);
        } else
        {
            updateActStatus(false, getString(R.string.gplay_not_available), true);
        }
    }

    private void updateActStatus(final boolean wait, final String msg, final boolean error)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                cvStatus.setVisibility(View.VISIBLE);
                //cvStatus.setVisibility(View.VISIBLE);
                tvActStatus.setText(msg);
                pbActStatus.setVisibility(wait ? View.VISIBLE : View.GONE);
                ivActStatus.setVisibility(wait ? View.GONE : View.VISIBLE);
                ivActStatus.setImageResource(error ? R.drawable.error_100_white : R.drawable.approval_100_white);
            }
        });
    }

    public static boolean isKeyInstalled(Context c)
    {
        return appInstalledOrNot(c, "com.brin.denonremote");
    }

    private static boolean appInstalledOrNot(Context c, String uri)
    {
        PackageManager pm = c.getPackageManager();
        boolean app_installed;
        try
        {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e)
        {
            app_installed = false;
        }
        return app_installed;
    }

    public void onBuy(View v)
    {
        buyItem(prodID);
    }

    private int displayedChild = 0;

    private void setChild(int c)
    {
        try
        {
            if (displayedChild == c) return;
            displayedChild = c;

            svMain.smoothScrollTo(0, 0);
            switch (c)
            {
                case 0:
                    enableScroll = true;
                    if (!gpKey.bought) cvBenefits.setVisibility(View.VISIBLE);
                    if (!badgeError) cvDonation.setVisibility(View.VISIBLE);
                    cvStatus.setVisibility(View.GONE);
                    break;
                case 1:
                    blMain.setExpanded(false);
                    enableScroll = false;
                    flBuy.setVisibility(View.GONE);
                    cvBenefits.setVisibility(View.GONE);
                    cvDonation.setVisibility(View.GONE);
                    cvSuccess.setVisibility(View.GONE);
                    //flBuy.setVisibility(View.GONE);
                    //cvBenefits.setVisibility(View.GONE);
                    //cvDonation.setVisibility(View.GONE);
                    break;
                case 2:

                    break;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details)
    {
        try
        {
            Log.d(TAG, "onProductPurchased: ");
            if (ctTimeOut != null)
                ctTimeOut.cancel();
            prefs.activateApp2(true);
            cvStatus.setVisibility(View.GONE);
            setChild(0);
            if (productId.startsWith("badge"))
            {
                final int boughtBadge = Integer.valueOf(productId.substring(5, 6));
                FrameLayout flItem = (FrameLayout) llDonations.getChildAt(boughtBadge - 1);
                flItem.setOnClickListener(null);
                cvSuccess.setVisibility(View.VISIBLE);
                cvDonation.setVisibility(View.VISIBLE);
                final ImageView ivCheck = (ImageView) flItem.findViewById(R.id.brinIcon2);
                ivCheck.setVisibility(View.GONE);
                new CountDownTimer(2000, 900)
                {
                    public void onTick(long millisUntilFinished)
                    {
                    }

                    public void onFinish()
                    {
                        ivCheck.setImageResource(R.drawable.check_100_white);
                        SetView.enterReveal(ivCheck);
                    }
                }.start();
            } else
            {
                cvSuccess.setVisibility(View.VISIBLE);
                cvDonation.setVisibility(View.VISIBLE);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            sendTrackMsg("BUY_ERROR/"+e.getMessage());
        }
    }

    @Override
    public void onPurchaseHistoryRestored()
    {
        Log.d(TAG, "onPurchaseHistoryRestored: ");
        if (ctTimeOut != null)
            ctTimeOut.cancel();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error)
    {
        Log.d(TAG, "onBillingError: ");
        if (ctTimeOut != null)
            ctTimeOut.cancel();
        try
        {
            updateActStatus(false, getString(R.string.error_billing) + " (" + error.getMessage() + ")", true);
        } catch (Exception e)
        {
            e.printStackTrace();
            sendTrackMsg("BUY_ERROR/"+e.getMessage());
            updateActStatus(false, getString(R.string.error_billing), true);
        }
        setChild(2);
    }

    private boolean badgeError = false;

    @Override
    public void onBillingInitialized()
    {
        Log.d(TAG, "onBillingInitialized: ");
        if (ctTimeOut != null)
            ctTimeOut.cancel();

        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    gpKey = new GooglePlayProduct(
                            bp.getPurchaseListingDetails(prodID).title,
                            bp.isPurchased(prodID),
                            bp.getPurchaseListingDetails(prodID).priceText,
                            prodID);

                    handler.post(new Runnable()
                    {
                        public void run()
                        {
                            if (gpKey.bought)
                            {
                                flBuy.setVisibility(View.GONE);
                                updateActStatus(false, getString(R.string.msg_key_already_bought), false);
                            } else
                            {
                                cvStatus.setVisibility(View.GONE);
                                tvBuy.setText(MessageFormat.format("{0} {1} {2}", getString(R.string.msg_buy_the_key), getString(R.string.only), gpKey.price));
                                flBuy.setVisibility(View.VISIBLE);
                                flBuy.startAnimation(animSlideBottomIn);
                            }
                        }
                    });
                } catch (Exception e)
                {
                    e.printStackTrace();
                    sendTrackMsg("BUY_ERROR/"+e.getMessage());
                }

                try
                {
                    for (int i = 1; i <= 9; i++)
                    {
                        final String itemId = "badge" + i;
                        Log.d(TAG, "onBillingInitialized: " + itemId);
                        final GooglePlayProduct gp = new GooglePlayProduct(
                                badgeIdToTitle(i),
                                bp.isPurchased(itemId),
                                bp.getPurchaseListingDetails(itemId).priceText,
                                itemId);

                        handler.post(new Runnable()
                        {
                            public void run()
                            {
                                try
                                {
                                    if (!badgeError)
                                        addBadge(gp);
                                } catch (Exception e)
                                {
                                    badgeError = true;
                                    e.printStackTrace();
                                }
                            }
                        });
                        if (badgeError)
                            break;
                        if (i == 1)
                        {
                            handler.post(new Runnable()
                            {
                                public void run()
                                {
                                    cvDonation.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    sendTrackMsg("BUY_ERROR/"+e.getMessage());
                }
            }
        }).start();
    }

    private String badgeIdToTitle(int i)
    {
        switch (i)
        {
            case 1:
                return getString(R.string.badge1);
            case 2:
                return getString(R.string.badge2);
            case 3:
                return getString(R.string.badge3);
            case 4:
                return getString(R.string.badge4);
            case 5:
                return getString(R.string.badge5);
            case 6:
                return getString(R.string.badge6);
            case 7:
                return getString(R.string.badge7);
            case 8:
                return getString(R.string.badge8);
            case 9:
                return getString(R.string.badge9);
            default:
                return null;
        }
    }

    @Override
    public void onBackPressed()
    {
        Log.d(TAG, "onBackPressed: " + displayedChild);
        switch (displayedChild)
        {
            case 1:
                break;
            case 2:
                setChild(0);
                break;
            default:
                super.onBackPressed();
                break;
        }
    }

    private Handler handler = new Handler();

    @Override
    protected void onDestroy()
    {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    private void addBadge(GooglePlayProduct gpItem) throws Exception
    {
        Log.d(TAG, "addBadge: " + gpItem.title);
        int icon = getResources().getIdentifier(gpItem.id + "_100_white", "drawable", getPackageName());
        FrameLayout flItem = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.item_buy_badge, llDonations, false);
        ImageView ivIcon = (ImageView) flItem.findViewById(R.id.brinIcon);
        ImageView ivCheck = (ImageView) flItem.findViewById(R.id.brinIcon2);
        TextView tv1 = (TextView) flItem.findViewById(R.id.brinText1);
        TextView tv2 = (TextView) flItem.findViewById(R.id.brinText2);
        tv2.setText(MessageFormat.format("{0} {1}", getString(R.string.for_a_donation_of), gpItem.price));
        ivIcon.setImageResource(icon);
        tv1.setText(gpItem.title);
        ivCheck.setImageResource(gpItem.bought ? R.drawable.check_100_white : R.drawable.right_100_white);
        llDonations.addView(flItem);
        if (!gpItem.bought)
        {
            flItem.setTag(gpItem.id);
            flItem.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    buyItem(view.getTag().toString());
                }
            });
        } else
        {
            flItem.setClickable(false);
        }
    }

    private void buyItem(String id)
    {
        try
        {
            Log.d(TAG, "buyItem: " + id);
            setChild(1);
            updateActStatus(true, getString(R.string.please_wait___), false);
            bp.purchase(this, id);
        } catch (Exception e)
        {
            e.printStackTrace();
            sendTrackMsg("BUY_ERROR/"+e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onBrinActivityResult(requestCode, resultCode, data);
    }
}
