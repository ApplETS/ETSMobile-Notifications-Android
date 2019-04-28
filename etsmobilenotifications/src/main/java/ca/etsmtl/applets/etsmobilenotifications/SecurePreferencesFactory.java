package ca.etsmtl.applets.etsmobilenotifications;

import android.content.Context;

import com.securepreferences.SecurePreferences;

/**
 * Created by Sonphil on 27-04-19.
 */

final class SecurePreferencesFactory {
    private static final String PASSWORD = "PWETSMobileNotifications";
    private static final String FILE_NAME = "etsmobile_notifications_prefs";

    static SecurePreferences createSecurePreferences(Context context) {
        return new SecurePreferences(context, PASSWORD, FILE_NAME);
    }
}
