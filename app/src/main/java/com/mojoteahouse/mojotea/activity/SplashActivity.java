package com.mojoteahouse.mojotea.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.mojoteahouse.mojotea.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY_MILLIS = 1500;

    private ImageView logoImageView;
    private Runnable navigateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logoImageView = (ImageView) findViewById(R.id.splash_logo_image_view);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean signedIn = sharedPreferences.getBoolean(getString(R.string.pref_signed_in), false);
        if (signedIn) {
            navigateRunnable = new Runnable() {
                @Override
                public void run() {
                    MojoMenuActivity.start(SplashActivity.this);
                    finish();
                }
            };
        } else {
            navigateRunnable = new Runnable() {
                @Override
                public void run() {
                    SignInActivity.start(SplashActivity.this);
                    finish();
                }
            };
        }
        logoImageView.postDelayed(navigateRunnable, SPLASH_SCREEN_DELAY_MILLIS);
    }

    @Override
    protected void onDestroy() {
        if (navigateRunnable != null) {
            logoImageView.removeCallbacks(navigateRunnable);
        }
        super.onDestroy();
    }
}
