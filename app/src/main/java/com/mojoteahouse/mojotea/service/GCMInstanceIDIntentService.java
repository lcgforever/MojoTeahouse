package com.mojoteahouse.mojotea.service;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mojoteahouse.mojotea.R;

public class GCMInstanceIDIntentService extends FirebaseInstanceIdService {

    private static final String TAG = GCMInstanceIDIntentService.class.getName();

    @Override
    public void onTokenRefresh() {
        saveNewGCMToken();
    }

    private void saveNewGCMToken() {
        String gcmToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "GCM Registration Token: " + gcmToken);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(getString(R.string.pref_gcm_token), gcmToken).apply();
    }
}
