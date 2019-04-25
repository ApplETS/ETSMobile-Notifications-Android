package ca.etsmtl.applets.sample.ui.main;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;
import ca.etsmtl.applets.sample.data.LoginRepository;
import ca.etsmtl.applets.sample.data.model.LoggedInUser;

/**
 * Created by Sonphil on 25-04-19.
 */

public class MainViewModel extends ViewModel implements LifecycleObserver {

    private LoginRepository loginRepository;
    private MutableLiveData<MainState> mainState = new MutableLiveData<>();
    private MutableLiveData<Void> navigateToLogin = new MutableLiveData<>();

    MainViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public LiveData<MainState> getMainState() {
        return mainState;
    }

    public LiveData<Void> getNavigateToLogin() {
        return navigateToLogin;
    }

    public void clickLoginButton() {
        navigateToLogin.setValue(null);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void checkUserLoggedIn() {
        LoggedInUser loggedInUser = loginRepository.loadCachedLoggedInUser();
        MainState newMainState;

        if (loggedInUser == null) {
            newMainState = new MainState(false, "", "");
        } else {
            newMainState = new MainState(false, loggedInUser.getUsername(), loggedInUser.getDomain());
        }

        mainState.setValue(newMainState);
    }
}
