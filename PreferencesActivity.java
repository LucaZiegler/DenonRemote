package com.brin.denonremotefree;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.FrameLayout;

import com.brin.denonremotefree.Helper.Prefs;
import com.brin.denonremotefree.BrinObj.BrinActivity;
import com.brin.denonremotefree.widgets.BrinToolbar;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PreferencesActivity extends BrinActivity
{

    @Bind(R.id.brinToolbar) BrinToolbar tbMain;
    @Bind(R.id.brinFrameLayout1) FrameLayout flPrefs;
    @Bind(R.id.brinFrameLayout2) FrameLayout flBlock;

    @Override
    public boolean broadcastEnabled()
    {
        return false;
    }

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setCrashReporter();
        setContentView(R.layout.activity_preferences);
        ButterKnife.bind(this);

        setToolbar(tbMain);
        setTitle(getString(R.string.app_settings));
        setNavBack(true);

        PrefsFragment details = new PrefsFragment();
        getFragmentManager().beginTransaction().add(flPrefs.getId(), details).commit();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            flBlock.setVisibility(View.GONE);
        }
    }

    public static class PrefsFragment extends PreferenceFragment
    {
        private Prefs prefs;

        @Override
        public void onCreate(Bundle b)
        {
            super.onCreate(b);
            addPreferencesFromResource(R.xml.app_prefs_frag);

            prefs = new Prefs(getActivity());

            if (prefs.isAppActivated())
            {
                Preference p = findPreference("switch_preference_4_1");
                p.setEnabled(true);
                p.setSummary(null);
            }
        }

        /*
        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
        {
            Boolean b = getPreferenceManager().getSharedPreferences().getBoolean(preference.getKey(), false);
            prefs.putGlobBool(preference.getKey(), b);
            try
            {
                switch (preference.getKey())
                {
                    case "switch_preference_4_0":
                        ReceiverConnectionService.prefNotConEnabled = b;
                        if (!b)
                            ReceiverConnectionService.showNotifyConnection(false, getActivity().getApplicationContext());
                        else if (ReceiverConnectionService.socketConnected)
                            ReceiverConnectionService.showNotifyConnection(true,getActivity().getApplicationContext());
                        break;
                    case "switch_preference_4_1":
                        ReceiverConnectionService.prefNotPlayEnabled = b;
                        if (!b)
                            ReceiverConnectionService.showNotifyPlayer(null, getActivity().getApplicationContext());
                        else
                            ReceiverConnectionService.sendCom("NSE");
                        break;
                    case "switch_preference_1_0":
                        DashboardActivity.prefVolButtons = b;
                        break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        */
    }
}
