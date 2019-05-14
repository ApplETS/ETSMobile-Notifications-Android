package ca.etsmtl.applets.etsmobilenotifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by gnut3ll4 on 16/10/15.
 */
public abstract class ETSFcmListenerService extends FirebaseMessagingService {

    /**
     * Called when message is received.
     *
     * @param message containing the data from the sender and from the message itself
     *                (Bundle containing message data as key/value pairs.
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {
        Map<String, String> data = message.getData();

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(data);
    }

    @Override
    public void onNewToken(String token) {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        super.onNewToken(token);
        FcmRegistrationIntentService.enqueueWork(this, new Intent());
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param data FCM message received.
     */
    private void sendNotification(Map<String, String> data) {
        MonETSNotification newMonETSNotification = getMonETSNotificationFromMap(data);

        if (newMonETSNotification != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupChannel();
            }

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notifyNotifications(notificationManager, newMonETSNotification);

            saveNewNotification(newMonETSNotification);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            NotificationChannel channel = notificationManager
                    .getNotificationChannel(Constants.DEFAULT_NOTIFICATION_CHANNEL_ID);

            if (channel == null) {
                // We could create multiple channels based on the notification but let's just create one for maintenance purposes.
                String channelName = getString(R.string.fcm_fallback_notification_channel_label);
                channel = new NotificationChannel(Constants.DEFAULT_NOTIFICATION_CHANNEL_ID,
                        channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void notifyNotifications(NotificationManagerCompat notificationManager,
                                     MonETSNotification monETSNotification) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                Constants.DEFAULT_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_ets_logo_blanc)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setColor(ContextCompat.getColor(this, R.color.ets_red))
                .setContentTitle(monETSNotification.getNotificationApplicationNom())
                .setContentText(monETSNotification.getNotificationTexte())
                .setAutoCancel(true);

        PendingIntent contentIntent = notificationClickedIntent(monETSNotification);

        if (contentIntent != null) {
            notificationBuilder.setContentIntent(contentIntent);
        }

        PendingIntent deleteIntent = notificationDismissedIntent(monETSNotification);

        if (deleteIntent != null) {
            notificationBuilder.setDeleteIntent(deleteIntent);
        }

        notificationManager.notify(monETSNotification.getId(), notificationBuilder.build());
    }

    private MonETSNotification getMonETSNotificationFromMap(Map<String, String> data) {
        String idStr = data.get("Id");

        if (idStr == null) {
            return null;
        }

        int id = Integer.valueOf(idStr);
        String idDossierStr = data.get("IdDossier");
        int idDossier = idDossierStr == null ? 0 : Integer.valueOf(idDossierStr);
        String notificationTexte = data.get("NotificationTexte");
        String notificationDateCreation = data.get("NotificationDateCreation");
        String notificationDateDebutAffichage = data.get("NotificationDateDebutAffichage");
        String notificationApplicationNom = data.get("NotificationApplicationNom");
        String notificationData = data.get("NotificationData");
        String url = data.get("Url");

        return new MonETSNotification(
                id,
                idDossier,
                notificationTexte,
                notificationDateCreation,
                notificationDateDebutAffichage,
                notificationApplicationNom,
                notificationData,
                url);
    }

    /**
     * Save new notification to device
     *
     * @param newNotification New notification to save
     */
    protected abstract void saveNewNotification(MonETSNotification newNotification);

    /**
     * Returns {@link PendingIntent} to execute when the user tap on the notification
     *
     * @param monETSNotification Tapped notification
     * @return {@link PendingIntent} to execute when the user tap on a notification
     * @see <a href="https://developer.android.com/training/notify-user/navigation">
     * https://developer.android.com/training/notify-user/navigation
     * </a>
     */
    @Nullable
    protected PendingIntent notificationClickedIntent(MonETSNotification monETSNotification) {
        return null;
    }

    /**
     * Returns {@link PendingIntent} to execute when the notification is explicitly dismissed by the
     * user, either with the "Clear All" button or by swiping it away individually.
     *
     * @param monETSNotification Dimissed notification
     * @return {@link PendingIntent} to execute when the notification is explicitly dismissed by the
     * user, either with the "Clear All" button or by swiping it away individually.
     */
    @Nullable
    protected PendingIntent notificationDismissedIntent(MonETSNotification monETSNotification) {
        return null;
    }
}