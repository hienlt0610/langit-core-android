package core.util.notification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.commonframe.R;

import core.base.BaseApplication;
import core.util.Constant;


@SuppressWarnings({"WeakerAccess", "unused"})
public class NotificationController {

    public static final String NOTIFICATION_CUSTOM_OK_TAG = "Notification_Custom_OK_";
    public static final String NOTIFICATION_CUSTOM_CANCEL_TAG = "Notification_Custom_Cancel_";
    public static final String NOTIFICATION_CUSTOM_ACTION = "Action";
    public static final String NOTIFICATION_CUSTOM_ACTION_OK = "OK";
    public static final String NOTIFICATION_CUSTOM_ACTION_CANCEL = "Cancel";
    public static final String NOTIFICATION_CUSTOM_CONTENT = "Content";
    public static final String NOTIFICATION_SERVICE_TAG = "Notification_Service_";
    public static final String NOTIFICATION_TAG = "Notification_";
    public static final String NOTIFICATION_SERVICE_CLASS_TARGET = "Notification_Class_Target";
    public static final String NOTIFICATION_TITLE = "Notification_Title";
    public static final String NOTIFICATION_MESSAGE = "Notification_Message";
    public static final String NOTIFICATION_ID = "Notification_Id";
    public static final String NOTIFICATION_CUSTOM = "Notification_Custom";

    public static void sendDelayedNotification(int id, String title,
                                               String message, long when, Intent src, Class<?> target) {

        AlarmManager am = (AlarmManager) BaseApplication.getContext()
                .getSystemService(Activity.ALARM_SERVICE);
        if (am != null) {
            Intent intent = new Intent(BaseApplication.getContext(),
                    NotificationService.class);
            intent.putExtra(Constant.NOTIFICATION_DEFINED, true);
            intent.putExtra(NOTIFICATION_SERVICE_CLASS_TARGET, target.getName());
            intent.putExtra(NOTIFICATION_TITLE, title);
            intent.putExtra(NOTIFICATION_MESSAGE, message);
            intent.putExtra(NOTIFICATION_ID, id);
            intent.putExtra(NOTIFICATION_CUSTOM, false);
            intent.setAction(NOTIFICATION_SERVICE_TAG + id);
            intent.putExtras(src);
            PendingIntent sender = PendingIntent.getService(
                    BaseApplication.getContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            am.set(AlarmManager.RTC_WAKEUP, when, sender);
        }
    }

    public static void sendDelayedCustomNotification(int id, String content,
                                                     long when, Intent src, Class<?> target) {
        AlarmManager am = (AlarmManager) BaseApplication.getContext()
                .getSystemService(Activity.ALARM_SERVICE);
        if (am != null) {
            Intent intent = new Intent(BaseApplication.getContext(),
                    NotificationService.class);
            intent.putExtra(Constant.NOTIFICATION_DEFINED, true);
            intent.putExtra(NOTIFICATION_SERVICE_CLASS_TARGET, target.getName());
            intent.putExtra(NOTIFICATION_CUSTOM_CONTENT, content);
            intent.putExtra(NOTIFICATION_ID, id);
            intent.putExtra(NOTIFICATION_CUSTOM, true);
            intent.setAction(NOTIFICATION_SERVICE_TAG + id);
            intent.putExtras(src);
            PendingIntent sender = PendingIntent.getService(
                    BaseApplication.getContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            am.set(AlarmManager.RTC_WAKEUP, when, sender);
        }
    }

    public static void sendNotification(int id, String title, String message,
                                        Intent intent) {
        NotificationManager manager = (NotificationManager) BaseApplication
                .getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setAction(NOTIFICATION_TAG + id);
            intent.putExtra(Constant.NOTIFICATION_DEFINED, true);
            PendingIntent contentIntent = PendingIntent.getActivity(
                    BaseApplication.getContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Uri alarmSound = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    BaseApplication.getContext())
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setContentTitle(title)
                    .setStyle(
                            new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setDefaults(
                            Notification.DEFAULT_LIGHTS
                                    | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(contentIntent);

            manager.notify(id, mBuilder.build());
        }
    }

    public static void sendCustomNotification(int id, String content,
                                              Intent intent) {
        NotificationManager manager = (NotificationManager) BaseApplication
                .getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {

            Intent intentOK = new Intent(intent);
            intentOK.putExtra(NOTIFICATION_CUSTOM_ACTION,
                    NOTIFICATION_CUSTOM_ACTION_OK);
            intentOK.putExtra(Constant.NOTIFICATION_DEFINED, true);
            intentOK.putExtra(NOTIFICATION_ID, id);
            intentOK.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentOK.setAction(NOTIFICATION_CUSTOM_OK_TAG + id);
            PendingIntent pendingIntentOK = PendingIntent.getActivity(
                    BaseApplication.getContext(), 0, intentOK,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Intent intentCancel = new Intent(intent);
            intentCancel.putExtra(NOTIFICATION_CUSTOM_ACTION,
                    NOTIFICATION_CUSTOM_ACTION_CANCEL);
            intentCancel.putExtra(Constant.NOTIFICATION_DEFINED, true);
            intentCancel.putExtra(NOTIFICATION_ID, id);
            intentCancel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentCancel.setAction(NOTIFICATION_CUSTOM_CANCEL_TAG + id);

            PendingIntent pendingIntentCancel = PendingIntent.getActivity(
                    BaseApplication.getContext(), 0, intentCancel,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews remoteViews = new RemoteViews(BaseApplication
                    .getContext().getPackageName(), R.layout.notification);
            remoteViews.setTextViewText(R.id.notification_tv_content,
                    content);
            remoteViews.setOnClickPendingIntent(R.id.notification_bt_ok,
                    pendingIntentOK);
            remoteViews.setOnClickPendingIntent(
                    R.id.notification_bt_cancel, pendingIntentCancel);

            Uri alarmSound = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    BaseApplication.getContext())
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_disabled)
                    .setSound(alarmSound)
                    .setDefaults(
                            Notification.DEFAULT_LIGHTS
                                    | Notification.DEFAULT_VIBRATE)
                    .setContent(remoteViews);
            manager.notify(id, mBuilder.build());
        }
    }

}
