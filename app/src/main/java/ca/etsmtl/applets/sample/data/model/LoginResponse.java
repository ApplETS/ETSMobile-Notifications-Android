package ca.etsmtl.applets.sample.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Sonphil on 24-04-19.
 */

public class LoginResponse {
    /** User name in the "ens\aa00000" format **/
    @SerializedName("Username")
    private String username;

    /** Domain name (ens or etsmtl) **/
    @SerializedName("Domaine")
    private String domain;

    /** User type id **/
    @SerializedName("TypeUsagerId")
    private int userTypeId;

    public String getUsername() {
        return username;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getUserTypeId() {
        return userTypeId;
    }
}
