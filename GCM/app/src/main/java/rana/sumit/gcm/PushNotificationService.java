package rana.sumit.gcm;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

public class PushNotificationService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        //createNotification(mTitle, push_msg);
    }
}
