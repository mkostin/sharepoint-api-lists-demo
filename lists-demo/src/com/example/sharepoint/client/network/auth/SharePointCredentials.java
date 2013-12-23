package com.example.sharepoint.client.network.auth;

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

    public SharePointCredentials() {
    }

    public SharePointCredentials(String clientId, String clientSecret, String url, String redirectUrl, String domain) throws IllegalArgumentException {
        super(clientId, clientSecret, url, redirectUrl, domain);
    }

    public AuthType getAuthType() {
        return mAuthType;
    }

    public void setAuthType(AuthType type) {
        mAuthType = type;
    }
}
