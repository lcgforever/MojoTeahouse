package com.mojoteahouse.mojotea.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mojoteahouse.mojotea.R;

public class AboutMojoActivity extends AppCompatActivity implements View.OnClickListener {

    private CoordinatorLayout coordinatorContainer;

    public static void start(Context context, Bundle optionsBundle) {
        Intent intent = new Intent(context, AboutMojoActivity.class);
        context.startActivity(intent, optionsBundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_mojo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        coordinatorContainer = (CoordinatorLayout) findViewById(R.id.coordinator_container);
        View phoneView = findViewById(R.id.about_mojo_phone_container);
        View emailView = findViewById(R.id.about_mojo_email_container);

        phoneView.setOnClickListener(this);
        emailView.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_mojo_phone_container:
                copyPhoneNumber();
                break;

            case R.id.about_mojo_email_container:
                sendEmail();
                break;
        }
    }

    private void copyPhoneNumber() {
        ClipData clipData = ClipData.newPlainText(
                getString(R.string.about_mojo_activity_phone_number_text),
                getString(R.string.mojo_phone_number));
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clipData);
        Snackbar.make(coordinatorContainer, R.string.about_mojo_activity_copy_snackbar_message, Snackbar.LENGTH_SHORT).show();
    }

    private void sendEmail() {
        String[] toEmails = {getString(R.string.mojo_email_address)};
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, toEmails);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.about_mojo_activity_send_email_title)));
    }
}
