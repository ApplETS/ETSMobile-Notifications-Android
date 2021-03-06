# ETSMobile-Notifications-Android

[![Build Status](https://travis-ci.com/ApplETS/ETSMobile-Notifications-Android.svg?branch=master)](https://travis-ci.org/ApplETS/ETSMobile-Notifications-Android)[ ![Download](https://api.bintray.com/packages/clubapplets-server/NotreDame/etsmobilenotifications/images/download.svg) ](https://bintray.com/clubapplets-server/NotreDame/etsmobilenotifications/_latestVersion)


Android library that handle push notifications for ÉTSMobile

<img src="https://github.com/ApplETS/ETSMobile-Notifications-Android/blob/master/docs/images/screenshot.png" alt="screenshot" width="270"/>

### Installation
##### Add Firebase to your project

Follow the instructions here: https://firebase.google.com/docs/android/setup

##### Add the dependency to your `build.gradle`
```gradle
dependencies {
    implementation 'ca.applets.etsmobilenotifications:etsmobilenotifications:1.1.0'
}
```

### Usage
##### Extend the `ETSFcmListenerService` class

You must override the `saveNewNotification` method which will be called when a new notification is received.

Optionally, you may also override the following methods.
`notificationClickedIntent` let you supply a [PendingIntent](https://developer.android.com/training/notify-user/navigation) to send when a notification is tapped. 
`notificationDismissedIntent` let you supply a `PendingIntent` to send when a notification is dismissed.
`notificationChannelLabel` let you set the [NotificationChannel](https://developer.android.com/training/notify-user/channels)'s label for a given notification

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

### About
Originally, this was developed for the [second version of ÉTSMobile](https://github.com/ApplETS/ETSMobile-Android2). The [code](https://github.com/ApplETS/ETSMobile-Android2/pull/138) has been extracted from the project and modified, so that it can be reused in further versions of ÉTSMobile.

Check out the [wiki](https://github.com/ApplETS/ETSMobile-Notifications-Android/wiki) for more details such as the device registration/retrievement process
