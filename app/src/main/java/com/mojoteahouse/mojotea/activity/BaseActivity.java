package com.mojoteahouse.mojotea.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mojoteahouse.mojotea.R;

public class BaseActivity extends AppCompatActivity {

    protected boolean storeClosed;
    private MojoMessageBroadcastReceiver mojoMessageBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mojoMessageBroadcastReceiver = new MojoMessageBroadcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mojoMessageBroadcastReceiver, new IntentFilter(getString(R.string.mojo_message_intent_filter)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mojoMessageBroadcastReceiver != null) {
            unregisterReceiver(mojoMessageBroadcastReceiver);
        }
    }

    private class MojoMessageBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
