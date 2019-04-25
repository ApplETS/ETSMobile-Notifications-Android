package ca.etsmtl.applets.sample.ui.login;

import android.content.Context;

import com.securepreferences.SecurePreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import ca.etsmtl.applets.sample.data.LoginDataSource;
import ca.etsmtl.applets.sample.data.LoginRepository;
import ca.etsmtl.applets.sample.data.MonETSService;
import ca.etsmtl.applets.sample.data.TLSUtilities;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
            MonETSService monETSService = createMonETSService();

            return (T) new LoginViewModel(LoginRepository.getInstance(securePreferences,
                    new LoginDataSource(monETSService)));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

    private MonETSService createMonETSService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://portail.etsmtl.ca/api/")
                .client(TLSUtilities.createETSOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MonETSService.class);
    }
}
