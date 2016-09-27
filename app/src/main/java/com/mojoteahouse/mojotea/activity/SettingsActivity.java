package com.mojoteahouse.mojotea.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.fragment.SettingsFragment;
import com.mojoteahouse.mojotea.fragment.dialog.ConfirmSignOutDialogFragment;

public class SettingsActivity extends AppCompatActivity implements SettingsFragment.SettingsClickListener,
        ConfirmSignOutDialogFragment.SignOutListener {

    private Toolbar toolbar;

    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_content_frame, SettingsFragment.newInstance())
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }

    @Override
    public void onAboutAppClicked() {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, toolbar, getString(R.string.toolbar_transition));
        AboutAppActivity.start(this, optionsCompat.toBundle());
    }

    @Override
    public void onAboutMojoClicked() {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, toolbar, getString(R.string.toolbar_transition));
        AboutMojoActivity.start(this, optionsCompat.toBundle());
    }

    @Override
    public void onSignOutClicked() {
        ConfirmSignOutDialogFragment.show(getFragmentManager());
    }

    @Override
    public void onSignOutConfirmed() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit()
                .remove(getString(R.string.pref_signed_in))
                .remove(getString(R.string.pref_user_id))
                .remove(getString(R.string.pref_user_email))
                .apply();
        SignInActivity.start(this);
    }
}
