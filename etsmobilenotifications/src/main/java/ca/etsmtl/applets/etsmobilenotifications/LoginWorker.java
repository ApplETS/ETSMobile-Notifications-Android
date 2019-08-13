package ca.etsmtl.applets.etsmobilenotifications;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.securepreferences.SecurePreferences;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Created by Sonphil on 13-08-19.
 */

public class LoginWorker extends Worker {
    static final String TAG = "LoginWorker";
    static final String USER_NAME_PARAM_KEY = "LoginWorkerUserName";
    static final String MON_ETS_DOMAINE_KEY = "LoginWorkerMonETSDomaine";

    public LoginWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = super.getApplicationContext();
        SecurePreferences securePreferences = SecurePreferencesFactory
                .createSecurePreferences(context);

        if (!securePreferences.getBoolean(Constants.USER_LOGGED_IN_PREF_KEY, false)) {
            if (areMetaDataValid(context)) {
                SecurePreferences.Editor editor = securePreferences.edit();

                Data data = getInputData();
                editor.putString(Constants.USER_NAME_PREF_KEY, data.getString(USER_NAME_PARAM_KEY ));
                editor.putString(Constants.MON_ETS_DOMAINE_PREF_KEY, data.getString(MON_ETS_DOMAINE_KEY));
                editor.putBoolean(Constants.USER_LOGGED_IN_PREF_KEY, true);
                editor.apply();

                try {
                    FcmRegistrationIntentService.enqueueWork(context, new Intent());
                } catch (Exception e) {
                    return Result.retry();
                }
            }
        }

        return Result.success();
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
}
