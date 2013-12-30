package com.example.office.lists.auth;

import com.microsoft.opentech.office.network.auth.SharePointCredentialsImpl;

/**
 * Stores credentials required to authenticate to Office 365 Online.
 */
public class SharePointCredentials extends SharePointCredentialsImpl {

    /**
     * Unique storage UUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Authentication type from {@link AuthType}.
     */
    private AuthType mAuthType = AuthType.UNDEFINED;

    /**
     * NTLM login.
     */
    private String mLogin;

    /**
     * NTLM login.
     */
    private String mPassword;

    /**
     * Basic constructor.
     */
    public SharePointCredentials() {
    }

    /**
     * {@inheritDoc}
     */
    public SharePointCredentials(String clientId, String clientSecret, String url, String redirectUrl, String domain) throws IllegalArgumentException {
        super(clientId, clientSecret, url, redirectUrl, domain);
    }

    /**
     * Resets current authentication state i.e. clears out access code and token.
     *
     * @return Updated credentials.
     */
    public SharePointCredentials reset() {
        setAccessCode(null);
        setToken(null);
        return this;
    }

    /**
     * @return Authentication type.
     */
    public AuthType getAuthType() {
        return mAuthType;
    }

    /**
     * Sets authentication type.
     *
     * @param type Authentication type. See {@link AuthType}.
     *
     * @return Updated credentials.
     */
    public SharePointCredentials setAuthType(AuthType type) {
        mAuthType = type;
        return this;
    }

    /**
     * @return the Login.
     */
    public String getLogin() {
        return mLogin;
    }

    /**
     * @param login The login to set.
     *
     * @return Updated credentials.
     */
    public SharePointCredentials setLogin(String login) {
        this.mLogin = login;
        return this;
    }

    /**
     * @return The password.
     */
    public String getPassword() {
        return mPassword;
    }

    /**
     * @param pass The pass to set.
     *
     * @return Updated credentials.
     */
    public SharePointCredentials setPassword(String pass) {
        this.mPassword = pass;
        return this;
    }
}
