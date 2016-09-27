package com.mojoteahouse.mojotea;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.facebook.FacebookSdk;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MojoTeaApp extends Application {

    public static final int EDIT_MOJO_MENU_REQUEST_CODE = 1000;
    public static final int EDIT_CART_ITEM_REQUEST_CODE = 1001;
    public static final int SHOW_CART_REQUEST_CODE = 1003;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FacebookSdk.sdkInitialize(this);
        String gcmToken = FirebaseInstanceId.getInstance().getToken();
        if (!TextUtils.isEmpty(gcmToken)) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putString(getString(R.string.pref_gcm_token), gcmToken).apply();
        }
    }
}
