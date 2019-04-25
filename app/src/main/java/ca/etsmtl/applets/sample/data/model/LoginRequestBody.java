package ca.etsmtl.applets.sample.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sonphil on 24-04-19.
 */

public class LoginRequestBody {
    @SerializedName("Username")
    private String username;

    @SerializedName("Password")
    private String password;

    public LoginRequestBody(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
