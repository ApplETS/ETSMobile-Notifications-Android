package ca.etsmtl.applets.sample.service;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.Nullable;
import ca.etsmtl.applets.etsmobilenotifications.ETSFcmListenerService;
import ca.etsmtl.applets.etsmobilenotifications.MonETSNotification;
import ca.etsmtl.applets.sample.ui.main.MainActivity;


public class AppETSFcmListenerService extends ETSFcmListenerService {

    @Override
    protected void saveNewNotification(MonETSNotification newNotification) {
        // Not implemented in this example
    }

    @Nullable
    @Override
    protected PendingIntent notificationClickedIntent(MonETSNotification monETSNotification) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getActivity(this, 0, intent, 0);
    }
}
