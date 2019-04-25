package ca.etsmtl.applets.sample.ui.main;

/**
 * Created by Sonphil on 25-04-19.
 */

class MainState {
    private boolean loginButtonVisible;
    private boolean universalCodeVisible;
    private String universalCodeText;
    private boolean domainVisible;
    private String domainText;

    public MainState(boolean userLoggedIn, String universalCodeText, String domainText) {
        this.loginButtonVisible = !userLoggedIn;
        this.universalCodeVisible = userLoggedIn;
        this.universalCodeText= universalCodeText;
        this.domainVisible = userLoggedIn;
        this.domainText = domainText;
    }

    public boolean isLoginButtonVisible() {
        return loginButtonVisible;
    }

    public boolean isUniversalCodeVisible() {
        return universalCodeVisible;
    }

    public String getUniversalCodeText() {
        return universalCodeText;
    }

    public boolean isDomainVisible() {
        return domainVisible;
    }

    public String getDomainText() {
        return domainText;
    }
}
