package core.util.gcm;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

import core.base.BaseActivity;
import core.util.notification.NotificationController;


@SuppressWarnings("WeakerAccess")
public class GcmService extends GcmListenerService {

    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(String from, Bundle extras) {
        if (extras != null) {
            sendNotification(GcmMessage.parse(extras.getString("data")));
        }
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(GcmMessage msg) {
        Intent intent = new Intent(this, BaseActivity.class);
        NotificationController.sendNotification(NOTIFICATION_ID, "GCM",
                "GCM Message: " + msg.toString(), intent);
    }
}
