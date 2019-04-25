package ca.etsmtl.applets.sample.data;

import com.securepreferences.SecurePreferences;

import ca.etsmtl.applets.sample.data.api.LoginDataSource;
import ca.etsmtl.applets.sample.data.model.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static final String USERNAME_PREF_KEY = "ca.etsmtl.applets.sample.USERNAME_PREF_KEY";
    private static final String DOMAIN_PREF_KEY = "ca.etsmtl.applets.sample.DOMAIN_PREF_KEY";

    private static volatile LoginRepository instance;

    private final SecurePreferences securePreferences;
    private final LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(SecurePreferences securePreferences, LoginDataSource dataSource) {
        this.securePreferences = securePreferences;
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(SecurePreferences securePreferences,
                                              LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(securePreferences, dataSource);
        }
        return instance;
    }

    public LoggedInUser loadCachedLoggedInUser() {
        String userName = securePreferences.getString(USERNAME_PREF_KEY, null);
        String domain = securePreferences.getString(DOMAIN_PREF_KEY, null);

        if (userName == null || domain == null) {
            return null;
        } else {
            return new LoggedInUser(userName, domain);
        }
    }

    public void logout() {
        securePreferences.edit()
                .remove(USERNAME_PREF_KEY)
                .remove(DOMAIN_PREF_KEY)
                .apply();

        user = null;
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;

        securePreferences.edit()
                .putString(USERNAME_PREF_KEY, user.getUsername())
                .putString(DOMAIN_PREF_KEY, user.getDomain())
                .apply();
    }

    public Result<LoggedInUser> login(String username, String password) {
        // handle login
        Result<LoggedInUser> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }
}
