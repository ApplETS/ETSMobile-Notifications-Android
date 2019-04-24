# ETSMobile-Notifications-Android

Android library that handle push notifications for ÉTSMobile

Check out the [wiki](https://github.com/ApplETS/ETSMobile-Notifications-Android/wiki) for details such as the device registration/retrievement process

### Installation
##### Add Firebase to your project

Follow the instructions here: https://firebase.google.com/docs/android/setup

##### Add the dependancy to your `build.gradle`
```gradle
dependencies {
    implementation 'TBD'
}
```

### Usage
##### Extend the `ETSFcmListenerService` class

You must override the `getEtsMobileNotificationManager` method which let the superclass retrieve an instance of your implementation of `EtsMobileNotificationManager`.

Optionally, you may also override the `notificationClickedIntent` and `notificationDismissedIntent` methods.
`notificationClickedIntent` let you supply a [PendingIntent](https://developer.android.com/training/notify-user/navigation) to send when a notification is clicked. `notificationDismissedIntent` let your supply a `PendingIntent` to send when a notification is dismissed.

<details><summary>Example from V2</summary>
  
```java
  public class AppETSFcmListenerService extends ETSFcmListenerService {

    private Gson gson = new Gson();

    @Override
    protected EtsMobileNotificationManager getEtsMobileNotificationManager() {
        SecurePreferences securePreferences = new SecurePreferences(getApplicationContext());

        return new EtsMobileNotificationManager() {
            @Override
            public void saveNewNotification(MonETSNotification newNotification, List<MonETSNotification> previousNotifications) {
                List<MonETSNotification> notificationsToSave = new ArrayList<>(previousNotifications);
                notificationsToSave.add(newNotification);

                securePreferences.edit()
                        .putString(Constants.RECEIVED_NOTIF, gson.toJson(notificationsToSave))
                        .commit();
            }

            @Override
            public List<MonETSNotification> getNotifications() {
                String notificationsStr = securePreferences.getString(Constants.RECEIVED_NOTIF, "");

                List<MonETSNotification> notifications = gson.fromJson(notificationsStr,
                        new TypeToken<ArrayList<MonETSNotification>>() {}.getType());

                return notifications == null ? new ArrayList<>() : notifications;
            }
        };
    }

    @Nullable
    @Override
    protected PendingIntent notificationClickedIntent(MonETSNotification monETSNotification) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getActivity(this, 0, intent, 0);
    }
}
```
</details>

##### Add your service to your `AndroidManifest` like so:
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <service
            android:name="YOUR SERVICE HERE">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
    </application>
</manifest>
```
##### Add AWS secrets to your `AndroidManifest` like so:
```xml
        <meta-data
            android:name="ca.etsmtl.applets.etsmobilenotifications.AWS_ACCESS_KEY"
            android:value="YOUR ACCESS KEY" />

        <meta-data
            android:name="ca.etsmtl.applets.etsmobilenotifications.AWS_SECRET_KEY"
            android:value="YOUR SECRET KEY" />

        <meta-data
            android:name="ca.etsmtl.applets.etsmobilenotifications.SNS_ARN"
            android:value="YOUR ARN" />
```

##### Login/logout
When the user has successfully logged in to MonÉTS, call `NotificationsLoginManager.login`.
<details><summary>Example from V2</summary>
  
```java
  public class AuthentificationPortailTask extends AsyncTask<String, Void, Intent> {

    private final AccountManager accountManager;
    private WeakReference<Activity> launchingActivityWeakRef;

    public AuthentificationPortailTask(Activity launchingActivity) {
        launchingActivityWeakRef = new WeakReference<>(launchingActivity);
        accountManager = AccountManager.get(launchingActivity);
    }

    protected Intent doInBackground(String... params) {
        if (launchingActivityWeakRef.get() == null) {
            return null;
        }

        OkHttpClient client = TLSUtilities.createETSOkHttpClient(launchingActivityWeakRef.get());
        String url = params[0], username = params[1], password = params[2];
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\n  \"Username\": \"" + username + "\",\n  \"Password\": \"" + password + "\"\n}");
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = null;
        String authCookie = "", domaine = "";
        int typeUsagerId = 0;

        final Intent res = new Intent();

        try {
            response = client.newCall(request).execute();

            if (response.code() == 200) {

                List<String> cookies = response.headers().values("Set-Cookie");

                for (String cookie : cookies) {
                    if (cookie.contains(Constants.MONETS_COOKIE_NAME)) {
                        authCookie = cookie;
                        break;
                    }
                }

                JSONObject jsonResponse = new JSONObject(response.body().string());

                typeUsagerId = jsonResponse.getInt("TypeUsagerId");
                domaine = jsonResponse.getString("Domaine");

                res.putExtra(AccountManager.KEY_AUTHTOKEN, authCookie);
                res.putExtra(Constants.TYPE_USAGER_ID, typeUsagerId);
                res.putExtra(Constants.DOMAINE, domaine);
            } else {
                Log.e("Erreur Portail", response.toString());
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);

        res.putExtra(Constants.PARAM_USER_PASS, password);


        return res;
    }

    protected void onPostExecute(Intent intent) {

        if (intent != null) {

            Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
            if (accounts.length > 0) {

                String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

                if (!TextUtils.isEmpty(authtoken)) {
                    int typeUsagerId = intent.getIntExtra(Constants.TYPE_USAGER_ID, -1);
                    String domaine = intent.getStringExtra(Constants.DOMAINE);

                    Activity launchingActivity = launchingActivityWeakRef.get();

                    if (launchingActivity != null) {
                        SecurePreferences securePreferences = new SecurePreferences(launchingActivity);
                        securePreferences.edit().putInt(Constants.TYPE_USAGER_ID, typeUsagerId).commit();
                        securePreferences.edit().putString(Constants.DOMAINE, domaine).commit();

                        securePreferences.edit().putString(Constants.EXP_DATE_COOKIE, domaine).commit();
                        ApplicationManager.domaine = domaine;
                        ApplicationManager.typeUsagerId = typeUsagerId;
                        accountManager.setAuthToken(accounts[0], Constants.AUTH_TOKEN_TYPE, authtoken);

                        Utility.saveCookieExpirationDate(authtoken, securePreferences);
                        NotificationsLoginManager.login(launchingActivity.getApplication(),
                                intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME), domaine);
                    } else {
                        return;
                    }
                }
            }

            Activity launchingActivity = launchingActivityWeakRef.get();

            if (launchingActivity != null) {
                launchingActivity.finish();
            }
        }
    }
}
```  
</details>

Finally, when the user has logged out, call `NotificationsLoginManager.logout`. You may also want to delete the notifications saved on the device.
