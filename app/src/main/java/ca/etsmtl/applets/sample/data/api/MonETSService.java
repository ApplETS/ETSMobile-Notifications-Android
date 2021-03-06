package ca.etsmtl.applets.sample.data.api;

import androidx.lifecycle.LiveData;
import ca.etsmtl.applets.sample.data.api.util.ApiResponse;
import ca.etsmtl.applets.sample.data.model.LoginRequestBody;
import ca.etsmtl.applets.sample.data.model.LoginResponse;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Sonphil on 24-04-19.
 */

public interface MonETSService {
    @Headers({
            "Content-Type: application/json",
            "Cache-Control: no-cache"
    })
    @POST("authentification")
    LiveData<ApiResponse<LoginResponse>> login(@Body LoginRequestBody loginRequestBody);
}
