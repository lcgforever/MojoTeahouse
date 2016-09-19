package com.mojoteahouse.mojotea.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;

import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.activity.AboutAppActivity;

public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceClickListener {

    public static SettingsFragment newInstance() {
        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setRetainInstance(true);
        return settingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findPreference(getString(R.string.key_about_app))
                .setOnPreferenceClickListener(this);

        findPreference(getString(R.string.key_about_mojotea))
                .setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        if (key.equals(getString(R.string.key_about_app))) {
            AboutAppActivity.start(getActivity());
            return true;
        } else if (key.equals(getString(R.string.key_about_mojotea))) {
            return true;
        }

        return false;
    }
}
