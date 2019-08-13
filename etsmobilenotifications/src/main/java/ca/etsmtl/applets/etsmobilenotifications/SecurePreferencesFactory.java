package ca.etsmtl.applets.etsmobilenotifications;

import android.content.Context;

import com.securepreferences.SecurePreferences;

/**
 * Created by Sonphil on 27-04-19.
 */

final class SecurePreferencesFactory {
    private static final String PASSWORD = "PWETSMobileNotifications";
    private static final String FILE_NAME = "etsmobile_notifications_prefs";

    /**
     * Creates a {@link SecurePreferences} that should be used to store information such as the
     * username and the domain
     *
     * This should not be used to store information that too sensitive such as a password.
     *
     * @param context {@link SecurePreferences}
     * @return A {@link SecurePreferences} that should be used to store information such as the
     * username and the domain
     */
    static SecurePreferences createSecurePreferences(Context context) {
        return new SecurePreferences(context, PASSWORD, FILE_NAME);
    }
}
