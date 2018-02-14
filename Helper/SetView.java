package com.brin.denonremotefree.Helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Luca on 14.06.2016.
 */
public class SetView
{
    private static final String TAG = "SET.VIEW";

    public static void hideKeyboard(Activity c)
    {
        View view = c.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void selectTabLayoutItem(TabLayout tl, int i)
    {
        try
        {
            if (tl.getVisibility() != View.VISIBLE)
                tl.setVisibility(View.VISIBLE);
            tl.getTabAt(i).select();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void addTabLayoutItem(TabLayout tl,String t)
    {
        tl.addTab(tl.newTab().setText(t));
    }

    public static float convertPixelsToDp(float px, Context context)
    {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static float convertDpToPixel(float dp, Context context)
    {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static void enterReveal(View myView)
    {
        if (myView.getVisibility() == View.VISIBLE)
            return;
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;
        Animator anim;
        myView.setVisibility(View.VISIBLE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
        {
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
            anim.start();
        }
    }

    public static void exitReveal(final View myView)
    {
        if (myView.getVisibility() != View.VISIBLE)
            return;
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;
        int initialRadius = myView.getWidth() / 2;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
        {
            Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);
            anim.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.GONE);
                }
            });
            anim.start();
        } else
        {
            myView.setVisibility(View.GONE);
        }
    }

    public static boolean setText(TextView tv, String text, Animation anim)
    {
        Log.d(TAG, MessageFormat.format("setText: {0};{1};{2}", tv == null, text, anim == null));
        if (tv != null)
        {
            if (text == null)
            {
                tv.setText(null);
                return false;
            }
            text = text.trim();
            if (!tv.getText().toString().equals(text))
            {
                tv.setText(text);
                if (anim != null) tv.startAnimation(anim);

                if (text.length() > 0)
                    tv.setVisibility(View.VISIBLE);
                else
                    tv.setVisibility(View.GONE);
                tv.setSelected(true);
                return true;
            }
        }
        return false;
    }

    public static void setProgress(SeekBar sb, int v)
    {
        if (sb != null)
        {
            Log.d(TAG, "setProgress: " + v);
            if (!sb.isPressed()) sb.setProgress(v);
        }
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static int generateViewId()
    {
        for (; ; )
        {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue))
            {
                Log.d(TAG, "generateViewId: " + result);
                return result;
            }
        }
    }
}
