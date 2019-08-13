package ca.etsmtl.applets.etsmobilenotifications;

import android.content.Context;

import com.securepreferences.SecurePreferences;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;


public final class NotificationsLoginManager {

    /**
     * Call this method when the user has logged in
     *
     * @param context       {@link Context}
     * @param userName      The user name (universal code)
     * @param monEtsDomaine MonÉTS' domain (ens or etsmtl)
     */
    public static void login(Context context, String userName, String monEtsDomaine) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelAllWorkByTag(LogoutWorker.TAG);
        workManager.cancelAllWorkByTag(LoginWorker.TAG);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data inputData = new Data
                .Builder()
                .putString(LoginWorker.USER_NAME_PARAM_KEY, userName)
                .putString(LoginWorker.MON_ETS_DOMAINE_KEY, monEtsDomaine)
                .build();
        OneTimeWorkRequest loginWorkRequest = new OneTimeWorkRequest
                .Builder(LoginWorker.class)
                .setConstraints(constraints)
                .addTag(LoginWorker.TAG)
                .setInputData(inputData)
                .build();

        workManager.enqueue(loginWorkRequest);
    }

    /**
     * Call this method when the user has logged out
     *
     * @param context {@link Context}
     */
    public static void logout(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelAllWorkByTag(LoginWorker.TAG);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest logoutWorkRequest = new OneTimeWorkRequest
                .Builder(LogoutWorker.class)
                .setConstraints(constraints)
                .addTag(LogoutWorker.TAG)
                .build();

        workManager.enqueue(logoutWorkRequest);
    }

    /**
     * Returns true if the user is logged in
     *
     * @param context {@link Context}
     * @return True if the user is logged in
     */
    public static boolean isUserLoggedIn(Context context) {
        SecurePreferences securePreferences = SecurePreferencesFactory.createSecurePreferences(context);

        return securePreferences.getBoolean(Constants.USER_LOGGED_IN_PREF_KEY, false);
    }

    /**
     * Get the user name (universal code)
     *
     * @return The user name
     */
    static String getUserName(Context context) {
        SecurePreferences securePreferences = SecurePreferencesFactory.createSecurePreferences(context);

        return securePreferences.getString(Constants.USER_NAME_PREF_KEY, null);
    }

    /**
     * Get MonETS' domain (ens or etsmtl)
     *
     * @return MonETS' domain (ens or etsmtl)
     */
    static String getMonEtsDomaine(Context context) {
        SecurePreferences securePreferences = SecurePreferencesFactory.createSecurePreferences(context);

        return securePreferences.getString(Constants.MON_ETS_DOMAINE_PREF_KEY, null);
    }
}
