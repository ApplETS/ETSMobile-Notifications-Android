package ca.etsmtl.applets.sample.data.api;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import ca.etsmtl.applets.sample.data.Result;
import ca.etsmtl.applets.sample.data.api.util.ApiResponse;
import ca.etsmtl.applets.sample.data.model.LoggedInUser;
import ca.etsmtl.applets.sample.data.model.LoginRequestBody;
import ca.etsmtl.applets.sample.data.model.LoginResponse;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private MonETSService monETSService;

    public LoginDataSource(MonETSService monETSService) {
        this.monETSService = monETSService;
    }

    public LiveData<Result<LoggedInUser>> login(String username, String password) {

        LiveData<ApiResponse<LoginResponse>> apiSrc = monETSService
                .login(new LoginRequestBody(username, password));

        return Transformations.map(apiSrc, (Function<ApiResponse<LoginResponse>, Result<LoggedInUser>>) apiResponse -> {
            LoginResponse loginResponse = apiResponse.body;

            if (apiResponse.isSuccessful() && loginResponse != null) {
                LoggedInUser loggedInUser = new LoggedInUser(loginResponse.getUsername(),
                        loginResponse.getDomain());

                return new Result.Success<>(loggedInUser);
            } else {
                return new Result.Error(new Exception(apiResponse.errorMessage));
            }
        });
    }
}
