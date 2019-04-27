package ca.etsmtl.applets.sample.ui.main;

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
 * ViewModel provider factory to instantiate MainViewModel.
 * Required given MainViewModel has a non-empty constructor
 */
public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    MainViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            SecurePreferences securePreferences = new SecurePreferences(context);
            MonETSService monETSService = MonETSServiceFactory.getMonETSService(context);

            return (T) new MainViewModel(LoginRepository.getInstance(context, securePreferences,
                    new LoginDataSource(monETSService)));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
