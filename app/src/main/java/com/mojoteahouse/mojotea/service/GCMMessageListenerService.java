package com.mojoteahouse.mojotea.service;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mojoteahouse.mojotea.R;

import java.util.Map;

public class GCMMessageListenerService extends FirebaseMessagingService {

    private static final String TAG = GCMMessageListenerService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "GCM Message received");

        Map<String, String> dataMap = remoteMessage.getData();
        if (dataMap != null) {
            Bundle data = new Bundle();
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                data.putString(entry.getKey(), entry.getValue());
            }
            Intent broadcastIntent = new Intent(getString(R.string.mojo_message_intent_filter));
            broadcastIntent.putExtras(data);
            sendBroadcast(broadcastIntent, null);
        }
    }
}
