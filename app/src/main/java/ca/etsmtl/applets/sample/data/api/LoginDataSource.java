package ca.etsmtl.applets.sample.data.api;

import java.io.IOException;

import ca.etsmtl.applets.sample.data.Result;
import ca.etsmtl.applets.sample.data.model.LoggedInUser;
import ca.etsmtl.applets.sample.data.model.LoginRequestBody;
import ca.etsmtl.applets.sample.data.model.LoginResponse;
import retrofit2.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private MonETSService monETSService;

    public LoginDataSource(MonETSService monETSService) {
        this.monETSService = monETSService;
    }

    public Result<LoggedInUser> login(String username, String password) {

        try {
            Response<LoginResponse> response = monETSService
                    .login(new LoginRequestBody(username, password))
                    .execute();
            LoginResponse loginResponse = response.body();

            if (response.isSuccessful() && loginResponse != null) {
                LoggedInUser loggedInUser = new LoggedInUser(loginResponse.getUsername(),
                        loginResponse.getDomain());

                return new Result.Success<>(loggedInUser);
            } else {
                return new Result.Error(new Exception(response.errorBody().string()));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }
}
