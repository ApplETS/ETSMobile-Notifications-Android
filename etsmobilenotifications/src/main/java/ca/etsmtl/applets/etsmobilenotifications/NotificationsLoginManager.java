package ca.etsmtl.applets.etsmobilenotifications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.securepreferences.SecurePreferences;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;


public final class NotificationsLoginManager {
    private static final String TAG = "LoginManager";

    /**
     * Call this method when the user has logged in
     *
     * @param context {@link Context}
     * @param userName The user name (universal code)
     * @param monEtsDomaine MonÉTS' domain (ens or etsmtl)
     */
    public static void login(Context context, String userName, String monEtsDomaine) {
        if (areMetaDataValid(context)) {
            WorkManager workManager = WorkManager.getInstance(context);
            workManager.cancelAllWorkByTag(LogoutWorker.TAG);

            SecurePreferences.Editor editor = getPrefsEditor(context);

            editor.putString(Constants.USER_NAME_PREF_KEY, userName);
            editor.putString(Constants.MON_ETS_DOMAINE_PREF_KEY, monEtsDomaine);
            editor.putBoolean(Constants.USER_LOGGED_IN_PREF_KEY, true);
            editor.apply();

            FcmRegistrationIntentService.enqueueWork(context, new Intent());
        }
    }

    /**
     * Returns false if the ARN, access key or secret key hasn't been provided.
     *
     * @param context {@link Context}
     * @return True if the ARN, the access key and the secret key have been provided.
     */
    private static boolean areMetaDataValid(Context context) {
        String arn = MetaDataUtils.getValue(context, ".SNS_ARN");

        if (arn == null || arn.isEmpty()) {
            Log.e(TAG, "ARN is null or empty!");

            return false;
        }

        String accessKey = MetaDataUtils.getValue(context, ".AWS_ACCESS_KEY");

        if (accessKey == null || accessKey.isEmpty()) {
            Log.e(TAG, "Access key is null or empty!");

            return false;
        }

        String secretKey = MetaDataUtils.getValue(context, ".AWS_SECRET_KEY");

        if (secretKey == null || secretKey.isEmpty()) {
            Log.e(TAG, "Secret key is null or empty!");

            return false;
        }

        return true;
    }

    /**
     * Call this method when the user has logged out
     *
     * @param context {@link Context}
     */
    public static void logout(Context context) {
        WorkManager workManager = WorkManager.getInstance(context);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest deleteEndpointWorkRequest = new OneTimeWorkRequest
                .Builder(LogoutWorker.class)
                .setConstraints(constraints)
                .addTag(LogoutWorker.TAG)
                .build();

        workManager.enqueue(deleteEndpointWorkRequest);
    }

    private static SecurePreferences.Editor getPrefsEditor(Context context) {
        SecurePreferences securePreferences = SecurePreferencesFactory.createSecurePreferences(context);

        return securePreferences.edit();
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
