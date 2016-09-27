package com.mojoteahouse.mojotea.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.fragment.AboutAppFragment;

public class AboutAppActivity extends AppCompatActivity implements AboutAppFragment.AboutAppClickListener {

    private Toolbar toolbar;

    public static void start(Context context, Bundle optionsBundle) {
        Intent intent = new Intent(context, AboutAppActivity.class);
        context.startActivity(intent, optionsBundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_app);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.about_app_content_frame, AboutAppFragment.newInstance())
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }

    @Override
    public void onCopyrightClicked() {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, toolbar, getString(R.string.toolbar_transition));
        CopyrightActivity.start(this, optionsCompat.toBundle());
    }
}
