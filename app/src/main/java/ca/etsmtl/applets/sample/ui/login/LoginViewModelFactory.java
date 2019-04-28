package ca.etsmtl.applets.sample.ui.login;

import android.content.Context;

import com.securepreferences.SecurePreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import ca.etsmtl.applets.sample.data.repository.LoginRepository;
import ca.etsmtl.applets.sample.data.api.LoginDataSource;
import ca.etsmtl.applets.sample.data.api.MonETSService;
import ca.etsmtl.applets.sample.data.api.MonETSServiceFactory;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public LoginViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            SecurePreferences securePreferences = new SecurePreferences(context);
            MonETSService monETSService = MonETSServiceFactory.getMonETSService(context);

            return (T) new LoginViewModel(LoginRepository.getInstance(context, securePreferences,
                    new LoginDataSource(monETSService)));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
