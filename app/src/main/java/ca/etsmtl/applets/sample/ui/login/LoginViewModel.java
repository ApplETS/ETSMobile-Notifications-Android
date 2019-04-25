package ca.etsmtl.applets.sample.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import ca.etsmtl.applets.sample.R;
import ca.etsmtl.applets.sample.data.LoginRepository;
import ca.etsmtl.applets.sample.data.Result;
import ca.etsmtl.applets.sample.data.model.LoggedInUser;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MediatorLiveData<LoginResult> loginResult = new MediatorLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        LiveData<Result<LoggedInUser>> loginSrc = loginRepository.login(username, password);

        loginResult.addSource(loginSrc, loggedInUserResult -> {
            if (loggedInUserResult instanceof Result.Success) {
                LoggedInUser data = ((Result.Success<LoggedInUser>) loggedInUserResult).getData();
                loginResult.postValue(new LoginResult(new LoggedInUserView(data.getUsername())));
            } else {
                loginResult.postValue(new LoginResult(R.string.login_failed));
            }

            loginResult.removeSource(loginSrc);
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }

        return !username.isEmpty() && username.matches("[a-zA-Z]{2}\\d{5}");
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && !password.trim().isEmpty();
    }
}
