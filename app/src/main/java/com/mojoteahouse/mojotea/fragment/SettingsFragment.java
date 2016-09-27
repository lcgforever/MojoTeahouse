package com.mojoteahouse.mojotea.fragment;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;

import com.mojoteahouse.mojotea.R;

public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceClickListener {

    private SettingsClickListener settingsClickListener;

    public interface SettingsClickListener {

        void onAboutAppClicked();

        void onAboutMojoClicked();

        void onSignOutClicked();
    }

    public static SettingsFragment newInstance() {
        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setRetainInstance(true);
        return settingsFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            settingsClickListener = (SettingsClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent activity must implement " + SettingsClickListener.class.getName());
        }
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

        findPreference(getString(R.string.key_sign_out))
                .setOnPreferenceClickListener(this);
    }

    @Override
    public void onDetach() {
        settingsClickListener = null;
        super.onDetach();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();

        if (key.equals(getString(R.string.key_about_app))) {
            settingsClickListener.onAboutAppClicked();
            return true;
        } else if (key.equals(getString(R.string.key_about_mojotea))) {
            settingsClickListener.onAboutMojoClicked();
            return true;
        } else if (key.equals(getString(R.string.key_sign_out))) {
            settingsClickListener.onSignOutClicked();
            return true;
        }

        return false;
    }
}
