package ca.etsmtl.applets.etsmobilenotifications;

import android.content.Context;

import com.securepreferences.SecurePreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Created by Sonphil on 13-08-19.
 */

class LogoutWorker extends Worker {
    static final String TAG = "LogoutWorker";

    public LogoutWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Context context = super.getApplicationContext();
            ArnEndpointHandler handler = new ArnEndpointHandler(context, "", "");

            handler.deleteEndpoint();

            SecurePreferences.Editor editor = getPrefsEditor(context);
            editor.clear();
            editor.apply();

            editor.putBoolean(Constants.USER_LOGGED_IN_PREF_KEY, false);
            editor.apply();

            return Result.success();
        } catch (Exception e) {
            return Result.retry();
        }
    }

    private SecurePreferences.Editor getPrefsEditor(Context context) {
        SecurePreferences securePreferences = SecurePreferencesFactory.createSecurePreferences(context);

        return securePreferences.edit();
    }
}
