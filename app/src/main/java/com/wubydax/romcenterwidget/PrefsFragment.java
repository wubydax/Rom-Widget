package com.wubydax.romcenterwidget;


import android.appwidget.AppWidgetManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;

import com.wubydax.romcenterwidget.prefs.IntentDialogPreference;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private IntentDialogPreference[] appPrefsArray;
    private SharedPreferences sp;


    public PrefsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        int widgetId = args.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
        getPreferenceManager().setSharedPreferencesName("widget_prefs" + String.valueOf(widgetId));
        sp = getPreferenceManager().getSharedPreferences();
        addPreferencesFromResource(R.xml.widget_prefs);
        appPrefsArray = new IntentDialogPreference[5];
        for (int i = 0; i < appPrefsArray.length; i++) {
            appPrefsArray[i] = (IntentDialogPreference) findPreference("app" + String.valueOf(i + 1));
        }
        setPreferencesForApps();

        sp.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }


    private void setPreferencesForApps() {
        int numApps = Integer.parseInt(sp.getString("widget_size", "4"));
        PreferenceCategory appCategory = (PreferenceCategory) findPreference("pref_category");

        int appPreferenceCount = appCategory.getPreferenceCount();
        if (appPreferenceCount > numApps) {
            for (int i = numApps; i < appPreferenceCount; i++) {
                IntentDialogPreference current = appPrefsArray[i];
                appCategory.removePreference(current);

            }

        } else if (appPreferenceCount < numApps) {
            for (int j = appPreferenceCount - 1; j < numApps; j++) {
                IntentDialogPreference current = appPrefsArray[j];
                appCategory.addPreference(current);


            }
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("widget_size")) {
            setPreferencesForApps();
        }

    }
}
