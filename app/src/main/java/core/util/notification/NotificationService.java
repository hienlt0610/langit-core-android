package core.util.notification;

import android.app.IntentService;
import android.content.Intent;

import core.base.BaseApplication;
import core.util.Utils;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("NotificationService");
    }

    public NotificationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null
                && intent.getExtras() != null
                && !Utils
                .isEmpty(intent
                        .getStringExtra(NotificationController.NOTIFICATION_SERVICE_CLASS_TARGET))) {
            boolean isCustom = intent.getBooleanExtra(
                    NotificationController.NOTIFICATION_CUSTOM, false);
            String target = intent
                    .getStringExtra(NotificationController.NOTIFICATION_SERVICE_CLASS_TARGET);
            int id = intent.getIntExtra(NotificationController.NOTIFICATION_ID,
                    0);
            String title = intent
                    .getStringExtra(NotificationController.NOTIFICATION_TITLE);
            String message = intent
                    .getStringExtra(NotificationController.NOTIFICATION_MESSAGE);
            String content = intent
                    .getStringExtra(NotificationController.NOTIFICATION_CUSTOM_CONTENT);
            intent.removeExtra(NotificationController.NOTIFICATION_ID);
            intent.removeExtra(NotificationController.NOTIFICATION_TITLE);
            intent.removeExtra(NotificationController.NOTIFICATION_MESSAGE);
            intent.removeExtra(NotificationController.NOTIFICATION_SERVICE_CLASS_TARGET);
            intent.removeExtra(NotificationController.NOTIFICATION_CUSTOM);
            intent.removeExtra(NotificationController.NOTIFICATION_CUSTOM_CONTENT);
            intent.setClassName(BaseApplication.getContext(), target);
            if (isCustom) {
                NotificationController.sendCustomNotification(id, content,
                        intent);
            } else {
                NotificationController.sendNotification(id, title, message,
                        intent);
            }

        }
    }

}
